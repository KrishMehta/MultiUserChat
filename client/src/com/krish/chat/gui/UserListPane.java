package com.krish.chat.gui;

import com.krish.chat.ChatClient;
import com.krish.chat.UserStatusListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * Class created by Krish
 */

public class UserListPane extends JPanel implements UserStatusListener {

    private final ChatClient client;
    private JList<String> userListUI;
    private DefaultListModel<String> userListModel;

    UserListPane(ChatClient client) {
        this.client = client;
        client.addUserStatusListener(this);

        userListUI = new JList<>();
        userListUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    String login = userListUI.getSelectedValue();
                    MessagePane messagePane = new MessagePane(client, login);

                    JFrame frame = new JFrame("Message: " + login);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setSize(500, 500);
                    frame.getContentPane().add(messagePane, BorderLayout.CENTER);
                    frame.setVisible(true);
                }
            }
        });
        setLayout(new BorderLayout());
        add(new JScrollPane(userListUI), BorderLayout.CENTER);

        userListModel = new DefaultListModel<>();
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient("localhost", 8818);
        UserListPane pane = new UserListPane(client);

        JFrame frame = new JFrame("User List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.getContentPane().add(pane, BorderLayout.CENTER);
        frame.setVisible(true);

        if (client.connect()) {
            try {
                client.login("Krish", "admin");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void online(String login) {
        userListModel.addElement(login);
    }

    @Override
    public void offline(String login) {
        userListModel.removeElement(login);
    }

}
