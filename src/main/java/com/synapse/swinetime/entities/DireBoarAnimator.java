package com.synapse.swinetime.entities;

import com.synapse.swinetime.SwineTimeMod;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DireBoarAnimator extends AzEntityAnimator<DireBoarEntity> {
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(
            SwineTimeMod.MODID,
            "animations/entity/dire_boar.animation.json"
    );

    @Override
    public void registerControllers(AzAnimationControllerContainer<DireBoarEntity> animationControllerContainer) {
        animationControllerContainer.add(
                AzAnimationController.builder(this, "base_controller")
                        .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(DireBoarEntity direBoarEntity) {
        return ANIMATIONS;
    }
}
