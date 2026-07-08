package com.synapse.swinetime.entities;

import mod.azure.azurelib.util.MoveAnalysis;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class DireBoarEntity extends Monster {
    public final DireBoarDispatcher animationDispatcher;
    public final MoveAnalysis moveAnalysis;

    public DireBoarEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.animationDispatcher = new DireBoarDispatcher(this);
        this.moveAnalysis = new MoveAnalysis(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 1.0D));
    }

    @Override
    public void tick() {
        super.tick();
        this.moveAnalysis.update();
    }

    public void updateAnimations() {
        boolean isMovingOnGround = moveAnalysis.isMovingHorizontally() && onGround();

        Runnable animationRunner;
        if (isMovingOnGround) {
//                if (this.isAggressive()) {
//                    animationRunner = animationDispatcher::run;
//                } else {
//                    animationRunner = animationDispatcher::walk;
//                }
            animationRunner = animationDispatcher::walk;
        } else {
            animationRunner = animationDispatcher::idle;
        }
        animationRunner.run();
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .build();
    }
}
