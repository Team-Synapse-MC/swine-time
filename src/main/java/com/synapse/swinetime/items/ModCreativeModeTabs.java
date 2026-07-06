package com.synapse.swinetime.items;

import com.synapse.swinetime.SwineTimeMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SwineTimeMod.MODID);

    public static final RegistryObject<CreativeModeTab> INHABITANTS_TAB = CREATIVE_MODE_TABS.register("swinetime_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(Items.BEEF))
                    .title(Component.translatable("creativetab.swinetime_tab"))
                    .displayItems((pParameters, pOutput) -> {

                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}