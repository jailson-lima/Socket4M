package it.shadow.events4j.argument;

/**
 * Event Argument. An event argument has a name to identify it and a value.
 * A value can be any Java type. the value must be immutable to avoid side effect.
 * @param <V> the type of argument value
 */
public final class Argument<V> {

    private final String name;
    private final V value;

    private Argument(String name, V value) {
        this.name = name;
        this.value = value;
    }

    public static <V> Argument of(String name, V value){
        return newBuilder().setName(name).setValue(value).build();
    };

    public V getValue(){
        return value;
    }

    public String getName() {
        return name;
    }


    public static Builder newBuilder() {
        return new Builder();
    }


    public static class Builder<V>{

        private String name;
        private V value;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setValue(V value) {
            this.value = value;
            return this;
        }

        public Argument<V> build() {
            return new Argument<>(this.name,this.value);
        }
    }

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
