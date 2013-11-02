package mattparks.mods.starcraft.core;

import java.io.File;
import java.util.HashMap;

import mattparks.mods.starcraft.core.entities.SCCoreEntityRocketT3;
import mattparks.mods.starcraft.core.entities.SCCoreEntityRocketT4;
import mattparks.mods.starcraft.core.entities.SCCoreEntityRocketT5;
import mattparks.mods.starcraft.core.items.SCCoreItems;
import mattparks.mods.starcraft.core.network.SCCorePacketHandlerServer;
import mattparks.mods.starcraft.core.recipe.SCCoreRecipeManager;
import mattparks.mods.starcraft.core.schematic.SCCoreSchematicRocketT3;
import mattparks.mods.starcraft.spacecraftBlocks.SpacecraftBlocks;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.recipe.CompressorRecipes;
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry;
import micdoodle8.mods.galacticraft.core.GCCoreConfigManager;
import micdoodle8.mods.galacticraft.core.GCLog;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.GCCoreBlocks;
import micdoodle8.mods.galacticraft.core.entities.GCCoreEntityAlienVillager;
import micdoodle8.mods.galacticraft.core.entities.GCCoreEntitySpider;
import micdoodle8.mods.galacticraft.core.entities.GCCoreEntityZombie;
import micdoodle8.mods.galacticraft.core.items.GCCoreItems;
import micdoodle8.mods.galacticraft.core.network.GCCoreConnectionHandler;
import micdoodle8.mods.galacticraft.core.network.GCCorePacketManager;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.world.gen.GCCoreOverworldGenerator;
import micdoodle8.mods.galacticraft.moon.items.GCMoonItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(name = StarcraftCore.NAME, version = GalacticraftCore.LOCALMAJVERSION + "." + GalacticraftCore.LOCALMINVERSION + "." + GalacticraftCore.LOCALBUILDVERSION, useMetadata = true, modid = StarcraftCore.MODID, dependencies = "required-after:" + GalacticraftCore.MODID + ";")
@NetworkMod(channels = { StarcraftCore.CHANNEL }, clientSideRequired = true, serverSideRequired = false, connectionHandler = GCCoreConnectionHandler.class, packetHandler = GCCorePacketManager.class)
public class StarcraftCore
{
    public static final String NAME = "Starcraft Core";
    public static final String MODID = "StarcraftCore";
    public static final String CHANNEL = "GCStarcraftCore";
    public static final String CHANNELENTITIES = "SCCoreEntities";

    public static final String LANGUAGE_PATH = "/assets/starcraftcore/lang/";
    private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US" };

    @SidedProxy(clientSide = "mattparks.mods.starcraft.core.client.ClientProxySCCore", serverSide = "mattparks.mods.starcraft.core.CommonProxySCCore")
    public static CommonProxySCCore proxy;

    @Instance(StarcraftCore.MODID)
    public static StarcraftCore instance;
    
	public static CreativeTabs starcraftCoreTab = new CreativeTabs("starcraftCoreTab") {
		public ItemStack getIconItemStack() {
			return new ItemStack(SCCoreItems.spaceshipT3, 1, 0);
		}
	};
	
	public static CreativeTabs starcraftMercuryTab = new CreativeTabs("starcraftMercuryTab") {
		public ItemStack getIconItemStack() {
			return new ItemStack(SpacecraftBlocks.MercuryGrass, 1, 0);
		}
	};
	
	public static CreativeTabs starcraftVenusTab = new CreativeTabs("starcraftVenusTab") {
		public ItemStack getIconItemStack() {
			return new ItemStack(SpacecraftBlocks.VenusGrass, 1, 0);
		}
	};
	
	public static CreativeTabs starcraftGasTab = new CreativeTabs("starcraftGasTab") {
		public ItemStack getIconItemStack() {
			return new ItemStack(SpacecraftBlocks.JupiterNitrogen, 1, 0);
		}
	};

	public static CreativeTabs starcraftPlutoTab = new CreativeTabs("starcraftPlutoTab") {
		public ItemStack getIconItemStack() {
			return new ItemStack(SpacecraftBlocks.PlutoGrass, 1, 0);
		}
	};
	
