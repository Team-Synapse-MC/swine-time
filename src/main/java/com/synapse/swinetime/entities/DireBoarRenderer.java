package com.synapse.swinetime.entities;

import com.synapse.swinetime.SwineTimeMod;
import mod.azure.azurelib.render.entity.AzEntityRenderer;
import mod.azure.azurelib.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DireBoarRenderer extends AzEntityRenderer<DireBoarEntity> {
    private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath(
            SwineTimeMod.MODID,
            "geo/entity/dire_boar.geo.json"
    );

    private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath(
            SwineTimeMod.MODID,
            "textures/entity/dire_boar.png"
    );

    public DireBoarRenderer(EntityRendererProvider.Context context) {
        super(AzEntityRendererConfig.<DireBoarEntity>builder(GEO, TEX).setAnimatorProvider(DireBoarAnimator::new).build(), context);
    }
}
