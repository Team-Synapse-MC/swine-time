package com.synapse.swinetime.items;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class SearedChops extends Item {
    public SearedChops() {
        super(new Item.Properties().food(
                new FoodProperties.Builder()
                        .nutrition(10)
                        .saturationMod(0.8f)
                        .alwaysEat()
                        .build()));
    }
}
