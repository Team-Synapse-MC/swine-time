package com.synapse.swinetime.entities.goals;

import com.synapse.swinetime.SwineTimeMod;
import com.synapse.swinetime.entities.dire_boar.DireBoarEntity;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BurrowGoal extends Goal {
    private final DireBoarEntity mob;

    private static final int INTERVAL = 200;
    private static final int DURATION = 15;

    private long lastBurrow;
    private int burrowTimer;

    public BurrowGoal(DireBoarEntity direBoar) {
        this.mob = direBoar;
        this.lastBurrow = this.mob.level().getGameTime();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mob.level().getGameTime() - this.lastBurrow >= INTERVAL && !this.mob.isVehicle() && this.mob.isTamed();
    }

    @Override
    public boolean canContinueToUse() {
        return burrowTimer > 0;
    }

    @Override
    public void start() {
        this.lastBurrow = mob.level().getGameTime();
        this.mob.triggerAnim("default", "burrow");
        burrowTimer = DURATION;
    }

    @Override
    public void tick() {
        burrowTimer -= 1;

        mob.getNavigation().stop();
        mob.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public void stop() {
        if (mob.level() instanceof  ServerLevel serverLevel) {
            LootTable table = serverLevel.getServer().getLootData().
                    getLootTable(ResourceLocation.fromNamespaceAndPath(SwineTimeMod.MODID, "entities/dire_boar_burrow"));

            LootParams context = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.THIS_ENTITY, mob)
                    .withParameter(LootContextParams.ORIGIN, mob.position())
                    .create(LootContextParamSets.GIFT);

            table.getRandomItems(context)
                    .forEach(itemStack -> {
                        Vec3i facing_normal = mob.getDirection().getNormal();
                        ItemEntity itementity = new ItemEntity(mob.level(), mob.getX() + facing_normal.getX(), mob.getY(), mob.getZ() + facing_normal.getZ(), itemStack);
                        itementity.setDefaultPickUpDelay();
                        mob.level().addFreshEntity(itementity);
                    });
        }

        super.stop();
    }
}
