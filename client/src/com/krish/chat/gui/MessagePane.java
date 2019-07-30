package com.krish.chat.gui;

import com.krish.chat.ChatClient;
import com.krish.chat.MessageListener;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Class created by Krish
 */

public class MessagePane extends JPanel implements MessageListener {

    private final ChatClient client;
    private final String login;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messageList = new JList<>(listModel);
    private JTextField inputField = new JTextField();

    MessagePane(ChatClient client, String login) {
        this.client = client;
        this.login = login;

        client.addMessageListener(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(event -> {
            try {
                String text = inputField.getText();
                client.msg(login, text);
                listModel.addElement(text);
                inputField.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onMessage(String sender, String body) {
        if (login.equalsIgnoreCase(sender)) {
            String line = sender + ": " + body;
            listModel.addElement(line);
        }
    }

}
