package me.devnatan.socket4m.client.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class Message extends HashMap<String, Object> {

    private String[] keys;
    private Object[] values;

    private Message() {}

    private Message(String[] keys, Object... values) {
        if(keys == null)
            throw new NullPointerException("Socket message keys cannot be null");
        if(values == null)
            throw new NullPointerException("Socket message values cannot be null");

        for(int i = 0; i < Math.min(keys.length, values.length); i++) {
            this.put(keys[i], values[i]);
        }
    }

    /**
     * Serialize the message object to a string in JSON.
     * @return = serialized message
     */
    public String to() {
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<Message>() { }.getType();

        return gson.toJson(this, type);
    }

    /**
     * Constructs a message object from a flat JSON message,
     * generally used to receive messages from the server.
     * @param s = the JSON string
     * @return Message
     */
    public static Message from(String s) {
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<Message>() { }.getType();

        return gson.fromJson(s, type);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String[] keys;
        private Object[] values;

        public Message.Builder keys(String... keys) {
            this.keys = keys;
            return this;
        }

        public Message.Builder values(Object... values) {
            this.values = values;
            return this;
        }

        public Message build() {
            return new Message(this.keys, this.values);
        }
    }
}
