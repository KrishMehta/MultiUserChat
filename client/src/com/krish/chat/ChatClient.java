package com.krish.chat;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Class created by Krish
 */

public class ChatClient {

    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8818);

        client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("Online: " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("Offline: " + login);
            }
        });
        client.addMessageListener((sender, body) -> System.out.println("You received a message from " + sender + ": " + body));

        if (!client.connect()) {
            System.err.println("Connection failed");
        } else {
            System.out.println("Connection successful");
            if (client.login("Krish", "admin")) {
                System.out.println("Login successful");
                client.msg("Guest", "Hi there!");
            } else {
                System.err.println("Login failed");
            }
//            client.logoff();
        }
    }

    public boolean connect() {
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Client port: " + socket.getLocalPort());
            serverIn = socket.getInputStream();
            serverOut = socket.getOutputStream();
            bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        System.out.println("Response line: " + response);

        if (response.equals("success")) {
            startMessageReader();
            return true;
        } else {
            return false;
        }
    }

    public void msg(String receiver, String body) throws IOException {
        String cmd = "msg " + receiver + " " + body + "\n";
        serverOut.write(cmd.getBytes());
    }

    public void logoff() throws IOException {
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
    }

    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }

    public void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

    private void startMessageReader() {
        new Thread(this::readMessageLoop).start();
    }

    private void readMessageLoop() {
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                String[] tokens = line.split("\\s");
                if (tokens.length > 0) {
                    String cmd = tokens[0];
                    if (cmd.equalsIgnoreCase("online")) {
                        handleOnline(tokens);
                    } else if (cmd.equalsIgnoreCase("offline")) {
                        handleOffline(tokens);
                    } else if (tokens[1].equals("->")) {
                        handleMessage(tokens);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners) {
            listener.online(login);
        }
    }

    private void handleMessage(String[] tokens) {
        if (!tokens[2].equals("you:")) return;
        String login = tokens[0];
        StringBuilder body = new StringBuilder();
        for (int i = 3; i < tokens.length; i++) {
            body.append(tokens[i]).append(" ");
        }

        for (MessageListener listener : messageListeners) {
            listener.onMessage(login, body.toString());
        }
    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners) {
            listener.offline(login);
        }
    }

}