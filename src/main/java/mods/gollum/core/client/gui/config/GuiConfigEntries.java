package mods.gollum.core.client.gui.config;

import static mods.gollum.core.ModGollumCoreLib.log;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import mods.gollum.core.client.gui.config.element.ConfigElement;
import mods.gollum.core.client.gui.config.entry.ConfigEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;

public class GuiConfigEntries extends GuiListExtended {

	protected Minecraft mc;
	public GuiConfig parent;
		
	private ArrayList<ConfigEntry> listEntries = new ArrayList<ConfigEntry>();
	
	public int labelX;
	public int controlX;
	public int resetX;
	public int scrollBarX;
	public int controlWidth;
	
	public GuiConfigEntries(GuiConfig parent, Minecraft mc) {
		super(mc, parent.width, parent.height, parent.titleLine2 != null ? 33 : 23, parent.height - 32, 20);

		this.mc = mc;
		this.parent = parent;
		
		this.setShowSelectionBox(false);
		
		for (ConfigElement configElement : this.parent.configElements) {
			if (configElement != null) {
				if (configElement.getConfigProp().show()) {
					try {
						if (configElement.getEntryClass() != null) {
							
							ConfigEntry configEntry = configElement.getEntryClass().getConstructor(Minecraft.class, GuiConfigEntries.class, ConfigElement.class).newInstance(this.mc, this, configElement);
							log.debug("Create config entry : "+configElement.getName()+" : "+configElement.getEntryClass().getName());
							
							listEntries.add(configEntry);
						} else {
							log.warning("ConfigElement "+configElement.getName()+" hasn't class entry");
						}
					} catch (Exception e) {
						e.printStackTrace();
						log.severe("Can create config entry : "+configElement.getName()+" : "+configElement.getEntryClass().getName());
					}
				}
			} else {
				log.severe("Can create config entry because ConfigElement is null");
			}
		}
		
	}

	@Override
	protected void elementClicked(int p_148144_1_, boolean p_148144_2_, int p_148144_3_, int p_148144_4_) {}

	@Override
	protected boolean isSelected(int p_148131_1_) {
		return false;
	}

	@Override
	protected void drawBackground() {}

	@Override
	protected void drawSlot(int p_148126_1_, int p_148126_2_, int p_148126_3_, int p_148126_4_, Tessellator p_148126_5_, int p_148126_6_, int p_148126_7_) {
		this.getListEntry(p_148126_1_).drawEntry(p_148126_1_, p_148126_2_, p_148126_3_, this.getListWidth(), p_148126_4_, p_148126_5_, p_148126_6_, p_148126_7_, this.func_148124_c(p_148126_6_, p_148126_7_) == p_148126_1_);
	}
	
	@Override
	protected int getSize() {
		return this.listEntries.size();
	}
	
	public IGuiListEntry getListEntry(int index) {
		return (IGuiListEntry) this.listEntries.get(index);
	}

	@Override
	public int getScrollBarX() {
		return this.scrollBarX;
	}
	
	/**
	 * Gets the width of the list
	 */
	@Override
	public int getListWidth() {
		return this.width;
	}
	
	public int getMaxLabelSizeEntry () {
		int maxSizeEntry = 0;
		for (ConfigEntry entry : this.listEntries) {
			
			int size = entry.getLabelWidth();
			
			if (maxSizeEntry < size) {
				maxSizeEntry = size;
			}
		}
		return maxSizeEntry;
	}
	
	public void initGui() {
		
		this.width = this.parent.width;
		this.height = this.parent.height;
		
		this.top    = this.parent.titleLine2 != null ? 33 : 23;
		this.bottom = this.parent.height - 32;
		this.left   = 0;
		this.right  = width;
		
		int maxLabel = this.getMaxLabelSizeEntry();
		
		int viewWidth = maxLabel + 8 + (width / 2);
		
		this.labelX = (this.width / 2) - (viewWidth / 2);
		this.controlX = this.labelX + maxLabel + 8;
		this.resetX = (this.width / 2) + (viewWidth / 2) - 45;
		this.scrollBarX = this.resetX + 45;
		this.controlWidth = this.resetX - this.controlX - 5;
		
	}
	
	public boolean isChanged() {
		boolean changed = false;
		
		for(ConfigEntry entry : this.listEntries) {
			if (entry.enabled() && entry.isChanged()) {
				changed = true;
				break;
			}
		}
		
		return changed;
	}

	public boolean isDefault() {
		boolean isDefault = true;
		
		for(ConfigEntry entry : this.listEntries) {
			if (entry.enabled() && !entry.isDefault()) {
				isDefault = false;
				break;
			}
		}
		
		return isDefault;
	}

	public boolean isValidValues() {
		boolean isValid = true;
		
		for(ConfigEntry entry : this.listEntries) {
			if (entry.enabled() && !entry.isValidValue()) {
				isValid = false;
				break;
			}
		}
		
		return isValid;
	}
	
	public void setToDefault() {
		for(ConfigEntry entry : this.listEntries) {
			if (entry.enabled()) {
				entry.setToDefault();
			}
		}
	}

	public void undoChanges() {
		for(ConfigEntry entry : this.listEntries) {
			if (entry.enabled()) {
				entry.undoChanges();
			}
		}
	}
	
	public Object getValues() {
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		for(ConfigEntry entry : this.listEntries) {
			if (entry.enabled() && entry.isValidValue()) {
				values.put(entry.getName(), entry.getValue());
			} else {
				values.put(entry.getName(), entry.configElement.getValue());
			}
		}
		return values;
	}
	
	/**
	 * This method is a pass-through for IConfigEntry objects that contain
	 * GuiTextField elements. Called from the parent GuiConfig screen.
	 */
	public void updateScreen() {
		for (ConfigEntry entry : this.listEntries) {
			entry.updateCursorCounter();
		}
	}
	
	public boolean requiresMcRestart() {
		for (ConfigEntry entry : this.listEntries) {
			if (entry.isChanged() && entry.requiresMcRestart()) {
				return true;
			}
		}
		
		return false;
	}
	
	/////////////
	// Actions //
	/////////////
	
	/**
	 * This method is a pass-through for IConfigEntry objects that require keystrokes. Called from the parent GuiConfig screen.
	 */
	public void keyTyped(char eventChar, int eventKey) {
		for (ConfigEntry entry : this.listEntries) {
			entry.keyTyped(eventChar, eventKey);
		}
	}
	
	/**
	 * This method is a pass-through for IConfigEntry objects that contain GuiTextField elements. Called from the parent GuiConfig
	 * screen.
	 */
	public void mouseClicked(int mouseX, int mouseY, int mouseEvent) {
		for (ConfigEntry entry : this.listEntries){
			entry.mouseClicked(mouseX, mouseY, mouseEvent);
		}
	}
	
}
