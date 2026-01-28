package net.weaponrack.modid.register;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.weaponrack.modid.Weaponrack;
import net.weaponrack.modid.block.WeaponRackBlock;

public class ModBlocks {
    
    // Déclaration du bloc - PAS d'appel à registerBlocks() ici !
    public static final Block WEAPON_RACK = new WeaponRackBlock(
        AbstractBlock.Settings.create()
            .strength(2.0f)
            .sounds(BlockSoundGroup.WOOD)
            .nonOpaque()
            .noCollision()
            .allowsSpawning((s, w, p, e) -> false)
            .solidBlock((s, w, p) -> false)
            .suffocates((s, w, p) -> false)
            .blockVision((s, w, p) -> false)
    );
    
    public static void registerBlocks() {
        Weaponrack.LOGGER.info("Registering blocks...");
        
        // Enregistrement du bloc
        Registry.register(
            Registries.BLOCK, 
            Identifier.of(Weaponrack.MODID, "weapon_rack"), 
            WEAPON_RACK
        );
        
        // Enregistrement de l'item
        Registry.register(
            Registries.ITEM, 
            Identifier.of(Weaponrack.MODID, "weapon_rack"),
            new BlockItem(WEAPON_RACK, new Item.Settings())
        );
        
        Weaponrack.LOGGER.info("Blocks registered successfully!");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(WEAPON_RACK);
        });

    }
}
