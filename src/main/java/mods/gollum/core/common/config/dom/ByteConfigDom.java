package mods.gollum.core.common.config.dom;

import argo.jdom.JsonNodeBuilder;
import argo.jdom.JsonNodeBuilders;

public class ByteConfigDom extends ConfigDom {
	
	public ByteConfigDom(byte b) {
		this.value = b;
	}
	
	public byte byteValue () { return (Byte)this.value; }
	
	/////////////////////
	// Convert to json //
	/////////////////////
	
	public JsonNodeBuilder json() {
		return JsonNodeBuilders.aNumberBuilder(value+"");
	}
}
