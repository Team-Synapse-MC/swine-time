package com.synapse.swinetime.entities;

import com.synapse.swinetime.SwineTimeMod;
import com.synapse.swinetime.entities.dire_boar.DireBoarEntity;
import com.synapse.swinetime.entities.dire_boar.DireBoarRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SwineTimeMod.MODID);

    public static final RegistryObject<EntityType<DireBoarEntity>> DIRE_BOAR =
            REGISTRY.register("dire_boar",
                    () -> EntityType.Builder.of(DireBoarEntity::new, MobCategory.MONSTER)
                            .sized(2.5f, 2.5f)
                            .build(ResourceLocation.fromNamespaceAndPath(SwineTimeMod.MODID, "dire_boar").toString())
            );

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DIRE_BOAR.get(), DireBoarRenderer::new);
    }

    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntities.DIRE_BOAR.get(), DireBoarEntity.setAttributes());
    }

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
