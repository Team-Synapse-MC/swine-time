package com.synapse.swinetime.entities.dire_boar;

import com.synapse.swinetime.SwineTimeMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DireBoarModel extends GeoModel<DireBoarEntity> {
    @Override
    public ResourceLocation getModelResource(DireBoarEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SwineTimeMod.MODID, "geo/dire_boar.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DireBoarEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SwineTimeMod.MODID, "textures/entities/dire_boar.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DireBoarEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SwineTimeMod.MODID, "animations/dire_boar.animation.json");
    }
}
