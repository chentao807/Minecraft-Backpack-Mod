package backpack;

import java.util.List;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import cpw.mods.fml.common.FMLCommonHandler;

public class BackpackItem extends Item {
	// the names of all backpacks
	static final String[] backpackNames = {
			"Backpack", "Red Backpack", "Green Backpack", "Brown Backpack", "Blue Backpack",
			"Purple Backpack", "Cyan Backpack", "Light Gray Backpack", "Gray Backpack",
			"Pink Backpack", "Lime Green Backpack", "Yellow Backpack", "Light Blue Backpack",
			"Magenta Backpack", "Orange Backpack", "White Backpack", "Ender Backpack"
	};

	// the damage of an magic backpack
	public static final int ENDERBACKPACK = 31999;

	/**
	 * Creates an instance of the backpack item and sets some default values.
	 * 
	 * @param id
	 *            The item id.
	 */
	protected BackpackItem(int id) {
		super(id);
		setIconIndex(0);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setItemName("backpack");
		setCreativeTab(CreativeTabs.tabMisc);
	}

	/**
	 * Returns the image with the items.
	 * 
	 * @return The path to the item file.
	 */
	@Override
	public String getTextureFile() {
		return CommonProxy.ITEMS_PNG;
	}

	/**
	 * Returns the icon index based on the item damage.
	 * 
	 * @param damage
	 *            The damage to check for.
	 * @return The icon index.
	 */
	@Override
	public int getIconFromDamage(int damage) {
		if(damage >= 0 && damage < 16) {
			return damage;
		}
		if(damage == ENDERBACKPACK) {
			return 16;
		}
		return 0;
    }

	/**
	 * Returns the sub items.
	 * 
	 * @param unknown
	 *            unknown
	 * @param tab
	 *            A creative tab.
	 * @param A
	 *            List which stores the sub items.
	 */
	@Override
	public void getSubItems(int unknown, CreativeTabs tab, List subItems) {
		for(int i = 0; i < 16; i++) {
			subItems.add(new ItemStack(this, 1, i));
		}
		subItems.add(new ItemStack(this, 1, ENDERBACKPACK));
	}

	/**
	 * Gets item name based on the ItemStack.
	 * 
	 * @param itemstack
	 *            The ItemStack to use for check.
	 * @return The name of the backpack.
	 */
	@Override
	public String getItemNameIS(ItemStack itemstack) {
		if(itemstack.getTagCompound() != null) {
			if(itemstack.getTagCompound().hasKey("display")) {
				return itemstack.getTagCompound().getCompoundTag("display").getString("Name");
			}
		}
		if(itemstack.getItemDamage() >= 0 && itemstack.getItemDamage() < 16) {
			// return
			// StringTranslate.getInstance().translateNamedKey(backpackNames[itemstack.getItemDamage()]);
			return backpackNames[itemstack.getItemDamage()];
		}
		if(itemstack.getItemDamage() == ENDERBACKPACK) {
			return backpackNames[16];
		}
		return backpackNames[0];
	}

	/**
	 * Handles what should be done on right clicking the item.
	 * 
	 * @param is
	 *            The ItemStack which is right clicked.
	 * @param world
	 *            The world in which the player is.
	 * @param player
	 *            The player who right clicked the item.
	 * @param Returns
	 *            the ItemStack after the process.
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player) {
		// if world.isRemote than we are on the client side
		if(world.isRemote) {
			// display rename GUI if player is sneaking
			if(player.isSneaking() && is.getItemDamage() != BackpackItem.ENDERBACKPACK) {
				FMLCommonHandler.instance().showGuiScreen(new BackpackGui(player));
			}
			return is;
		}

		// when the player is not sneaking
		if(!player.isSneaking()) {
			// get the inventory
			IInventory inv;
			if(is.getItemDamage() == BackpackItem.ENDERBACKPACK) {
				inv = player.getInventoryEnderChest();
			} else {
				inv = new BackpackInventory(player, is);
			}

			// open the GUI for a chest based on the loaded inventory
			player.displayGUIChest(inv);
		}
		return is;
	}

	/**
	 * Returns the item name to display in the tooltip.
	 * @param itemstack The ItemStack to use for check.
	 * @return The name of the backpack for the tooltip.
	 */
	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		// it ItemStack has a NBTTagCompound load name from inventory title.
		if(itemstack.hasTagCompound()) {
			if(itemstack.getTagCompound().hasKey("Inventory")) {
				return itemstack.getTagCompound().getCompoundTag("Inventory").getString("title");
			}
		}
		// else if damage is between 0 and 15 return name from backpackNames array
		if(itemstack.getItemDamage() >= 0 && itemstack.getItemDamage() < 16) {
			return backpackNames[itemstack.getItemDamage()];
		}
		// else if damage is equal to ENDERBACKPACK then return backpackNames index 16
		if(itemstack.getItemDamage() == ENDERBACKPACK) {
			return backpackNames[16];
		}

		// return index 0 of backpackNames array as fallback
		return backpackNames[0];
	}
}
