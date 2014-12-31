package mods.gollum.core.client.gui.config;

import static mods.gollum.core.ModGollumCoreLib.log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import mods.gollum.core.client.gui.config.element.CategoryElement;
import mods.gollum.core.common.config.ConfigLoader;
import mods.gollum.core.common.config.ConfigLoader.ConfigLoad;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.client.config.GuiMessageDialog;

public class GuiCategoryConfig extends GuiConfig {
	
	private ConfigLoad configLoad;
	
	public GuiCategoryConfig(GuiScreen parent) {
		super(parent);
	}
	
	@Override
	protected void initConfigElement() {
		
		this.configLoad = ConfigLoader.configLoaded.get(this.mod);
		
		ArrayList<String> categories = configLoad.getCategories();
		for (String category : categories) {
			configElements.add(new CategoryElement (category, this.configLoad));
		}
	}
	
	@Override
	public void displayParent() {
		if (this.entryList.requiresMcRestart()) {
			mc.displayGuiScreen(new GuiMessageDialog(parent, "fml.configgui.gameRestartTitle", new ChatComponentText(I18n.format("fml.configgui.gameRestartRequired")), "fml.configgui.confirmRestartMessage"));
		} else {
			this.mc.displayGuiScreen(this.parent);
		}
	}
	
	@Override
	public void saveValue() {
		log.info("Save configuration "+this.getMod());
		
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
		for (Entry<String, LinkedHashMap<String, Object>> entry : ((LinkedHashMap<String, LinkedHashMap<String, Object>>)this.entryList.getValues()).entrySet()) {
			values.putAll(entry.getValue());
		}
		
		this.configLoad.saveValue(values);
		new ConfigLoader(configLoad.config, false).writeConfig();
	}
	
}
