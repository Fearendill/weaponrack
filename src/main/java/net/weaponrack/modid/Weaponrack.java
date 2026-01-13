package net.weaponrack.modid;

import net.fabricmc.api.ModInitializer;
import net.weaponrack.modid.register.ModBlocks;
import net.weaponrack.modid.register.ModBlockEntities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Weaponrack implements ModInitializer {
    
    public static final String MODID = "weaponrack";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        System.out.println("===========================================");
        System.out.println("WEAPON RACK MOD IS LOADING!!!");
        System.out.println("===========================================");
        
        LOGGER.info("Weapon Rack Mod Starting");
        
        ModBlocks.registerBlocks();
        LOGGER.info("Blocks registered");
        
        ModBlockEntities.registerBlockEntities();
        LOGGER.info("Block Entities registered");
        
        System.out.println("===========================================");
        System.out.println("WEAPON RACK MOD LOADED SUCCESSFULLY!!!");
        System.out.println("===========================================");
    }
}