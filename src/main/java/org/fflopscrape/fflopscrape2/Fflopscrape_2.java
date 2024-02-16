package org.fflopscrape.fflopscrape2;

import com.mojang.logging.LogUtils;
import net.minecraft.util.profiling.jfr.event.WorldLoadFinishedEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Fflopscrape_2.MODID) // MODID is defined below
public class Fflopscrape_2 {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "fflopscrape_2_1";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Fflopscrape_2() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("common setup");
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("server starting");
    }

    @SubscribeEvent
        public static void onContainerOpen(PlayerContainerEvent.Open event)
        {
            // Do something when a player opens a container
            LOGGER.info("container open");

            // check if container is a chestMenu instance 
            if (event.getContainer() instanceof AbstractContainerMenu) {
                LOGGER.info("Opened AbstractContainerMenu");
                logChestContents(event.getContainer());
            }
        }

        public static void logChestContents(AbstractContainerMenu container) {
            // Do something when a player opens a container
            LOGGER.info("Iterating Through AbstractContainerMenu Slots");
            // iterate over all itemstacks in the container and extract the names. Save to a list.
            try {
                LOGGER.info("Found container." + container.slots.size() + " slots in container");

                File logFile = new File(System.getProperty("user.home") + "/Desktop/log.txt");
                FileWriter writer = new FileWriter(logFile, true);
                for (int i = 0; i < container.slots.size(); i++) {
                    String logMessage = "Slot " + i + " contains " + container.slots.get(i).getItem().getDisplayName().getString();
                    LOGGER.info(logMessage);
                    writer.write(logMessage + "\n");
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < container.slots.size(); i++) {
                LOGGER.info("Slot " + i + " contains " + container.slots.get(i).getItem().getDisplayName().getString()); 
                
            }
        }

        public static void onWorldLoaded(WorldLoadFinishedEvent event) {
            // Do something when the world is loaded
            LOGGER.info("World Loaded Successfully!");

            try {
                File logFile = new File(System.getProperty("user.home") + "/Desktop/worldLoad.txt");
                FileWriter writer = new FileWriter(logFile, true);
                writer.write("World Loaded! \n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("client setup");
        }

    }
}
