package com.uni.buildorder;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class Order {

    BiPredicate<ObservationInterface, ActionInterface> postCondition;
    BiConsumer<ObservationInterface, ActionInterface> action;

    public Order(BiConsumer<ObservationInterface, ActionInterface> action,
                 BiPredicate<ObservationInterface, ActionInterface> postCondition) {
        this.action = action;
        this.postCondition = postCondition;
    }
}
