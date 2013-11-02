package mattparks.mods.starcraft.core.items;

import java.util.List;

import mattparks.mods.starcraft.core.StarcraftCore;
import micdoodle8.mods.galacticraft.core.client.ClientProxyCore;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SCCoreItemRocketParts extends Item
{
    public static final String[] names = { "plateHeavyT3", "plateHeavyT4", "plateHeavyT5", "noseNoneT3", "noseNoneT4", "noseNoneT5", "tier3engine", "tier2booster", "tier4engine", "tier3booster", "tier5engine", "tier4booster" };

    protected Icon[] icons = new Icon[SCCoreItemRocketParts.names.length];

    public SCCoreItemRocketParts(int id, String assetName)
    {
        super(id);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setUnlocalizedName(assetName);
        this.setTextureName(StarcraftCore.ASSET_PREFIX + assetName);
    }

    @Override
    public CreativeTabs getCreativeTab()
    {
        return StarcraftCore.starcraftRocketsTab;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        return ClientProxyCore.galacticraftItem;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister)
    {
        int i = 0;

        for (final String name : SCCoreItemRocketParts.names)
        {
            this.icons[i++] = iconRegister.registerIcon(this.getIconString() + "." + name);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return this.getUnlocalizedName() + "." + SCCoreItemRocketParts.names[itemStack.getItemDamage()];
    }

    @Override
    public Icon getIconFromDamage(int damage)
    {
        if (this.icons.length > damage)
        {
            return this.icons[damage];
        }

        return super.getIconFromDamage(damage);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < SCCoreItemRocketParts.names.length; i++)
        {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }

    @Override
    public int getMetadata(int par1)
    {
        return par1;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        if (par2EntityPlayer.worldObj.isRemote)
        {
            switch (par1ItemStack.getItemDamage())
            {
            case 0:
                par3List.add(LanguageRegistry.instance().getStringLocalization("item.tier3.desc"));
                break;
            case 1:
                par3List.add(LanguageRegistry.instance().getStringLocalization("item.tier4.desc"));
                break;
            case 2:
                par3List.add(LanguageRegistry.instance().getStringLocalization("item.tier5.desc"));
                break;
            }
        }
    }
}