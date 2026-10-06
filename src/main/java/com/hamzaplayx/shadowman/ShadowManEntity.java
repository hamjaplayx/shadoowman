package com.hamzaplayx.shadowman;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;

public class ShadowManEntity extends HostileEntity {
    private int scareCooldown = 160;
    private int vanishCooldown = 0;

    public ShadowManEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
        this.experiencePoints = 20;
    }

    @Override protected void initGoals() {
        goalSelector.add(2, new MeleeAttackGoal(this, 1.35, false));
        goalSelector.add(7, new WanderAroundFarGoal(this, 0.75));
        goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 16.0f));
        goalSelector.add(9, new LookAroundGoal(this));
        targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override public void tick() {
        super.tick();
        World world = getWorld();
        if (world.isClient) return;
        ServerWorld sw = (ServerWorld) world;

        if (vanishCooldown > 0) vanishCooldown--;
        if (scareCooldown > 0) scareCooldown--;

        // Constant subtle shadow particles.
        if (world.random.nextInt(3) == 0) {
            sw.spawnParticles(ParticleTypes.SMOKE, getX(), getBodyY(0.25), getZ(), 2, 0.22, 0.35, 0.22, 0.01);
        }

        PlayerEntity player = world.getClosestPlayer(this, 28.0);
        if (player == null || player.isCreative() || player.isSpectator()) return;

        double distance = squaredDistanceTo(player);
        // Rare stalking jump-scare: teleport behind/near a player and make a vanilla horror sound.
        if (scareCooldown <= 0 && distance > 10.0 && distance < 28.0 && world.random.nextInt(180) == 0 && vanishCooldown == 0) {
            teleportNearPlayer(player);
            scareCooldown = 300 + world.random.nextInt(400);
        }

        // Very rare disappearance after stalking, rather than every encounter.
        if (vanishCooldown == 0 && distance > 18.0 && world.random.nextInt(500) == 0) {
            sw.spawnParticles(ParticleTypes.LARGE_SMOKE, getX(), getBodyY(0.5), getZ(), 20, 0.35, 0.6, 0.35, 0.02);
            discard();
        }
    }

    private void teleportNearPlayer(PlayerEntity player) {
        World world = getWorld();
        Vec3d look = player.getRotationVec(1.0f).normalize();
        // Place ShadowMan roughly behind the player.
        double x = player.getX() - look.x * (6.0 + world.random.nextDouble() * 5.0);
        double y = player.getY();
        double z = player.getZ() - look.z * (6.0 + world.random.nextDouble() * 5.0);
        this.refreshPositionAndAngles(x, y, z, player.getYaw() + 180.0f, 0.0f);
        world.playSound(null, x, y, z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.HOSTILE, 0.9f, 0.65f);
        ((ServerWorld) world).spawnParticles(ParticleTypes.PORTAL, x, y + 1.0, z, 45, 0.35, 0.8, 0.35, 0.05);
        vanishCooldown = 100;
    }
}
