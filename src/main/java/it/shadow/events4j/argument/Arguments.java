package it.shadow.events4j.argument;

import lombok.Getter;

import java.util.*;

/**
 * Event Arguments.The argument of an event.
 */
public final class Arguments {

    @Getter
    private final List<Argument> arguments;

    private Arguments(List<Argument> arguments) {
        this.arguments = Collections.unmodifiableList(arguments);
    }

    /**
     * Return {{@link Argument}} where name is {argId}. Return null if
     * argument not exist
     * @param name argument name used to identify argument
     */
    private Argument get(String name) {
        return arguments.stream()
                .filter(a -> a.getName().equals(name))
                .findFirst().<NoSuchElementException>orElseThrow(() -> {
                    throw new NoSuchElementException("Cannot find argument " + name);
                });
    }

    public Object value(String name) {
        return arguments.stream()
                .filter(Objects::nonNull)
                .filter(argument -> argument.getName().equals(name))
                .findFirst().<NoSuchElementException>orElseThrow(() -> {
                    throw new NoSuchElementException("Cannot fetch value for argument " + name);
                }).getValue();
    }

    /**
     * Return the number of argument
     */
    public int length() {
        return arguments.size();
    }

    static Builder builder(){
        return new Builder();
    }

    public static class Builder {

        private List<Argument> arguments = new ArrayList<>();

        public Builder with(Argument argument) {
            arguments.add(argument);
            return this;
        }

        public Builder with(String key, Object value) {
            return with(Argument.of(key, value));
        }

        public Arguments build(){
            return new Arguments(arguments);
        }
    }
}
