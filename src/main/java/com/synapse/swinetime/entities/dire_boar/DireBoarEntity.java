package com.synapse.swinetime.entities.dire_boar;

import com.synapse.swinetime.entities.goals.*;
import com.synapse.swinetime.items.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
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

import java.util.List;
import java.util.Objects;

public class DireBoarEntity extends AbstractHorse implements GeoEntity, PlayerRideable, OwnableEntity {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final float SPRINT_SPEED_MULT = 10.0f;
    private float rammingTicks = 0;
    private boolean isFleeing = false;

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
                this, 1.2D, false, 20,
                "default", "attack", 20, 10,
                true, true
                ).setFreezeMovement(0, 15)
        );
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new BurrowGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

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

    @Override
    public boolean doHurtTarget(@NotNull Entity pEntity) {
//        pEntity.addDeltaMovement(new Vec3(0, 2.0, 0)); // this doesnt seem to work on players
        return super.doHurtTarget(pEntity);
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.ATTACK_DAMAGE, 6.0f)
                .add(Attributes.MAX_HEALTH, 30.0f)
                .build();
    }

    @Override
    public double getMeleeAttackRangeSqr(@NotNull LivingEntity pEntity) {
        // lower the default range
        return super.getMeleeAttackRangeSqr(pEntity) * 0.7;
    }

    @Override
    protected void tickRidden(@NotNull Player pPlayer, @NotNull Vec3 pTravelVector) {
        if (rammingTicks > 0) {
            rammingTicks--;
            this.setSprinting(true);

            if (rammingTicks <= 0) {
                this.setIsJumping(false);
                this.setStanding(false);
                this.setSprinting(false);
            }

            AABB frontBox = this.getBoundingBox().inflate(1.5);
            List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, frontBox,
                    (e) -> e != this && e.isAlive()
            );

            if (!list.isEmpty()) {
                LivingEntity hitEntity = list.get(0);
                if (hitEntity instanceof Player player) {
                    if (player == getOwner()) {
                        return;
                    }
                }
                hitEntity.hurt(this.damageSources().mobAttack(this), 10f);
            }
        }
        super.tickRidden(pPlayer, pTravelVector);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.isFleeing) {
            AABB box = this.getBoundingBox().inflate(40);
            List<LightningBolt> list = level().getEntitiesOfClass(LightningBolt.class, box);

            if (!list.isEmpty()) {
                LightningBolt bolt = list.get(0);
                Vec3 random_position = DefaultRandomPos.getPosAway(this, 15, 5, bolt.position());
                if (random_position == null) return;
                this.getNavigation().moveTo(random_position.x, random_position.y, random_position.z, 1.4);
                this.setSprinting(true);
                this.isFleeing = true;
            }
        } else {
            if (this.getNavigation().isDone()) {
                this.isFleeing = false;
                this.setSprinting(false);
            }
        }
    }

    @Override
    public boolean canSpawnSprintParticle() {
        return super.canSpawnSprintParticle() && getDeltaMovement().lengthSqr() > 1;
    }

    @Override
    protected void executeRidersJump(float pPlayerJumpPendingScale, @NotNull Vec3 pTravelVector) {
    }

    @Override
    public void handleStartJump(int pJumpPower) {
        // enables "standing", which results in a rider position translation
        super.handleStartJump(pJumpPower);

        if (rammingTicks > 0) return;

        // max jumpPower is 90
        rammingTicks = 20 * ((float) pJumpPower / 90);
    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Override
    public boolean isSaddled() {
        return super.isSaddled();
    }

    @Override
    public boolean isSaddleable() {
        return true;
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player pPlayer, @NotNull InteractionHand pHand) {
        Item item = pPlayer.getItemInHand(pHand).getItem();
        if (isFood(pPlayer.getItemInHand(pHand))) {
            return super.mobInteract(pPlayer, pHand);
        }

        if (!this.isTamed() && item.equals(ModItems.HEARTY_POTATO.get())) {
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
    protected float getRiddenSpeed(@NotNull Player pPlayer) {
        float baseSpeed = 0.05f;
        if (isSprinting() || Objects.requireNonNull(this.getControllingPassenger()).isSprinting()) {
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
            if (this.isVehicle() && !this.getPassengers().isEmpty() && this.getControllingPassenger() instanceof Player rider) {
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
