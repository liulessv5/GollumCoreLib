package mods.gollum.core.client.gui.config.entry;

import mods.gollum.core.client.gui.config.GuiConfigEntries;
import mods.gollum.core.client.gui.config.element.ConfigElement;
import net.minecraft.client.Minecraft;

public class FloatEntry extends DoubleEntry {
	
	public FloatEntry(int index, Minecraft mc, GuiConfigEntries parent, ConfigElement configElement) {
		super(index, mc, parent, configElement);
	}
	
	@Override
	public Object getValue() {
		
		Float value = new Float(0.0F);
		try {
			value = Float.parseFloat(this.textFieldValue.getText());
		} catch (Exception e) {
		}
		
		return value;
	}

}
