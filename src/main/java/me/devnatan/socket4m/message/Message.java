package me.devnatan.socket4m.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

@Builder
public class Message {

    @Getter @Setter private String text;
    @Getter private final Map<String, Object> values = new LinkedHashMap<>();

    public Message() {}

    public Message(String text) {
        this.text = text;
    }

    /**
     * Serialize the message object to a string in JSON.
     * @return = serialized message
     */
    public String json() {
        if(values.isEmpty()) return text;
        try {
            Gson gson = new GsonBuilder().create();
            Type type = new TypeToken<Map>() { }.getType();

            return gson.toJson(values, type);
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
}
