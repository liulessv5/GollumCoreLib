package com.gollum.core.client.renderer;

import static com.gollum.core.ModGollumCoreLib.log;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.lwjgl.opengl.GL11;

import com.gollum.core.client.event.RenderItemEvent;
import com.gollum.core.client.event.RenderItemIntoGuiEvent;
import com.gollum.core.client.handlers.ISimpleBlockRenderingHandler;
import com.gollum.core.common.blocks.ISimpleBlockRendered;
import com.gollum.core.tools.registry.RenderingRegistry;
import com.gollum.core.utils.math.Integer2d;
import com.gollum.core.utils.reflection.Reflection;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class GCLRenderItem extends RenderItem {
	
	private static GCLRenderItem instance = null;
	
	public boolean renderWithColor = true;
	public GLCRenderBlocks renderBlocksRi = new GLCRenderBlocks();
	
	protected GCLRenderItem(TextureManager textureManager, ModelManager modelManager) {
		super(textureManager, modelManager);
	}
	
	public static void override () {
		if (instance == null) {
			log.message("Override RenderItem...");
			instance = new GCLRenderItem(Minecraft.getMinecraft().getRenderItem());
			
			try {
				boolean found = false;
				for (Field f: Minecraft.class.getDeclaredFields()) {
					f.setAccessible(true);
					if (f.getType() == RenderItem.class) {
						f.set(Minecraft.getMinecraft(), instance);
						log.message("Override RenderItem OK");
						return;
					}
				}
				log.severe("Override RenderItem KO: "+RenderItem.class.getCanonicalName()+" not found in "+Minecraft.class.getCanonicalName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private RenderItem proxy;
	private boolean isInit = false;
	
	public GCLRenderItem(RenderItem proxy) {
		super(getResourceManager(), getTextureMapBlocks());
		this.proxy = proxy;
		this.setProxyField();
		this.isInit = true;
	}
	
	protected static TextureManager getResourceManager () {
		return new TextureManager(Minecraft.getMinecraft().getResourceManager());
	}
	
	protected static ModelManager getTextureMapBlocks () {
		return new ModelManager(Minecraft.getMinecraft().getTextureMapBlocks());
	}
	
	private void setProxyField() {
		try {
			for (Field f: RenderItem.class.getDeclaredFields()) {
				f.setAccessible(true);
				if((f.getModifiers() & Modifier.STATIC) != Modifier.STATIC) {
					if((f.getModifiers() & Modifier.FINAL) == Modifier.FINAL) {
						Reflection.setFinalField(f, this, f.get(this.proxy));
					} else {
						f.set(this, f.get(this.proxy));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void registerItem(Item itm, int subType, String identifier) {
		if (this.isInit) {
			this.registerItem(itm, subType, identifier);
		}
	}
	

	public void renderItem(ItemStack stack, IBakedModel model) {
		
		boolean rendered = false;
		
		RenderItemEvent event = new RenderItemEvent.Pre(this, stack, model);
		if (event.isCanceled()) {
			return;
		}
		stack = event.itemStack;
		model = event.model;
		
		Block block = Block.getBlockFromItem(stack.getItem());
		if (block instanceof ISimpleBlockRendered) {
			int modelId = ((ISimpleBlockRendered) block).getGCLRenderType();
			ISimpleBlockRenderingHandler renderHandler = RenderingRegistry.getBlockHandler(modelId);
			if (renderHandler != null) {
				this.renderBlocksRi.useInventoryTint = this.renderWithColor;
				this.renderBlocksRi.renderBlockAsItem(block, stack.getItemDamage(), 1.0F);
				this.renderBlocksRi.useInventoryTint = true;
				rendered = true;
			}
		}
		
		if (!rendered) {
			super.renderItem(stack, model);
		}
		
		event = new RenderItemEvent.Post(this, stack, model);
		MinecraftForge.EVENT_BUS.post(event);
	}
	
	@Override
	public void renderItemIntoGUI(ItemStack stack, int x, int y) {
		
		RenderItemIntoGuiEvent event = new RenderItemIntoGuiEvent.Pre(this, stack, new Integer2d(x, y));
		MinecraftForge.EVENT_BUS.post(event);  
		if (event.isCanceled()) {
			return;
		}
		x = event.pos.x;
		y = event.pos.y;
		stack = event.itemStack;
		
		super.renderItemIntoGUI(stack, x, y);
		
		event = new RenderItemIntoGuiEvent.Post(this, stack, new Integer2d(x, y));
		MinecraftForge.EVENT_BUS.post(event);
	}
}
