package com.ashik619.nowplaying.models;

/**
 * mesage pojo
 */
public class Message {

    /** Property username */
    String username;

    /** Property text */
    String text;

    /**
     * Constructor
     */
    public Message() {
    }

    /**
     * Gets the username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the username
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the text
     */
    public String getText() {
        return this.text;
    }

    /**
     * Sets the text
     */
    public void setText(String value) {
        this.text = value;
    }
}
