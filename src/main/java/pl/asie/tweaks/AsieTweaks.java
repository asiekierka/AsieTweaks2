package pl.asie.tweaks;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.asie.tweaks.applecore.AppleCoreTweaks;
import pl.asie.tweaks.creative.CreativeTabManager;
import pl.asie.tweaks.creative.CreativeTabMineTweaker;
import pl.asie.tweaks.forestry.ForestryTweaks;
import pl.asie.tweaks.override.FoodStatsNull;
import pl.asie.tweaks.override.MapGenStrongholdNull;
import pl.asie.tweaks.override.MapGenVillageNull;
import pl.asie.tweaks.recipes.ItemRepairMineTweaker;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = AsieTweaks.MODID, version = AsieTweaks.VERSION, dependencies = "after:AppleCore;after:Forestry")
public class AsieTweaks
{
    public static final String MODID = "asietweaks";
    public static final String VERSION = "0.0.1";

    public static Logger log;

    public static boolean disableXp = false;
    public static boolean disableHunger = false;
    public static boolean disableAchievements = false;

    private static boolean denyMobsIDontLike = false;
    private static boolean disableStrongholds = false;
    private static boolean disableVillages = false;
    private static boolean disableEnd = false;
    private static boolean disableNether = false;
    private static boolean addGraphite = false;

    private static boolean fixDyes = false;

    private static final List<PopulateChunkEvent.Populate.EventType> disabledGenerators = new ArrayList<PopulateChunkEvent.Populate.EventType>();

    private static final MapGenVillageNull nullVillageGenerator = new MapGenVillageNull();
    private static final MapGenStrongholdNull nullStrongholdGenerator = new MapGenStrongholdNull();

    private static Configuration config;
    private static File configDir;

    private static CreativeTabManager tabManager = new CreativeTabManager();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log = LogManager.getLogger("asietweaks");

        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        configDir = new File(event.getSuggestedConfigurationFile().getParentFile(), "asietweaks");

        if (!configDir.exists() && !configDir.mkdirs()) {
            log.error("Could not create AsieTweaks config directory!");
        }

        //denyMobsIDontLike = config.getBoolean("denyMobsIDontLike", "general", false, "Stops mobs Asie does not like from existing, ever");
        disableVillages = config.getBoolean("disableVillages", "world", false, "Stops villages from generating");
        disableStrongholds = config.getBoolean("disableStrongholds", "world", false, "Stops strongholds from generating");
        disableEnd = config.getBoolean("disableEnd", "world", false, "Disables The End. Implies disableStrongholds.");
        disableNether = config.getBoolean("disableNether", "world", false, "Disables The Nether. Implies disableEnd because I'm lazy.");
        disableXp = config.getBoolean("disableXp", "general", false, "Disables experience points.");
        disableHunger = config.getBoolean("disableHunger", "general", false, "Disables hunger, keeping it at 50% and hiding it from rendering.");
        disableAchievements = config.getBoolean("disableAchievements", "client", false, "Disables achievements (popups only for now).");
        addGraphite = config.getBoolean("addGraphite", "items", true, "Adds graphite (made of charcoal)");
        //fixDyes = config.getBoolean("fixDyes", "recipes", true, "Fix dyes in recipes to use the Ore Dictionary.");

        for (PopulateChunkEvent.Populate.EventType type : PopulateChunkEvent.Populate.EventType.values()) {
            if (config.getBoolean(type.name(), "disabledWorldGenerators", false, "")) {
                disabledGenerators.add(type);
            }
        }

        if (Loader.isModLoaded("AppleCore")) {
            AppleCoreTweaks.INSTANCE.preInit(config);
        }
        
        if (disableNether) {
            disableEnd = true;
        }
        if (disableEnd) {
            disableStrongholds = true;
        }

        if (addGraphite) {
            Item graphite = new Item().setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("graphite").setTextureName("asietweaks:graphite");
            GameRegistry.registerItem(graphite, "graphite");
            OreDictionary.registerOre("dyeBlack", graphite);
            GameRegistry.addShapelessRecipe(new ItemStack(graphite, 2, 0), new ItemStack(Items.coal, 1, 1));
        }

        config.save();

        tabManager.loadConfig(configDir);
    }

    @EventHandler
    public void init(FMLInitializationEvent event){
        if(disableVillages && Loader.isModLoaded("Forestry")){
            ForestryTweaks.INSTANCE.addMonasticRecipe();
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);

        if (disableEnd) {
            DimensionManager.unregisterDimension(1);
            DimensionManager.unregisterProviderType(1);
        }
        if (disableNether) {
            DimensionManager.unregisterDimension(-1);
            DimensionManager.unregisterProviderType(-1);
        }

        if (Loader.isModLoaded("MineTweaker3")) {
            CreativeTabMineTweaker.init();
            ItemRepairMineTweaker.init();
        }

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            tabManager.run();

            MinecraftForge.EVENT_BUS.register(new AsieTweaksClient());
            new AsieTweaksClient().init();
        }

        if (Loader.isModLoaded("AppleCore")) {
            AppleCoreTweaks.INSTANCE.postInit();
        }

        if (fixDyes) {
            FixDyes.run();
        }
    }

    private static final String[] FOODSTATS_NAMES = new String[] {"foodStats", "field_71100_bB", "bp"};

    @SubscribeEvent
    public void disableHunger(PlayerEvent.PlayerLoggedInEvent event) {
        if (disableHunger) {
            for (String s : FOODSTATS_NAMES) {
                try {
                    Field f = EntityPlayer.class.getDeclaredField(s);
                    f.setAccessible(true);
                    try {
                        f.set(event.player, new FoodStatsNull());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return;
                } catch(NoSuchFieldException e) {
                    //NOOP
                }
            }
        }
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (disableXp) {
            event.player.experience = 0;
            event.player.experienceLevel = 0;
            event.player.experienceTotal = 0;
        }
    }

    @SubscribeEvent
    public void breakEvent(BlockEvent.BreakEvent event) {
        if (disableXp) {
            event.setExpToDrop(0);
        }
    }

    private boolean isMobIDontLike(Entity mob) {
        if (!denyMobsIDontLike) {
            return false;
        }
        if (mob instanceof EntityCow || mob instanceof EntityPig || mob instanceof EntitySheep
                || mob instanceof EntityChicken || mob instanceof EntityPlayer) {
            return false;
        }
        return (mob instanceof EntityLiving);
    }

    @SubscribeEvent
    public void denyMobs(LivingSpawnEvent.CheckSpawn event) {
        if (isMobIDontLike(event.entity)) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void denyMobs(EntityJoinWorldEvent event) {
        if (isMobIDontLike(event.entity)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void disableStructures(InitMapGenEvent event) {
        if (disableVillages && event.originalGen instanceof MapGenVillage) {
            event.newGen = nullVillageGenerator;
        } else if (disableStrongholds && event.originalGen instanceof MapGenStronghold) {
            event.newGen = nullStrongholdGenerator;
        }
    }

    @SubscribeEvent
    public void disableGenerators(PopulateChunkEvent.Populate event) {
        event.setResult(disabledGenerators.contains(event.type) ? Event.Result.DENY : Event.Result.DEFAULT);
    }
}
