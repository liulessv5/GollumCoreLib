package com.gollum.core.common.building.handler;

import com.gollum.core.ModGollumCoreLib;
import com.gollum.core.common.building.Building.Unity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockTorch;
import net.minecraft.world.World;

public class BlockDirectionalWithNoneBuildingHandler extends BuildingBlockHandler {
	
	@Override
	protected boolean mustApply (World world, int x, int y, int z, Block block) {
		return
			block instanceof BlockTorch ||
			block instanceof BlockButton
		;
	}
	
	@Override
	public void applyOrientation(World world, int x, int y, int z, Block block, int metadata, int orientation, int rotate) {
		/* FIXME
		if (orientation == Unity.ORIENTATION_NONE)  { metadata = (metadata & 0x8) + 0; } else 
		if (orientation == Unity.ORIENTATION_UP)    { metadata = (metadata & 0x8) + 4; } else 
		if (orientation == Unity.ORIENTATION_DOWN)  { metadata = (metadata & 0x8) + 3; } else 
		if (orientation == Unity.ORIENTATION_LEFT)  { metadata = (metadata & 0x8) + 2; } else 
		if (orientation == Unity.ORIENTATION_RIGTH) { metadata = (metadata & 0x8) + 1; } else 
		{
			ModGollumCoreLib.log.severe("Bad orientation : "+orientation+" name:"+block.getUnlocalizedName()+" pos:"+x+","+y+","+z);
		}
		
		world.setBlockMetadataWithNotify(x, y, z, metadata, 0);
		return;
		*/
	}
	
}