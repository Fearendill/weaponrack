package net.weaponrack.modid;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.weaponrack.modid.client.render.WeaponRackBlockEntityRenderer;
import net.weaponrack.modid.register.ModBlockEntities;

public class WeaponrackClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(
            ModBlockEntities.WEAPON_RACK, 
            WeaponRackBlockEntityRenderer::new
        );
    }
}