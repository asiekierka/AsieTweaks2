package pl.asie.tweaks.creative;

import net.minecraftforge.common.config.Configuration;
import pl.asie.tweaks.AsieTweaks;

import java.io.File;
import java.util.Random;

public class CreativeTabManager
{
    public static Configuration config;
    public static Configuration itemConfig;
    public static Configuration tabConfig;
    public static Random rand = new Random();
    public static File configDir;
    public static File tabDefinitionDir;
    private TabManager tabs;
    
    public void loadConfig(File configFile) {
        configDir = new File(configFile, "creative");
        tabDefinitionDir = new File(configDir, "tabs");
        if (!configDir.exists() && !configDir.mkdirs()) {
            AsieTweaks.log.error("Could not create ReCreate config directory! No good!");
        }
        config = new Configuration(new File(configDir, "main.cfg"));
        itemConfig = new Configuration(new File(configDir, "items.cfg"));
        tabConfig = new Configuration(new File(configDir, "tabs.cfg"));
        config.load();
        itemConfig.load();
        tabConfig.load();
    }

    public void run() {
        tabs = new TabManager();
        tabs.loadTabs(config, tabConfig);
        tabs.arrangeItems(itemConfig);
        config.save();
        itemConfig.save();
        tabConfig.save();
    }
}
