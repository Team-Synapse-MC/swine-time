package com.synapse.swinetime.entities;

import com.synapse.swinetime.SwineTimeMod;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SwineTimeMod.MODID);

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
