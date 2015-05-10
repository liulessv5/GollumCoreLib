package com.gollum.core.common.building.handler;

import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.gollum.core.common.blocks.BlockProximitySpawn;
import com.gollum.core.common.tileentities.TileEntityBlockProximitySpawn;

public class BlockProximitySpawnBuildingHandler extends BuildingBlockHandler {
	
	@Override
	protected boolean mustApply (World world, int x, int y, int z, Block block) {
		return block instanceof BlockProximitySpawn;
	}
	
	/**
	 * Insert les extras informations du block
	 * @param rotate	
	 */
	@Override
	public void applyExtra(
		Block block,
		World world,
		Random random, 
		int x, int y, int z, 
		HashMap<String, String> extra,
		int initX, int initY, int initZ, 
		int rotate,
		int dx, int dz,
		int maxX, int maxZ
	) {
		
		TileEntity te  = world.getTileEntity (x, y, z);
		if (te instanceof TileEntityBlockProximitySpawn) {
			String entity = ""; try { entity = extra.get("entity"); } catch (Exception e) {} entity = (entity != null) ? entity : "Chicken";
			((TileEntityBlockProximitySpawn) te).setModId (entity);
		}
	}
	
}