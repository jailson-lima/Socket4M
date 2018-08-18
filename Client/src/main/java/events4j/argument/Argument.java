package events4j.argument;

import lombok.Builder;
import lombok.Getter;

/**
 * Event Argument. An event argument has a name to identify it and a value.
 * A value can be any Java type. the value must be immutable to avoid side effect.
 * @param <V> the type of argument value
 */
@Builder
public final class Argument<V> {

    @Getter private final String name;
    @Getter private final V value;

    private Argument(String name, V value) {
        this.name = name;
        this.value = value;
    }

    public static <V> Argument of(String name, V value){
        return builder().name(name).value(value).build();
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Argument<?> argument = (Argument<?>) o;

        if (name != null ? !name.equals(argument.name) : argument.name != null) return false;
        return value != null ? value.equals(argument.value) : argument.value == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
