package com.hamzaplayx.shadowman;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class ShadowManClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        EntityRendererRegistry.register(ShadowManMod.SHADOWMAN, ShadowManRenderer::new);
    }

    private static class ShadowManRenderer extends MobEntityRenderer<ShadowManEntity, BipedEntityModel<ShadowManEntity>> {
        private static final Identifier TEXTURE = new Identifier(ShadowManMod.MOD_ID, "textures/entity/shadowman.png");
        ShadowManRenderer(EntityRendererFactory.Context context) {
            super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER)), 0.5f);
        }
        @Override public Identifier getTexture(ShadowManEntity entity) { return TEXTURE; }
    }
}
