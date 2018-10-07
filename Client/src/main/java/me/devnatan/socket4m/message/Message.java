package me.devnatan.socket4m.message;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class Message<K, V> {

    private final Map<K, V> content;

    public Message(Map<K, V> content) {
        this.content = content;
    }

    public Message(K k, V v) {
        content = new LinkedHashMap<>();
        content.put(k, v);
    }

    public Message(K k1, V v1, K k2, V v2) {
        content = new LinkedHashMap<>();
        content.put(k1, v1);
        content.put(k2, v2);
    }

    public Message(K k1, V v1, K k2, V v2, K k3, V v3) {
        content = new LinkedHashMap<>();
        content.put(k1, v1);
        content.put(k2, v2);
        content.put(k3, v3);
    }

    public Message(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        content = new LinkedHashMap<>();
        content.put(k1, v1);
        content.put(k2, v2);
        content.put(k3, v3);
        content.put(k4, v4);
    }

    public Message(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        content = new LinkedHashMap<>();
        content.put(k1, v1);
        content.put(k2, v2);
        content.put(k3, v3);
        content.put(k4, v4);
        content.put(k5, v5);
    }

    /**
     * Serialize the message object to a string in JSON.
     * @return = serialized message
     */
    public String toJson() {
        GsonBuilder builder = new GsonBuilder();
        return builder.create().toJson(this);
    }

    /**
     * Constructs a message object from a flat JSON message,
     * generally used to receive messages from the server.
     * @param s = the JSON string
     * @return Message
     */
    public static Message fromJson(String s) {
        GsonBuilder builder = new GsonBuilder();
        try {
            return builder.create().fromJson(s, Message.class);
        } catch (JsonSyntaxException e) {
            return new Message<>("text", s);
        }
    }

}
