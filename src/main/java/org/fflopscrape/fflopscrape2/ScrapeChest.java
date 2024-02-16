package org.fflopscrape.fflopscrape2;

import com.mojang.logging.LogUtils;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(ScrapeChest.MODID)
@Mod.EventBusSubscriber(modid = ScrapeChest.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = {Dist.CLIENT, Dist.DEDICATED_SERVER})

public class ScrapeChest {

    public static final String MODID = "fflopscrape_2_1";
    private static final Logger LOGGER = LogUtils.getLogger();
    protected static boolean logEverything;

    @Mod.EventBusSubscriber(modid = MODID)
    public static class EventHandler {

        @SubscribeEvent
        public static void onContainerOpen(PlayerContainerEvent.Open event) {
            if (event.getContainer() instanceof ChestMenu) {
                ChestMenu chestContainer = (ChestMenu) event.getContainer();

                try {
                    logChestContentsIterating(chestContainer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public static String desktopPath = System.getProperty("user.home") + "/Desktop/";


        public static boolean hasRightClicked = false;

        @SubscribeEvent
        public static void onEmptyRightClickEvent(PlayerInteractEvent.RightClickEmpty event) {
            hasRightClicked = !hasRightClicked;
            LOGGER.info("Logging: " + hasRightClicked);
        }

        private static void logChestContentsIterating(ChestMenu chestMenu) throws IOException {

            if (hasRightClicked) {
                // String fileName = "chest_contents.txt";

                String fileName = desktopPath + "chest_contents.txt";

                try (FileWriter writer = new FileWriter(fileName, true)) {
                    writer.write("Chest opened:\n");
                    LOGGER.info("Chestlogging Triggered");

                    Container container = chestMenu.getContainer();
                    // int rowCount = chestMenu.getRowCount();

                    for (int slotIndex = 0; slotIndex < container.getContainerSize(); slotIndex++) {
                        ItemStack itemStack = container.getItem(slotIndex);
                        if (!itemStack.isEmpty()) {
                            String itemName = itemStack.getDisplayName().getString();
                            int row = slotIndex / 9;
                            int column = slotIndex % 9;
                            writer.write("Slot (" + column + ", " + row + "): " + itemName + "\n");
                        }
                    }

                    writer.write("\n");
                }
            }
        }

        public static boolean logEverything = true;

        @SubscribeEvent
        public static void onEvent(PlayerEvent event) {
            if (logEverything && hasRightClicked) {
                try (FileWriter writer = new FileWriter(desktopPath + "log.txt", true)) {
                    if (!event.getClass().getSimpleName().equals("MovementInputUpdateEvent")
                            && !event.getClass().getSimpleName().contains("Pre")
                            && !event.getClass().getSimpleName().contains("Post")) {
                        String eventName = event.getClass().getSimpleName();
                        if (!eventName.equals("ItemTooltipEvent")) {
                            writer.write("an event was triggered, name: " + eventName + "\n");
                            System.out.println("Event triggered: " + event.getClass().getSimpleName());
                            LOGGER.info("Event triggered: " + event.getClass().getSimpleName());
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);

                }
            }

        }

        public static String storage = "";
        public static String lastWrittenLine = "";

        @SubscribeEvent
        public static void onItemTooltipEvent(ItemTooltipEvent event) {
            if (hasRightClicked) {
                storage = "";
                List<String> literals = new ArrayList<>();
                Pattern pattern = Pattern.compile("literal\\{([^}]*)\\}");

                for (int i = 0; i < event.getToolTip().size(); i++) {
                    String temp = String.valueOf(event.getToolTip().get(i));
                    Matcher matcher = pattern.matcher(temp);

                    while (matcher.find()) {
                        String literal = matcher.group(1);
                        literals.add(literal);
                    }
                }

                String output = "TooltipEvent, " + String.join(" ", literals) + "\n";
                LOGGER.info(output);

                if (!output.equals(lastWrittenLine)) {
                    try (FileWriter writer = new FileWriter(desktopPath + "itemTooltipExport.txt", true)) {
                        writer.write(output);
                        lastWrittenLine = output;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}