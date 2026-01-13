package net.weaponrack.modid.register;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.weaponrack.modid.Weaponrack;
import net.weaponrack.modid.block.entity.WeaponRackBlockEntity;

public class ModBlockEntities {
    public static BlockEntityType<WeaponRackBlockEntity> WEAPON_RACK;

    public static void registerBlockEntities() {
        WEAPON_RACK = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Weaponrack.MODID, "weapon_rack"),
            BlockEntityType.Builder.create(
                WeaponRackBlockEntity::new, 
                ModBlocks.WEAPON_RACK
            ).build()
        );
    }
}