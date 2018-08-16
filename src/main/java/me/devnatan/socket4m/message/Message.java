package me.devnatan.socket4m.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class Message {

    private String text;
    private final Map<String, Object> values;

    private Message() {
        values = new LinkedHashMap<>();
    }

    private Message(String[] keys, Object... values) {
        this();
        if(keys == null)
            throw new NullPointerException("Socket message keys cannot be null");
        if(values == null)
            throw new NullPointerException("Socket message values cannot be null");

        for(int i = 0; i < Math.min(keys.length, values.length); i++) {
            this.values.put(keys[i], values[i]);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    /**
     * Serialize the message object to a string in JSON.
     * @return = serialized message
     */
    public String json() {
        try {
            Gson gson = new GsonBuilder().create();
            Type type = new TypeToken<Map>() { }.getType();

            return gson.toJson(this.values, type);
        } catch (com.google.gson.JsonSyntaxException e) {
            return text;
        }
    }

    /**
     * Constructs a message object from a flat JSON message,
     * generally used to receive messages from the server.
     * @param s = the JSON string
     * @return Message
     */
    public static Message from(String s) {
        Message message = new Message();
        message.setText(s);

        try {
            Gson gson = new GsonBuilder().create();
            Type type = new TypeToken<Map>() { }.getType();

            message.getValues().putAll(gson.fromJson(s, type));
        } catch (com.google.gson.JsonSyntaxException ignored) { }

        return message;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String text;
        private String[] keys;
        private Object[] values;

        public Message.Builder text(String text) {
            this.text = text;
            return this;
        }

        public Message.Builder append(String text) {
            if(this.text == null) text(text);
            else this.text += text;

            return this;
        }

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
