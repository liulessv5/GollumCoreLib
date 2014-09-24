package mods.gollum.core.tools.simplejson;

import mods.gollum.core.tools.simplejson.Json.TYPE;
import argo.jdom.JsonNodeBuilder;
import argo.jdom.JsonNodeBuilders;

public class JsonChar extends Json {
	
	public JsonChar(char c) {
		this.value = c;
	}
	
	public char charValue () { return (Character)this.value; }
	
	public TYPE getType () {
		return TYPE.CHAR;
	}
	
	public void setValue(Object value) {
		try {
			this.value = (char) (Byte.parseByte(value.toString()) & 0x00FF);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/////////////////////
	// Convert to json //
	/////////////////////
	
	public JsonNodeBuilder json() {
		return JsonNodeBuilders.aNumberBuilder(((byte)((Character)value).charValue())+"");
	}
}