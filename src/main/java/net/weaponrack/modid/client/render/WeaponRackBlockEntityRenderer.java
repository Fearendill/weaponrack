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
    public void render(WeaponRackBlockEntity entity, float tickDelta,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            int light, int overlay) {

        if (entity.getWeaponItem().isEmpty())
            return;

        matrices.push();

        boolean onWall = entity.getCachedState().get(WeaponRackBlock.ON_WALL);
        Direction facing = entity.getCachedState().get(WeaponRackBlock.FACING);

        /*
         * =========================
         * 1️⃣ CENTRAGE DE BASE (centre du bloc 0.5, 0.5, 0.5)
         * =========================
         */
        matrices.translate(0.5F, 0.25F, 0.5F);
        // premier paramètre : X
        // deuxième paramètre : Y
        // troisième paramètre : Z

        /*
         * =========================
         * 2️⃣ ROTATION SELON LA DIRECTION (facing)
         * =========================
         * On fait tourner l'épée pour qu'elle suive l'orientation du rack
         */
        switch (facing) {
            case NORTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            case SOUTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));
            case WEST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
            case EAST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-270));
            case UP, DOWN -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));
        }

        /*
         * =========================
         * 3️⃣ POSITIONNEMENT VERTICAL ET PROFONDEUR
         * =========================
         */
        if (onWall) {
            // ACCROCHÉ AU MUR
            // On recule légèrement pour que l'épée soit contre le mur (Z négatif = arrière
            // du bloc)
            matrices.translate(0.0F, 0.0F, -0.35F);

            // Ajustement hauteur pour centrer sur le support du milieu (élément 6 du JSON :
            // Y=7 à 11)
            matrices.translate(0.0F, 0.1F, 0.0F);

        } else {
            // POSÉ AU SOL
            // On descend pour que la pointe repose sur le rack (élément 0 du JSON commence
            // à Y=2.93)
            matrices.translate(0.0F, -0.15F, 0.0F);
        }

        /*
         * =========================
         * 4️⃣ ROTATION VERTICALE (lame vers le bas)
         * =========================
         * Par défaut les items FIXED montrent la face "plate" vers le haut
         * 180° sur X retourne l'item (lame vers le bas)
         */
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        // Ajustement pour que la garde soit en haut et la lame en bas
        // (Certains items ont besoin d'une rotation Y supplémentaire pour être "droit")
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));

        // Ajustement pour que la garde soit en haut et la lame en bas
        // (Certains items ont besoin d'une rotation Z supplémentaire pour être "droit")
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45));

        /*
         * =========================
         * 5️⃣ ÉCHELLE (1.0 = taille normale, peut ajuster)
         * =========================
         */
        // Exemple d'échelle doublée en hauteur avec largeur et profondeur adaptées
        matrices.scale(1.0f, 1.0f, 1.0f);
            // premier paramètre : échelle X (largeur)
            // deuxième paramètre : échelle Y (hauteur)
            // troisième paramètre : échelle Z (profondeur)
        /*
         * =========================
         * 6️⃣ RENDU
         * =========================
         */
        MinecraftClient.getInstance().getItemRenderer().renderItem(
                entity.getWeaponItem(),
                ModelTransformationMode.FIXED,
                light,
                overlay,
                matrices,
                vertexConsumers,
                entity.getWorld(),
                0);

        matrices.pop();
    }
}