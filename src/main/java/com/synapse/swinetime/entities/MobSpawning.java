package com.synapse.swinetime.entities;

import com.synapse.swinetime.SwineTimeMod;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SwineTimeMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MobSpawning {
    @SubscribeEvent
    public static void entitySpawnRestriction(SpawnPlacementRegisterEvent event) {
        event.register(
            ModEntities.DIRE_BOAR.get(),
            SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            (type, level, reason, pos, random) -> pos.getY() > level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 15,
            SpawnPlacementRegisterEvent.Operation.REPLACE
        );
    }

    private static boolean isFlat(LevelAccessor level) {
        if (level.getLevelData() instanceof WorldData worldData && worldData.isFlatWorld()) {
            return true;
        }
        return level.getChunkSource() instanceof ServerChunkCache scc &&
            scc.getGenerator() instanceof FlatLevelSource;
    }
}
