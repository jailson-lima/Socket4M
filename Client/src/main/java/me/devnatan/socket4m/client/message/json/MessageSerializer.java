package me.devnatan.socket4m.client.message.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import me.devnatan.socket4m.client.message.Message;

import java.lang.reflect.Type;

public class MessageSerializer implements JsonSerializer<Message> {

    public JsonElement serialize(Message src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src);
    }

}
