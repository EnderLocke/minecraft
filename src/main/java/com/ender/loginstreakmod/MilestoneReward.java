package com.ender.loginstreakmod;

import java.util.List;

public class MilestoneReward {
    public final String message;
    public final List<ItemReward> items;

    public MilestoneReward(String message, List<ItemReward> items) {
        this.message = message;
        this.items = items;
    }

    public static class ItemReward {
        public final String id;
        public final int count;

        public ItemReward(String id, int count) {
            this.id = id;
            this.count = count;
        }
    }
}
