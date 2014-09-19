package mods.gollum.core.client.gui.config;

import static cpw.mods.fml.client.config.GuiUtils.RESET_CHAR;
import static cpw.mods.fml.client.config.GuiUtils.UNDO_CHAR;
import static mods.gollum.core.ModGollumCoreLib.log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiConfigEntries.IConfigEntry;
import mods.gollum.core.common.config.ConfigProp;
import mods.gollum.core.common.config.ConfigLoader.ConfigLoad;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;

public class GuiConfigEntries extends GuiListExtended {
	
	private ArrayList<ConfigEntry> entries;
	private GuiConfigMod parent;
	private Minecraft mc;
	private ConfigLoad configLoad;

	//////////////
	// Position //
	//////////////
	
	public int maxEntryRightBound = 0;
	public int maxLabelTextWidth = 0;
	public int labelX;
	public int controlX;
	public int resetX;
	public int controlWidth;
	public int scrollBarX;
	
	public GuiConfigEntries(GuiConfigMod parent, Minecraft mc, ConfigLoad configLoad) {
		
		super(mc, parent.width, parent.height, 23, parent.height - 32, 20);
		
		this.parent     = parent;
		this.mc         = mc;
		this.configLoad = configLoad;
		
		this.entries = new ArrayList<ConfigEntry>();
		
		
		if (configLoad.config != null) {
			for (Field f : configLoad.config.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				
				ConfigProp anno = f.getAnnotation(ConfigProp.class);
				if (anno != null) {
					
					// TODO généraliser aux autres champs
					if (
						f.getType().isAssignableFrom(Long.TYPE) ||
						f.getType().isAssignableFrom(Integer.TYPE)
					) {
						
						String label = this.getLabel (f.getName());
						
						try {
							
							entries.add(new ConfigEntry(this, label, Long.parseLong (f.get(configLoad.config).toString())) {
								@Override
								protected void onSave() {
									log.debug ("Save entity");
								}
							});
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
			}
		}
		
		this.initGui();
	}
	
	protected void initGui() {
		this.width  = parent.width;
		this.height = parent.height;
		
		this.maxLabelTextWidth = 0;
		for (ConfigEntry entry : this.entries) {
			this.maxLabelTextWidth = Math.max (this.maxLabelTextWidth, this.mc.fontRenderer.getStringWidth(entry.label));
		}
		
		this.top = 23;
		this.bottom = parent.height - 32;
		this.left = 0;
		this.right = width;

		int viewWidth = this.maxLabelTextWidth + 8 + (this.width / 2);
		this.labelX   = (this.width / 2) - (viewWidth / 2);
		this.controlX = this.labelX + this.maxLabelTextWidth + 8;
		this.resetX   = (this.width / 2) + (viewWidth / 2) - 45;
		
		this.maxEntryRightBound = 0;
		for (ConfigEntry entry : this.entries) {
			this.maxEntryRightBound = Math.max(this.maxEntryRightBound, entry.getEntryRightBound());
		}
		
		scrollBarX = this.maxEntryRightBound + 5;
		controlWidth = maxEntryRightBound - controlX - 45;

	}
	
	public String getLabel (String name) {
		return this.configLoad.mod.i18n().trans("config."+name);
	}
	
	@Override
	public IGuiListEntry getListEntry(int i) {
		return this.entries.get(i);
	}

	@Override
	protected int getSize() {
		return this.entries.size();
	}

	public void updateScreen() {
		for (ConfigEntry entry : this.entries) {
			entry.updateCursorCounter();
		}
	}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseEvent) {
		for (ConfigEntry entry : this.entries) {
			entry.mouseClicked(mouseX, mouseY, mouseEvent);
		}
	}
	
	public void keyTyped(char eventChar, int eventKey) {
		 for (ConfigEntry entry : this.entries) {
			 entry.keyTyped(eventChar, eventKey);
		}
	}
	
	@Override
	public int getScrollBarX() {
		return scrollBarX;
	}
	
	@Override
	public int getListWidth() {
		return parent.width;
	}


	public boolean isDefault() {
		for (ConfigEntry entry : this.entries) {
			if (!entry.isDefault()) {
				return false;
			}
		}
		return true;
	}

	public boolean isChanged() {
		for (ConfigEntry entry : this.entries) {
			if (entry.isChanged()) {
				return true;
			}
		}
		return false;
	}
	
	public abstract class ConfigEntry implements IGuiListEntry {
		
		public GuiConfigEntries parent;
		public String label;
		
		private boolean isInit = false;
		
		protected final GuiButtonExt btnUndoChanges;
		protected final GuiButtonExt btnDefault;
		private GuiTextField textFieldValue;
		
		public ConfigEntry (GuiConfigEntries parent, String label, Long value) {
			this.parent = parent;
			this.label = label;
			
			this.btnUndoChanges = new GuiButtonExt(0, 0, 0, 18, 18, UNDO_CHAR);
			this.btnDefault     = new GuiButtonExt(0, 0, 0, 18, 18, RESET_CHAR);
			
			this.textFieldValue = new GuiTextField(this.parent.mc.fontRenderer, this.parent.controlX + 1, 0, this.parent.controlWidth - 3, 16);
			this.textFieldValue.setMaxStringLength(10000);
			this.textFieldValue.setText(value.toString());
			
			
		}

		@Override
		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, Tessellator tessellator, int mouseX, int mouseY, boolean isSelected) {
			
			EnumChatFormatting color = EnumChatFormatting.GRAY;
			color = (!isValidValue()) ? EnumChatFormatting.RED   : color;
			color = (isChanged())     ? EnumChatFormatting.WHITE : color;
			
			this.parent.mc.fontRenderer.drawString(color+this.label, this.parent.labelX, y + slotHeight / 2 - this.parent.mc.fontRenderer.FONT_HEIGHT / 2, 0x000000);
			
			
			this.btnUndoChanges.xPosition = this.parent.scrollBarX - 44;
			this.btnUndoChanges.yPosition = y;
			this.btnUndoChanges.enabled = this.isChanged ();
			this.btnUndoChanges.drawButton(this.parent.mc, mouseX, mouseY);

			this.btnDefault.xPosition = this.parent.scrollBarX - 22;
			this.btnDefault.yPosition = y;
			this.btnDefault.enabled = !this.isDefault();
			this.btnDefault.drawButton(this.parent.mc, mouseX, mouseY);
			
			this.textFieldValue.xPosition = this.parent.controlX + 2;
			this.textFieldValue.yPosition = y + 1;
			this.textFieldValue.width = this.parent.controlWidth - 4;
			this.textFieldValue.drawTextBox();
			
			// Fixe display text 
			if (!isInit) { this.textFieldValue.setCursorPosition(this.textFieldValue.getCursorPosition()); isInit = true; }
			this.textFieldValue.drawTextBox();
		}
		
		public void updateCursorCounter() {
			this.textFieldValue.updateCursorCounter();
		}
		
		public void mouseClicked(int x, int y, int mouseEvent) {
			this.textFieldValue.mouseClicked(x, y, mouseEvent);
		}
		
		public void keyTyped(char eventChar, int eventKey) {
//			if (eventKey == Keyboard.KEY_LEFT || eventKey == Keyboard.KEY_RIGHT || eventKey == Keyboard.KEY_HOME || eventKey == Keyboard.KEY_END) {
			
			this.textFieldValue.textboxKeyTyped(eventChar, eventKey);
			
		}
		
		public int getEntryRightBound() {
			return this.parent.resetX + 40;
		}
		
		public boolean isChanged() {
			// TODO Auto-generated method stub
			return false;
		}
		
		public boolean isDefault() {
			// TODO Auto-generated method stub
			return true;
		}
		
		public boolean isValidValue() {
			return true;
		}
		
		@Override
		public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY) {
			if (this.btnDefault.mousePressed(this.parent.mc, x, y)) {
				btnDefault.func_146113_a(mc.getSoundHandler());
//				setToDefault(); // TODO
				return true;
			} else if (this.btnUndoChanges.mousePressed(this.parent.mc, x, y)) {
				btnUndoChanges.func_146113_a(mc.getSoundHandler());
//				undoChanges(); // TODO
				return true;
			}
			return false;
		}


		@Override
		public void mouseReleased(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_) {
			// TODO Auto-generated method stub
			
		}
		
		protected abstract void onSave ();
		
	}
	
}
