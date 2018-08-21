package me.devnatan.socket4m.client.message;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import me.devnatan.socket4m.client.message.json.MessageDeserializer;
import me.devnatan.socket4m.client.message.json.MessageSerializer;

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

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Message.class, new MessageDeserializer());
        builder.registerTypeAdapter(Message.class, new MessageSerializer());

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
        builder.registerTypeAdapter(Message.class, new MessageDeserializer());
        builder.registerTypeAdapter(Message.class, new MessageSerializer());

        return builder.create().fromJson(s, Message.class);
    }

    public static <K, V> Message fromMap(Map<K, V> map) {
        Message message = new Message();
        message.setValues((Map<Object, Object>) map);
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
