package mods.gollum.core.client.gui.config.entry;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import mods.gollum.core.client.gui.config.GuiConfigEntries;
import mods.gollum.core.client.gui.config.GuiValueConfig;
import mods.gollum.core.client.gui.config.element.CategoryElement;
import mods.gollum.core.client.gui.config.element.ConfigElement;
import mods.gollum.core.common.config.Config;
import mods.gollum.core.common.config.ConfigProp;
import mods.gollum.core.common.config.ConfigLoader.ConfigLoad;
import net.minecraft.client.Minecraft;

public class SubConfigEntry extends ButtonEntry {
	
	private ArrayList<Config> value;
	
	public SubConfigEntry(int index, Minecraft mc, GuiConfigEntries parent, ConfigElement configElement) {
		super(index, mc, parent, configElement);
		
		this.labelDisplay = false;
		this.updateValueButtonText(this.getLabel());
		this.value = (ArrayList<Config>) this.configElement.getValue();
	}

	@Override
	public void valueButtonPressed(int slotIndex) {
//		this.mc.displayGuiScreen(new GuiValueConfig(this));
	}

	@Override
	public Object getValue() {
		return this.value;
	}

	@Override
	public ConfigEntry setValue(Object value) {
		this.value = (ArrayList<Config>) value;
		return this;
	}
	
	@Override
	public boolean equals (Object values) {
		return false;
	}
	
}
