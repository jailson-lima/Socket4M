package me.devnatan.socket4m.client.message;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class Message {

    @Getter @Setter private String text;
    @Getter @Setter private Map<Object, Object> values;

    public Message() {}

    /**
     * Serialize the message object to a string in JSON.
     * @return = serialized message
     */
    public String toJson() {
        if(values == null) return null;
        try {
            return new GsonBuilder().create().toJson(values, Map.class);
        } catch (com.google.gson.JsonSyntaxException e) {
            e.printStackTrace();
        } return null;
    }

    /**
     * Constructs a message object from a flat JSON message,
     * generally used to receive messages from the server.
     * @param s = the JSON string
     * @return Message
     */
    public static Message fromJson(String s) {
        Message message = new Message();

        try {
            message.setValues(new HashMap<>());
            message.getValues().putAll(new GsonBuilder().create().fromJson(s, Map.class));
        } catch (com.google.gson.JsonSyntaxException e) {
            e.printStackTrace();
        }

        return message;
    }

    public static <K, V> Message fromMap(Map<K, V> map) {
        Message message = new Message();
        message.setValues((Map<Object, Object>) map);
        message.setText(message.toJson());

        return message;
    }
}
