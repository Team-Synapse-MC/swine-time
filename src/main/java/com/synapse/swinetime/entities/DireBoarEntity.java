package com.synapse.swinetime.entities;

import mod.azure.azurelib.util.MoveAnalysis;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
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
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.7D, 2));
    }

    @Override
    public void tick() {
        super.tick();
        moveAnalysis.update();

        if (this.level().isClientSide) {
            Runnable animRunner;
            boolean isMovingOnGround = moveAnalysis.isMovingHorizontally() && onGround();
            if (isMovingOnGround) {
                if (this.isAggressive()) // if moving and aggressive, play running
                    animRunner = animationDispatcher::run;
                else // if moving but not aggressive play walk
                    animRunner = animationDispatcher::walk;
            } else {
                animRunner = animationDispatcher::idle;
            }
            animRunner.run();
        }
    }
    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes().build();
    }
}
