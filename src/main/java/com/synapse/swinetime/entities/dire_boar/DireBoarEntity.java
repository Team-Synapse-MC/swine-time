package com.synapse.swinetime.entities.dire_boar;

import com.synapse.swinetime.entities.goals.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
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

import java.util.Objects;

public class DireBoarEntity extends AbstractHorse implements GeoEntity, PlayerRideable, OwnableEntity {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final float SPRINT_SPEED_MULT = 1.5f;

    public DireBoarEntity(EntityType<? extends AbstractHorse> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noCulling = true;
        this.setMaxUpStep(1.0f);
        this.createInventory();
        this.canGallop = false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new AnimatedCooldownMeleeAttackGoal(
                this, 1.0D, false, 20,
                "default", "attack", 20, 10,
                true, true
                ).setFreezeMovement(0, 15)
        );
        this.goalSelector.addGoal(6, new DireBoarFollowOwner(this, 1.0D, 4.0F, 8.0F, 3.0F, false));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new BurrowGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new BoarOwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new BoarOwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity pTarget) {
        if (Objects.equals(getOwner(), pTarget)) {
            return false;
        } else if (pTarget instanceof DireBoarEntity boar && Objects.equals(getOwner(), boar.getOwner())) {
            return false;
        }
        return super.canAttack(pTarget);
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.ATTACK_DAMAGE, 6.0f)
                .add(Attributes.MAX_HEALTH, 20.0f)
                .build();
    }

    @Override
    public double getMeleeAttackRangeSqr(@NotNull LivingEntity pEntity) {
        // half the default range
        return super.getMeleeAttackRangeSqr(pEntity) * 0.7;
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
    public boolean isSaddleable() {
        return false;
    }

    @Override
    public boolean canSprint() {
        return true;
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player pPlayer, @NotNull InteractionHand pHand) {
        Item item = pPlayer.getItemInHand(pHand).getItem();
        if (isFood(pPlayer.getItemInHand(pHand))) {
            return super.mobInteract(pPlayer, pHand);
        }

        // TODO: fill in for hearty potato
        if (!this.isTamed() && item.equals(Items.BONE)) {
            tameWithName(pPlayer);
            return InteractionResult.SUCCESS;
        } else {
            if (this.isTamed() && this.getOwner() != null && this.getOwner().equals(pPlayer)) {
                pPlayer.startRiding(this);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

    @Override
    public float getSpeed() {
        float baseSpeed = (float) this.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
        if (isSprinting()) {
            return baseSpeed * SPRINT_SPEED_MULT;
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
                .triggerableAnim("attack", RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE))
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
                if (this.isSprinting()) {
                    animationState.setAndContinue(RawAnimation.begin().then("Run", Animation.LoopType.LOOP));
                } else {
                    animationState.setAndContinue(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
                }
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
