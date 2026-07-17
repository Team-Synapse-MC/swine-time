package com.synapse.swinetime.items;

import com.synapse.swinetime.SwineTimeMod;
import com.synapse.swinetime.entities.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SwineTimeMod.MODID);

    public static final RegistryObject<Item> DIRE_BOAR_SPAWN_EGG = ITEMS.register("dire_boar_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.DIRE_BOAR, 0x969090, 0x9d9382, new Item.Properties()));

    public static final RegistryObject<Item> CHUNKY_CHOPS = ITEMS.register("chunky_chops",
            ChunkyChops::new);

    public static final RegistryObject<Item> SEARED_CHOPS = ITEMS.register("seared_chops",
            SearedChops::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
