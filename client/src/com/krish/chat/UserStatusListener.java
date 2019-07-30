package com.krish.chat;

/**
 * Interface created by Krish
 */

public interface UserStatusListener {

     void online(String login);
     void offline(String login);

}