    public static final String TEXTURE_DOMAIN = "starcraftcore";
    public static final String TEXTURE_PREFIX = StarcraftCore.TEXTURE_DOMAIN + ":";
    
    public static long tick;
    public static long slowTick;
    
    public static HashMap<String, ItemStack> blocksList = new HashMap<String, ItemStack>();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        new SCCoreConfigManager(new File(event.getModConfigurationDirectory(), "starcraft/core.conf"));

//        GCVenusBlocks.initBlocks();
//        GCVenusBlocks.setHarvestLevels();

        SCCoreItems.initItems();

        StarcraftCore.proxy.preInit(event);
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        int languages = 0;

        for (String language : StarcraftCore.LANGUAGES_SUPPORTED)
        {
            LanguageRegistry.instance().loadLocalization(StarcraftCore.LANGUAGE_PATH + language + ".lang", language, false);

            if (LanguageRegistry.instance().getStringLocalization("children", language) != "")
            {
                try
                {
                    String[] children = LanguageRegistry.instance().getStringLocalization("children", language).split(",");

                    for (String child : children)
                    {
                        if (child != "" || child != null)
                        {
                            LanguageRegistry.instance().loadLocalization(StarcraftCore.LANGUAGE_PATH + language + ".lang", child, false);
                            languages++;
                        }
                    }
                }
                catch (Exception e)
                {
                    FMLLog.severe("Failed to load a child language file.");
                    e.printStackTrace();
                }
            }

            languages++;
        }

        GCLog.info("Starcraft Core Loaded: " + languages + " Languages.");

        SchematicRegistry.registerSchematicRecipe(new SCCoreSchematicRocketT3());

        NetworkRegistry.instance().registerGuiHandler(StarcraftCore.instance, StarcraftCore.proxy);
        this.registerTileEntities();
        this.registerCreatures();
        this.registerOtherEntities();
        StarcraftCore.proxy.init(event);
        GalacticraftRegistry.addDungeonLoot(2, new ItemStack(SCCoreItems.schematic, 1, 0));
        
//        CompressorRecipes.addShapelessRecipe(new ItemStack(GCVenusItems.venusItemBasic, 1, 3), new ItemStack(GCCoreItems.heavyPlatingTier1), new ItemStack(GCMoonItems.meteoricIronIngot, 1, 1));
//        CompressorRecipes.addShapelessRecipe(new ItemStack(GCVenusItems.venusItemBasic, 1, 5), new ItemStack(GCVenusItems.venusItemBasic, 1, 2));
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        NetworkRegistry.instance().registerChannel(new SCCorePacketHandlerServer(), StarcraftCore.CHANNEL, Side.SERVER);
    }

    public void registerTileEntities()
    {
    }

    public void registerCreatures()
    {
    }

    public void registerOtherEntities()
    {
    	this.registerGalacticraftNonMobEntity(SCCoreEntityRocketT3.class, "SpaceshipT3", SCCoreConfigManager.idEntitySpaceshipTier3, 150, 1, true);    
		this.registerGalacticraftNonMobEntity(SCCoreEntityRocketT4.class, "SpaceshipT4", SCCoreConfigManager.idEntitySpaceshipTier4, 150, 1, true);    
		this.registerGalacticraftNonMobEntity(SCCoreEntityRocketT5.class, "SpaceshipT5", SCCoreConfigManager.idEntitySpaceshipTier5, 150, 1, true);    
    }

    @EventHandler
    public void postLoad(FMLPostInitializationEvent event)
    {
        StarcraftCore.proxy.postInit(event);
        StarcraftCore.proxy.registerRenderInformation();
        SCCoreRecipeManager.loadRecipes();
    }

    public void registerGalacticraftCreature(Class<? extends Entity> var0, String var1, int id, int back, int fore)
    {
        EntityRegistry.registerGlobalEntityID(var0, var1, id, back, fore);
        EntityRegistry.registerModEntity(var0, var1, id, StarcraftCore.instance, 80, 3, true);
    }

    public void registerGalacticraftNonMobEntity(Class<? extends Entity> var0, String var1, int id, int trackingDistance, int updateFreq, boolean sendVel)
    {
        EntityRegistry.registerModEntity(var0, var1, id, this, trackingDistance, updateFreq, sendVel);
    }
}
