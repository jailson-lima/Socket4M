package events4j.listener;

import events4j.argument.Arguments;

@FunctionalInterface
public interface EventListener {

    void call(Arguments args);

}
