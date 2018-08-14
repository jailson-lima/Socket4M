package it.shadow.events4j.listener;

import it.shadow.events4j.argument.Arguments;

@FunctionalInterface
public interface EventListener {

    void call(Arguments args);

}
