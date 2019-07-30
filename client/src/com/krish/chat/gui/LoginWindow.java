package com.krish.chat.gui;

import com.krish.chat.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Class created by Krish
 */

public class LoginWindow extends JFrame {

    private final ChatClient client;
    private JTextField loginField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JButton loginButton = new JButton("Login");

    private LoginWindow() {
        super("Login");

        this.client = new ChatClient("localhost", 8818);
        client.connect();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(loginField);
        panel.add(passwordField);
        panel.add(loginButton);

        loginButton.addActionListener(e -> login());

        getContentPane().add(panel, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    private void login() {
        String login = loginField.getText();
        String password = passwordField.getText();

        try {
            if (client.login(login, password)) {
                // bring up the user list window
                UserListPane pane = new UserListPane(client);

                JFrame frame = new JFrame("User List");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 600);
                frame.getContentPane().add(pane, BorderLayout.CENTER);
                frame.setVisible(true);
                setVisible(false);
            } else {
                // show error message
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.setVisible(true);
    }

}
