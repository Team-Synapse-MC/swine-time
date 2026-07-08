package com.synapse.swinetime.entities.dire_boar;

import com.synapse.swinetime.entities.goals.BurrowGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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

public class DireBoarEntity extends AbstractHorse implements GeoEntity, PlayerRideable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public DireBoarEntity(EntityType<? extends AbstractHorse> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noCulling = true;
        this.setMaxUpStep(1.0f);
        this.createInventory();
        this.canGallop = false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(1, new BurrowGoal(this));
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.1f)
                .build();
    }

    // use this later for ramming
    @Override
    protected void executeRidersJump(float pPlayerJumpPendingScale, @NotNull Vec3 pTravelVector) {
        super.executeRidersJump(pPlayerJumpPendingScale, pTravelVector);
    }
    @Override
    public void onPlayerJump(int pJumpPower) {
        super.onPlayerJump(pJumpPower);
    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Override
    public boolean isSaddled() {
        return true;
    }

    @Override
    public boolean canSprint() {
        return true;
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player pPlayer, @NotNull InteractionHand pHand) {
        Item item = pPlayer.getItemInHand(pHand).getItem();
        // TODO: fill in for hearty potato
        if (!this.isTamed() && item.equals(Items.BONE)) {
            tameWithName(pPlayer);
            return InteractionResult.SUCCESS;
        } else {
            if (this.isTamed()) {
                pPlayer.startRiding(this);
                return InteractionResult.SUCCESS;
            } else {
                return super.mobInteract(pPlayer, pHand);
            }
        }
    }

    @Override
    protected float getRiddenSpeed(Player pPlayer) {
        float baseSpeed = (float) this.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
        if (pPlayer.isSprinting() || this.isSprinting()) {
            return baseSpeed * 1.5f;
        }
        return baseSpeed;
    }

    // breeding foods
    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.getItem().equals(Items.POTATO) || pStack.getItem().equals(Items.CARROT) || pStack.getItem().equals(Items.BEETROOT);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "default", 5, this::predicate)
                .triggerableAnim("burrow", RawAnimation.begin().then("eat", Animation.LoopType.PLAY_ONCE))
        );
    }

    private PlayState predicate(software.bernie.geckolib.core.animation.AnimationState<DireBoarEntity> animationState) {
        if (animationState.isMoving()) {
            if (this.isVehicle() && !this.getPassengers().isEmpty() && this.getPassengers().get(0) instanceof Player rider) {

                if (rider.isSprinting() || this.isSprinting()) {
                    animationState.setAndContinue(RawAnimation.begin().then("Run", Animation.LoopType.LOOP));
                } else {
                    animationState.setAndContinue(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
                }
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
