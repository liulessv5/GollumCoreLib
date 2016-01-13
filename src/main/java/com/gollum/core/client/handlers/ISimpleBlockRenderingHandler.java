package com.gollum.core.client.handlers;

import com.gollum.core.client.renderer.GCLRenderItem;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderItem;

public interface ISimpleBlockRenderingHandler {

	void renderInventoryBlock(Block block, int metadata, int modelID, RenderItem renderItem);

}
