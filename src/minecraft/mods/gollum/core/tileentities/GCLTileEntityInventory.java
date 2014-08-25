package mods.gollum.core.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public abstract class GCLTileEntityInventory extends TileEntity implements IInventory {

	protected ItemStack[] inventory;
	protected int maxSize;

	/**
	 * Nombre de player utilisant le coffre
	 */
	protected int numUsingPlayers;
	
	public GCLTileEntityInventory(int maxSize) {
		super();
		this.maxSize   = maxSize;
		this.inventory = new ItemStack[maxSize];
	}
	
	///////////////
	// Inventory //
	///////////////
	
	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		return this.inventory.length;
	}
	
	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int slot) {
		
		if (slot >= this.getSizeInventory ()) return null;
		
		return  this.inventory[slot];
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be
	 * 64, possibly will be extended. *Isn't this more of a set than a get?*
	 */
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	
	/**
	 * Removes from an inventory slot (first arg) up to a specified number
	 * (second arg) of items and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int slot, int number) {
		
		if (slot >= this.getSizeInventory ()) return null;
		
		ItemStack itemStack = null;
		
		if (this.inventory[slot] != null) {
			number = Math.min (number, this.inventory[slot].stackSize);
			itemStack = this.inventory[slot].splitStack(number);
			
			if (this.inventory[slot].stackSize == 0) {
				this.inventory[slot] = null;
			}
		}
		
		return itemStack;
	}

	/**
	 * When some containers are closed they call this on each slot, then drop
	 * whatever it returns as an EntityItem - like when you close a workbench
	 * GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {

		if (slot >= this.getSizeInventory ()) return null;
		
		ItemStack itemstack = null;

		if (this.inventory[slot] != null) {
			itemstack = this.inventory[slot];
			this.inventory[slot] = null;
			
		}
		return itemstack;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {

		if (slot >= this.getSizeInventory ());
		
		this.inventory[slot] = itemStack;
		
		if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit()) {
			itemStack.stackSize = this.getInventoryStackLimit();
		}

		this.onInventoryChanged();
	}
	
	public void openChest() {
		++this.numUsingPlayers;
	}

	public void closeChest() {
		--this.numUsingPlayers;
	}
	
	/**
	 * If this returns false, the inventory name will be used as an unlocalized
	 * name, and translated into the player's language. Otherwise it will be
	 * used directly.
	 */
	public boolean isInvNameLocalized() {
		return true;
	}
	
	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring
	 * stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		// TODO a revoir
		return true;
	}
	
	/**
	 * Do not make give this method the name canInteractWith because it clashes
	 * with Container
	 */
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
		
		boolean rtn = false;
		if (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) == this) {
			rtn = par1EntityPlayer.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
		}
		return rtn;
	}
	
	
	////////////////
	// Save datas //
	////////////////

	protected void readItems (NBTTagCompound nbtTagCompound, String tagName) {
		this.readItems(nbtTagCompound, tagName, false);
	}
	
	protected void readItems (NBTTagCompound nbtTagCompound, String tagName, boolean merge) {
		
		if (!nbtTagCompound.hasKey (tagName)) {
			return;
		}
		
		NBTTagList nbttaglist = nbtTagCompound.getTagList(tagName);
		
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			try {
				NBTTagCompound nbttagcompound = (NBTTagCompound) nbttaglist.tagAt(i);
				int j = nbttagcompound.getByte("Slot");
				
				if (j >= 0 && j < this.inventory.length) {
					
					ItemStack itemStack = ItemStack.loadItemStackFromNBT(nbttagcompound);
					
					if (!merge || itemStack != null) {
						this.inventory[j] = itemStack;
					}
				}
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * Reads a tile entity from NBT.
	 */
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		
		this.inventory = new ItemStack[this.maxSize];
		this.readItems(nbtTagCompound, "Items");
		
	}
	
	
	protected void writeItems (NBTTagCompound nbtTagCompound, String tagName, ItemStack[] inventory) {
		
		NBTTagList nbttaglist = new NBTTagList();
		
		for (int i = 0; i < this.getSizeInventory(); ++i) {
			if (this.inventory[i] != null) {
				NBTTagCompound subNBTTagCompound = new NBTTagCompound();
				subNBTTagCompound.setByte("Slot", (byte) i);
				this.inventory[i].writeToNBT(subNBTTagCompound);
				nbttaglist.appendTag(subNBTTagCompound);
			}
		}

		nbtTagCompound.setTag(tagName, nbttaglist);
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		
		this.writeItems(nbtTagCompound, "Items", inventory);
		
	}
	
	

	

}