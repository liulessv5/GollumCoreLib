package mods.gollum.core.tools.helper.blocks;

import java.util.List;

import mods.gollum.core.tools.helper.BlockMetadataHelper;
import mods.gollum.core.tools.helper.IBlockMetadataHelper;
import mods.gollum.core.tools.helper.items.HMetadataItemBlock;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public abstract class HBlockContainerMetadata extends HBlockContainer implements IBlockMetadataHelper {

	public HBlockContainerMetadata(int id, String registerName, Material material) {
		super(id, registerName, material);
		this.helper = new BlockMetadataHelper(this, registerName);
		this.setItemBlockClass(HMetadataItemBlock.class);
	}
	
	public HBlockContainerMetadata(int id, String registerName, Material material, int listSubBlock[]) {
		super(id, registerName, material);
		this.helper = new BlockMetadataHelper(this, registerName, listSubBlock);
		this.setItemBlockClass(HMetadataItemBlock.class);
	}
	
	public HBlockContainerMetadata(int id, String registerName, Material material, int numberSubBlock) {
		super(id, registerName, material);
		this.helper = new BlockMetadataHelper(this, registerName, numberSubBlock);
		this.setItemBlockClass(HMetadataItemBlock.class);
	}
	
	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood
	 * returns 4 blocks)
	 */
	@Override
	public void getSubBlocks(int id, CreativeTabs ctabs, List list) {
		((IBlockMetadataHelper) this.helper).getSubBlocks(id, ctabs, list);
	}
	
	/**
	 * Determines the damage on the item the block drops. Used in cloth and
	 * wood.
	 */
	@Override
	public int damageDropped(int damage) {
		return (helper.vanillaDamageDropped) ? super.damageDropped(damage) : ((IBlockMetadataHelper) this.helper).damageDropped (damage);
	}
	
	/**
	 * Called when a user uses the creative pick block button on this block
	 * 
	 * @param target
	 *            The full target the player is looking at
	 * @return A ItemStack to add to the player's inventory, Null if nothing
	 *         should be added.
	 */
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return (helper.vanillaPicked) ? super.getPickBlock(target, world, x, y, z) : ((IBlockMetadataHelper) helper).getPickBlock(target, world, x, y, z);
	}

	/**
	 * Liste des metadata enabled pour le subtype
	 */
	public boolean[] listSubEnabled () {
		return ((IBlockMetadataHelper) this.helper).listSubEnabled();
	}
	
	public int getEnabledMetadata (int dammage) {
		return ((IBlockMetadataHelper) this.helper).getEnabledMetadata(dammage);
	}
}
