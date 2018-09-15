package me.devnatan.socket4m.message;

import com.google.gson.GsonBuilder;
import lombok.Data;

import java.util.Map;

@Data
public class Message {

    private Map<?, ?> content;

    public Message() {}

    public Message(Map<?, ?> content) {
        this.content = content;
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
        return builder.create().fromJson(s, Message.class);
    }

}
