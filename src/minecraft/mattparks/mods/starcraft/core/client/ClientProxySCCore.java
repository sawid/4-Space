package mattparks.mods.starcraft.core.client;

import mattparks.mods.starcraft.core.CommonProxySCCore;
import mattparks.mods.starcraft.core.SCCoreConfigManager;
import mattparks.mods.starcraft.core.StarcraftCore;
import mattparks.mods.starcraft.core.client.model.SCCoreModelSpaceshipTier3;
import mattparks.mods.starcraft.core.client.model.SCCoreModelSpaceshipTier4;
import mattparks.mods.starcraft.core.client.model.SCCoreModelSpaceshipTier5;
import mattparks.mods.starcraft.core.client.render.item.SCCoreItemRendererSpaceshipT3;
import mattparks.mods.starcraft.core.client.render.item.SCCoreItemRendererSpaceshipT4;
import mattparks.mods.starcraft.core.client.render.item.SCCoreItemRendererSpaceshipT5;
import mattparks.mods.starcraft.core.client.sounds.SCCoreSounds;
import mattparks.mods.starcraft.core.entities.SCCoreEntityRocketT3;
import mattparks.mods.starcraft.core.entities.SCCoreEntityRocketT4;
import mattparks.mods.starcraft.core.entities.SCCoreEntityRocketT5;
import mattparks.mods.starcraft.core.items.SCCoreItems;
import micdoodle8.mods.galacticraft.core.client.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.client.GCCoreCloudRenderer;
import micdoodle8.mods.galacticraft.core.client.render.entities.GCCoreRenderAlienVillager;
import micdoodle8.mods.galacticraft.core.client.render.entities.GCCoreRenderSpaceship;
import micdoodle8.mods.galacticraft.core.client.render.entities.GCCoreRenderZombie;
import micdoodle8.mods.galacticraft.core.client.render.item.GCCoreItemRendererKey;
import micdoodle8.mods.galacticraft.core.client.sounds.GCCoreSoundUpdaterSpaceship;
import micdoodle8.mods.galacticraft.core.entities.GCCoreEntityAlienVillager;
import micdoodle8.mods.galacticraft.core.entities.GCCoreEntityZombie;
import micdoodle8.mods.galacticraft.core.util.PacketUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class ClientProxySCCore extends CommonProxySCCore
{
    private static int eggRenderID;
    private static int treasureRenderID;
    
    public static ArrayList<SoundPoolEntry> newMusic = new ArrayList<SoundPoolEntry>();
    
    public static Map<String, String> capeMap = new HashMap<String, String>();
    
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new SCCoreSounds());
    }
    
    @Override
    public void init(FMLInitializationEvent event)
    {
        TickRegistry.registerTickHandler(new TickHandlerClient(), Side.CLIENT);
        NetworkRegistry.instance().registerChannel(new ClientPacketHandler(), StarcraftCore.CHANNEL, Side.CLIENT);
        ClientProxySCCore.eggRenderID = RenderingRegistry.getNextAvailableRenderId();

        //Blue Cape
        String capeBlueString = "https://raw.github.com/mattparks/Starcraft-2/master/capes/capeBlue.png";
        //Green Cape
        String capeGreenString = "https://raw.github.com/mattparks/Starcraft-2/master/capes/capeGreen.png";
        //Orange Cape
        String capeOrangeString = "https://raw.github.com/mattparks/Starcraft-2/master/capes/capeOrange.png";
        //Red Cape
        String capeRedString = "https://raw.github.com/mattparks/Starcraft-2/master/capes/capeRed.png";
        //Violet Cape
        String capeVioletString = "https://raw.github.com/mattparks/Starcraft-2/master/capes/capeViolet.png";
        //Yellow Cape
        String capeYellowString = "https://raw.github.com/mattparks/Starcraft-2/master/capes/capeYellow.png";

        ClientProxyCore.capeMap.put("mattparks", capeBlueString);     
        ClientProxyCore.capeMap.put("flashy3", capeOrangeString); 
        ClientProxyCore.capeMap.put("dinammar", capeVioletString); 
        ClientProxyCore.capeMap.put("imac123456", capeOrangeString); 
        
        ClientProxyCore.capeMap.put("ghostheart305", capeRedString); 
        ClientProxyCore.capeMap.put("langjam350roxsox", capeBlueString); 
        ClientProxyCore.capeMap.put("_Ja1m3", capeRedString); 
        ClientProxyCore.capeMap.put("goldenkat99", capeOrangeString); 

    }

    @Override
    public void registerRenderInformation()
    {
        IModelCustom cargoRocketModel = AdvancedModelLoader.loadModel("/assets/galacticraftmars/models/cargoRocket.obj");
        RenderingRegistry.registerEntityRenderingHandler(SCCoreEntityRocketT3.class, new GCCoreRenderSpaceship(new SCCoreModelSpaceshipTier3(), StarcraftCore.TEXTURE_DOMAIN, "rocketT3"));
        RenderingRegistry.addNewArmourRendererPrefix("gem");
        MinecraftForgeClient.registerItemRenderer(SCCoreItems.spaceshipT3.itemID, new SCCoreItemRendererSpaceshipT3(cargoRocketModel));
    
        RenderingRegistry.registerEntityRenderingHandler(SCCoreEntityRocketT4.class, new GCCoreRenderSpaceship(new SCCoreModelSpaceshipTier4(), StarcraftCore.TEXTURE_DOMAIN, "rocketT4"));
        MinecraftForgeClient.registerItemRenderer(SCCoreItems.spaceshipT4.itemID, new SCCoreItemRendererSpaceshipT4(cargoRocketModel));
        
        RenderingRegistry.registerEntityRenderingHandler(SCCoreEntityRocketT5.class, new GCCoreRenderSpaceship(new SCCoreModelSpaceshipTier5(), StarcraftCore.TEXTURE_DOMAIN, "rocketT5"));
        MinecraftForgeClient.registerItemRenderer(SCCoreItems.spaceshipT5.itemID, new SCCoreItemRendererSpaceshipT5(cargoRocketModel));
    }


    @Override
    public int getEggRenderID()
    {
        return ClientProxySCCore.eggRenderID;
    }

    @Override
    public int getTreasureRenderID()
    {
        return ClientProxySCCore.treasureRenderID;
    }


    @Override
    public void spawnParticle(String var1, double var2, double var4, double var6)
    {
        final Minecraft var14 = FMLClientHandler.instance().getClient();

        if (var14 != null && var14.renderViewEntity != null && var14.effectRenderer != null)
        {
            final double var15 = var14.renderViewEntity.posX - var2;
            final double var17 = var14.renderViewEntity.posY - var4;
            final double var19 = var14.renderViewEntity.posZ - var6;
            Object var21 = null;
            final double var22 = 64.0D;

            if (var15 * var15 + var17 * var17 + var19 * var19 < var22 * var22)
            {
                if (var1.equals("sludgeDrip"))
                {
                }
            }
        }
    }

    public class ClientPacketHandler implements IPacketHandler
    {
        @Override
        public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player p)
        {
            final DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
            final int packetType = PacketUtil.readPacketID(data);
            EntityPlayer player = (EntityPlayer) p;

            if (packetType == 0)
            {
                final Class<?>[] decodeAs = { Integer.class, Integer.class, Integer.class };
                final Object[] packetReadout = PacketUtil.readPacketData(data, decodeAs);

                int entityID = 0;
                Entity entity = null;
                
                switch ((Integer) packetReadout[1])
                {
                case 0:
                    entityID = (Integer) packetReadout[2];
                    entity = player.worldObj.getEntityByID(entityID);

                    {
                    }

                    player.openContainer.windowId = (Integer) packetReadout[0];
                    break;
                case 1:
                    entityID = (Integer) packetReadout[2];
                    entity = player.worldObj.getEntityByID(entityID);

                    {
                    }

                    player.openContainer.windowId = (Integer) packetReadout[0];
                    break;
                }
            }
        }
    }

    {
    }

    public static boolean handleLavaMovement(EntityPlayer player)
    {
        return player.worldObj.isMaterialInBB(player.boundingBox.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.lava);
    }

    public static boolean handleWaterMovement(EntityPlayer player)
    {
        return player.worldObj.isMaterialInBB(player.boundingBox.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.water);
    }

    {
    }

    public static class TickHandlerClient implements ITickHandler
    {
        @Override
        public void tickStart(EnumSet<TickType> type, Object... tickData)
        {
            final Minecraft minecraft = FMLClientHandler.instance().getClient();

            final WorldClient world = minecraft.theWorld;

            if (type.equals(EnumSet.of(TickType.CLIENT)))
            {
                if (world != null)
                {
                    {
                        if (world.provider.getSkyRenderer() == null)
                        {
                        }

                        if (world.provider.getCloudRenderer() == null)
                        {
                            world.provider.setCloudRenderer(new GCCoreCloudRenderer());
                        }
                    }

                    for (int i = 0; i < world.loadedEntityList.size(); i++)
                    {
                        final Entity e = (Entity) world.loadedEntityList.get(i);

                        if (e != null)
                        {
                            if (e instanceof SCCoreEntityRocketT3)
                            {
                                final SCCoreEntityRocketT3 eship = (SCCoreEntityRocketT3) e;

                                if (eship.rocketSoundUpdater == null)
                                {
                                    eship.rocketSoundUpdater = new GCCoreSoundUpdaterSpaceship(FMLClientHandler.instance().getClient().sndManager, eship, FMLClientHandler.instance().getClient().thePlayer);
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void tickEnd(EnumSet<TickType> type, Object... tickData)
        {
        }

        @Override
        public String getLabel()
        {
            return "Starcraft Core Client";
        }

        @Override
        public EnumSet<TickType> ticks()
        {
            return EnumSet.of(TickType.CLIENT);
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tile = world.getBlockTileEntity(x, y, z);

        return null;
    }
}
