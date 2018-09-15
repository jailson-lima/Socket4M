package events4j.argument;

import lombok.Builder;
import lombok.Data;

/**
 * Event Argument. An event argument has a name to identify it and a value.
 * A value can be any Java type. the value must be immutable to avoid side effect.
 * @param <V> the type of argument value
 */
@Data
@Builder
public final class Argument<V> {

    private final String name;
    private final V value;

    private Argument(String name, V value) {
        this.name = name;
        this.value = value;
    }

    public static <V> Argument of(String name, V value){
        return builder().name(name).value(value).build();
    };
}
