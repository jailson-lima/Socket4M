package me.devnatan.socket4m.client.message;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class Message {

    @Getter @Setter private String text;
    @Getter @Setter private Map<?, ?> values;

    public Message() {}

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
        return builder.create().fromJson(s, Message.class);
    }

    public static <K, V> Message fromMap(Map<K, V> map) {
        Message message = new Message();
        message.setValues(map);
        message.setText(message.toJson());

        return message;
    }

    public boolean equals(Object other) {
        if(!(other instanceof Message))
            return false;

        Message otherMessage = (Message) other;
        return otherMessage.text.equals(text) && otherMessage.getValues().equals(values);
    }
}
