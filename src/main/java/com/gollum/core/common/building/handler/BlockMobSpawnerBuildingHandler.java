package com.gollum.core.common.building.handler;

import java.util.HashMap;

import com.gollum.core.common.building.Building.EnumRotate;

import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockMobSpawnerBuildingHandler extends BuildingBlockHandler {
	
	@Override
	protected boolean mustApply (World world, BlockPos pos, IBlockState state) {
		return 
			state != null && state.getBlock() instanceof BlockMobSpawner;
	}
	
	@Override
	protected void applyExtra(
		World world,
		BlockPos pos,
		IBlockState state,
		HashMap<String, String> extra,
		BlockPos initPos,
		EnumRotate rotate,
		int maxX, int maxZ
	) {
		TileEntity te  = world.getTileEntity (pos);
		if (te instanceof TileEntityMobSpawner) {
			String entity = ""; try { entity = extra.get("entity"); } catch (Exception e) {} entity = (entity != null) ? entity : "Pig";
			((TileEntityMobSpawner) te).getSpawnerBaseLogic().setEntityName(entity);
		}
	}
	
}
