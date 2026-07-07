package com.synapse.swinetime.entities;

import mod.azure.azurelib.util.MoveAnalysis;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;

public class DireBoarEntity extends AbstractHorse {
    public final DireBoarDispatcher dispatcher;
    public final MoveAnalysis moveAnalysis;

    public DireBoarEntity(EntityType<? extends AbstractHorse> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.dispatcher = new DireBoarDispatcher(this);
        this.moveAnalysis = new MoveAnalysis(this);
    }

    public static AttributeSupplier setAttributes() {
        return AbstractHorse.createBaseHorseAttributes().build();
    }
}
