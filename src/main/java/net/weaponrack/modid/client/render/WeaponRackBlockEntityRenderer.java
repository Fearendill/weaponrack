package net.weaponrack.modid.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.weaponrack.modid.block.WeaponRackBlock;
import net.weaponrack.modid.block.entity.WeaponRackBlockEntity;

public class WeaponRackBlockEntityRenderer implements BlockEntityRenderer<WeaponRackBlockEntity> {
    
    public WeaponRackBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
public void render(WeaponRackBlockEntity entity, float tickDelta, MatrixStack matrices,
                   VertexConsumerProvider vertexConsumers, int light, int overlay) {

    if (entity.getStack().isEmpty()) {
        return;
    }

    matrices.push();

    // Position de base (centre du bloc)
    matrices.translate(0.5, 0.5, 0.5);

    // Rotation selon la direction du bloc
    Direction facing = entity.getCachedState().get(WeaponRackBlock.FACING);
    switch (facing) {
        case NORTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));
        case SOUTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        case WEST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        case EAST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270));
        default -> throw new IllegalArgumentException("Unexpected value: " + facing);
    }

    // Position de l'épée (ajustée pour le nouveau modèle)
    matrices.translate(0, 0.3, -0.2);

    // Rotation de l'épée (45° inclinée)
    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45));
    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

    // Taille de l'épée
    matrices.scale(0.4f, 0.4f, 0.4f);

    // Rendu de l'item
    MinecraftClient.getInstance().getItemRenderer().renderItem(
        entity.getStack(),
        ModelTransformationMode.FIXED,
        light,
        overlay,
        matrices,
        vertexConsumers,
        entity.getWorld(),
        0
    );

    matrices.pop();
}

}