package com.uni.buildorder;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class BuildOrder {

    private final Queue<Order> orders;
    private boolean isVerifiedStep;
    private final Map<BuildOrderTag, Integer> tags;
    private int currentIndex;

    private BuildOrder(Queue<Order> orders, Map<BuildOrderTag, Integer> tags) {
        this.orders = orders;
        isVerifiedStep = false;
        this.tags = tags;
        currentIndex = 0;
    }

    public static BuildOrderBuilder builder() {
        return new BuildOrderBuilder();
    }

    public void execute(ObservationInterface observation, ActionInterface actions) {
        if (!orders.isEmpty()) {
            Order currentOrder = orders.peek();

            isVerifiedStep = !isVerifiedStep;

            if (isVerifiedStep) {
                currentOrder.action.accept(observation, actions);
            }

            if (!isVerifiedStep && currentOrder.postCondition.test(observation, actions)) {
                orders.poll();
                currentIndex++;
            }
        }
    }

    public boolean isReadyFor(BuildOrderTag tag) {
        return currentIndex > tags.get(tag);
    }

    public boolean isBuildOrderFinished() {
        return orders.isEmpty();
    }

    public static class BuildOrderBuilder {

        private final Queue<Order> orders;
        private final Map<BuildOrderTag, Integer> tags;

        public BuildOrderBuilder() {
            orders = new LinkedList<>();
            tags = new HashMap<>();
        }

        public BuildOrderBuilder inQueue(BiConsumer<ObservationInterface, ActionInterface> action,
                                         BiPredicate<ObservationInterface, ActionInterface> postCondition) {
            this.orders.add(new Order(action, postCondition));
            return this;
        }

        public BuildOrderBuilder addTag(BuildOrderTag tag, int indexOfStep) {
            this.tags.put(tag, indexOfStep);
            return this;
        }

        public BuildOrder build() {
            return new BuildOrder(orders, tags);
        }
    }
}
