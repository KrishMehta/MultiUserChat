package com.krish.chat;

/**
 * Class created by Krish
 */

public class ServerMain {

    public static void main(String[] args) {
        Server server = new Server(8818);
        server.start();
    }

}
