package com.synapse.swinetime.entities.dire_boar;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class DireBoarEntity extends TamableAnimal implements GeoEntity {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public DireBoarEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.3D));
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes().build();
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player pPlayer, @NotNull InteractionHand pHand) {
        Item item = pPlayer.getItemInHand(pHand).getItem();
        // TODO: fill in for hearty potato
        if (item.equals(Items.BONE)) {
            tame(pPlayer);
            return InteractionResult.SUCCESS;
        } else {
            return super.mobInteract(pPlayer, pHand);
        }
    }

    // breeding foods
    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.getItem().equals(Items.POTATO) || pStack.getItem().equals(Items.CARROT) || pStack.getItem().equals(Items.BEETROOT);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", this::predicate));
    }

    private PlayState predicate(software.bernie.geckolib.core.animation.AnimationState<DireBoarEntity> animationState) {
        if (animationState.isMoving()) {
            if (this.isSprinting()) {
                animationState.setAndContinue(RawAnimation.begin().then("Run", Animation.LoopType.LOOP));
            } else {
                animationState.setAndContinue(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
            }
        } else {
            animationState.setAndContinue(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(@NotNull ServerLevel pLevel, @NotNull AgeableMob pOtherParent) {
        return null;
    }
}
