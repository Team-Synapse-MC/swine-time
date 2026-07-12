package com.synapse.swinetime.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RamPacketC2S {
    private final int entityId;

    public RamPacketC2S(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(RamPacketC2S packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.entityId);
    }

    public static RamPacketC2S decode(FriendlyByteBuf buffer) {
        return new RamPacketC2S(buffer.readInt());
    }

    public static void handle(RamPacketC2S packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            assert player != null;
            Level level = player.level();
            Entity entity = level.getEntity(packet.entityId);
            if (entity == null) return;
            DamageSource damageSource = entity.damageSources().mobAttack(player);
            entity.hurt(damageSource, 5f);
        });
        ctx.get().setPacketHandled(true);
    }
}
