package com.hamzaplayx.shadowman;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public class ShadowManMod implements ModInitializer {
    public static final String MOD_ID = "shadowman";
    public static final EntityType<ShadowManEntity> SHADOWMAN = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(MOD_ID, "shadowman"),
            EntityType.Builder.create(ShadowManEntity::new, SpawnGroup.MONSTER)
                    .setDimensions(0.8f, 2.2f).maxTrackingRange(64).build("shadowman")
    );

    public static final SpawnEggItem SHADOWMAN_SPAWN_EGG = Registry.register(
            Registries.ITEM, new Identifier(MOD_ID, "shadowman_spawn_egg"),
            new SpawnEggItem(SHADOWMAN, 0x0A0A0A, 0x5A0000, new FabricItemSettings())
    );

    @Override public void onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> entries.add(SHADOWMAN_SPAWN_EGG));
        FabricDefaultAttributeRegistry.register(SHADOWMAN, createAttributes());
        SpawnRestriction.register(SHADOWMAN, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), SpawnGroup.MONSTER, SHADOWMAN, 8, 1, 1);
    }

    private static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.30)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.35);
    }
}
