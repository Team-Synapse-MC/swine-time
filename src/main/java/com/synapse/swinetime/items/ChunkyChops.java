package com.synapse.swinetime.items;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class ChunkyChops extends Item {
    public ChunkyChops() {
        super(new Item.Properties().food(
                new FoodProperties.Builder()
                        .nutrition(4)
                        .saturationMod(0.5f)
                        .alwaysEat()
                        .build()));
    }
}
