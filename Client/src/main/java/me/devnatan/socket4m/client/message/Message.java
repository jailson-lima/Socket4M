package me.devnatan.socket4m.client.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class Message {

    private final Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .disableHtmlEscaping()
            .create();
    private final Map<String, Object> content;

    /**
     * Serialize the message object to a string in JSON.
     * @return = serialized message
     */
    public String toJson() {
        return gson.toJson(content);
    }

    /**
     * Constructs a message object from a flat JSON message,
     * generally used to receive messages from the server.
     * @param s = the JSON string
     * @return Message
     */
    public static Message fromJson(String s) {
        try {
            return new Message(new GsonBuilder().create().fromJson(s, Map.class));
        } catch (JsonSyntaxException e) {
            return Message.builder()
                    .with("text", s)
                    .build();
        }
    }

    /**
     * Create a message builder so that messages are easily created from it.
     * @return Builder
     */
    public static Message.Builder builder() {
        return new Builder();
    }

    /**
     * Self-explanatory
     */
    public static class Builder {

        private Map<String, Object> values;

        private Builder() {}

        /**
         * Adds a key and value to the list of values of the message to be constructed.
         * @param k = the key
         * @param v = the value
         * @return this
         */
        public Builder with(String k, Object v) {
            if(values == null)
                values = new LinkedHashMap<>();
            values.put(k, v);
            return this;
        }

        /**
         * Constructs a message from the elements assigned to this constructor.
         * @return Message
         */
        public Message build() {
            return new Message(values);
        }

    }

}
