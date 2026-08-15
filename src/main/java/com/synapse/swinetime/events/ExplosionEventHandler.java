package com.synapse.swinetime.events;

import com.synapse.swinetime.SwineTimeMod;
import com.synapse.swinetime.entities.dire_boar.DireBoarEntity;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SwineTimeMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExplosionEventHandler {
    @SubscribeEvent
    public static void onExplosion(ExplosionEvent event) {
        SwineTimeMod.LOGGER.info("Explosion");
        Vec3 pos = event.getExplosion().getPosition();
        AABB box = AABB.ofSize(pos, 40f, 20f, 40f);

        List<DireBoarEntity> list = event.getLevel().getEntitiesOfClass(DireBoarEntity.class, box);

        for (DireBoarEntity direBoar : list) {
            Vec3 random_position = DefaultRandomPos.getPosAway(direBoar, 20, 5, pos);
            if (random_position == null) continue;
            direBoar.flee(random_position);
        }
    }
}
