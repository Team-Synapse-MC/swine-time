package com.synapse.swinetime.entities;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DireBoarRenderer extends GeoEntityRenderer<DireBoarEntity> {
    public DireBoarRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DireBoarModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DireBoarEntity animatable) {
        return this.getGeoModel().getTextureResource(animatable);
    }
}
