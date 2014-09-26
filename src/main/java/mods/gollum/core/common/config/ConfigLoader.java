package mods.gollum.core.common.config;import java.io.BufferedReader;import java.io.BufferedWriter;import java.io.File;import java.io.FileReader;import java.io.FileWriter;import java.io.IOException;import java.lang.reflect.Array;import java.lang.reflect.Field;import java.util.ArrayList;import java.util.HashMap;import java.util.HashSet;import java.util.LinkedList;import mods.gollum.core.ModGollumCoreLib;import mods.gollum.core.common.config.type.IConfigJsonClass;import mods.gollum.core.common.config.type.IConfigJsonType;import mods.gollum.core.common.context.ModContext;import mods.gollum.core.common.log.Logger;import mods.gollum.core.common.mod.GollumMod;import mods.gollum.core.tools.simplejson.Json;import argo.format.JsonFormatter;import argo.format.PrettyJsonFormatter;import argo.jdom.JdomParser;import argo.jdom.JsonNode;import argo.jdom.JsonNodeFactories;import argo.jdom.JsonRootNode;import cpw.mods.fml.relauncher.FMLInjectionData;public class ConfigLoader {	private static final String extention = ".cfg";	private static final String configDir = "config";	private static final JdomParser parser = new JdomParser();	private static final JsonFormatter formatter = new PrettyJsonFormatter(); 		public static final HashMap<GollumMod, ConfigLoad> configLoaded = new HashMap<GollumMod, ConfigLoad>();		private boolean updateFile = false;	private File dir;	private String fileName;	private Config config;	private LinkedList<Field> configFields;	private HashSet<String> groupList;		public class ConfigLoad {				public GollumMod mod;		public Config configDefault;		public Config config;				public ConfigLoad (GollumMod mod, Config config) {			this.mod = mod;			this.configDefault = (Config)config.clone();			this.config = config;		}				public ArrayList<String> getCategories() {						ArrayList<String> categories = new ArrayList<String>();			categories.add("General");						try {				for (Field f : this.config.getClass().getDeclaredFields()) {					f.setAccessible(true);					ConfigProp anno = f.getAnnotation(ConfigProp.class);					if (anno != null && !anno.group().equals("") && !categories.contains(anno.group())) {						categories.add(anno.group());					}				}			} catch (Throwable e)  {				e.printStackTrace();			}						return categories;		}	}		/**	 * Constructeur	 */	public ConfigLoader (Config config) {		this(config, true);	}		/**	 * Constructeur	 */	public ConfigLoader (Config config, boolean register) {				if (register) {			GollumMod mod = ModContext.instance().getCurrent();			if (config.isMain() && !this.configLoaded.containsKey (mod)) {				this.configLoaded.put(mod, new ConfigLoad(mod, config));			}		}				this.dir = new File (((File)(FMLInjectionData.data()[6])).getAbsoluteFile(), this.configDir);		this.dir = new File (this.dir, config.getRelativePath());				if (!this.dir.exists()) {			this.dir.mkdir();		}				this.config       = config;		this.configFields = new LinkedList<Field>();		this.groupList    = new HashSet<String>();		this.fileName     = config.getFileName ()+this.extention;				// Parse la class pour chercher les propriété à save en config		Field[] fields = this.config.getClass().getDeclaredFields();		for (Field field : fields) {			if (field.isAnnotationPresent(ConfigProp.class)) {								this.configFields.add(field);				ConfigProp prop = (ConfigProp) field.getAnnotation(ConfigProp.class);				this.groupList.add(prop.group());								Logger.log(ModGollumCoreLib.MODID, Logger.LEVEL_DEBUG, "Field : "+field.getName()+" found goup="+prop.group());							}		}	}		/**	 * Renvoie les props de chaque group	 * @param groupName	 * @return	 */	public LinkedList<Field> getFieldByGroup (String groupName) {				LinkedList<Field> list = new LinkedList<Field>();				for (Field field : this.configFields) {			ConfigProp prop = (ConfigProp) field.getAnnotation(ConfigProp.class);			if (prop.group().equals(groupName)) {				list.add(field);			}		}		return list;	}			/**	 * Charge la config	 */	public void loadConfig() {				Logger.log(ModGollumCoreLib.MODID, Logger.LEVEL_INFO, "Read config : "+this.config.getRelativePath()+"/"+this.fileName);				try {						File configFile = new File(this.dir, this.fileName);			HashMap<String, Field> types = new HashMap<String, Field>();						for (Field field : this.configFields) {				ConfigProp prop = (ConfigProp) field.getAnnotation(ConfigProp.class);				types.put( ((!prop.name().isEmpty()) ? prop.name() : field.getName()), field);			}						HashMap<String, Field> properties;						if (configFile.exists()) {								properties = parseConfig(configFile, types);								for (String prop : types.keySet()) {										try {						if (properties.containsKey(prop)) {														Field field = (Field) types.get(prop);							Object obj = properties.get(prop);														field.setAccessible(true);														if (!obj.equals(field.get(this.config))) {																Logger.log(ModGollumCoreLib.MODID, Logger.LEVEL_DEBUG, "Set field : "+field.getName()+", obj="+obj+", objType="+obj.getClass().getName());																field.set(this.config, this.mergeValue(field.get(this.config), obj));							}						} else {							Logger.log(ModGollumCoreLib.MODID, Logger.LEVEL_WARNING, this.fileName+"Propery : "+prop+"Not found");							this.updateFile = true;						}					} catch (Exception e) {						e.printStackTrace();						this.updateFile = true;					}				}							} else {				this.updateFile = true;			}					} catch (Exception e) {			this.updateFile = true;			e.printStackTrace();			Logger.log(ModGollumCoreLib.MODID, Logger.LEVEL_SEVERE, e.getMessage());		}		if (this.updateFile) {			writeConfig();		}		this.updateFile = false;	}		private Object mergeValue (Object oldValue, Object newValue) {		if (oldValue instanceof IConfigMerge) {			if(!((IConfigMerge)oldValue).merge (newValue)) {				this.updateFile = true;			}			return oldValue;		}		return newValue;	}		private Object parseConvert (Class classType, String prop) {				String jsonStr = "{\"root\":"+prop+"}";		try {			JsonRootNode root = this.parser.parse(jsonStr);			return this.parseConvert(classType, root.getNode("root"));		} catch (Exception e) {			Logger.log(ModGollumCoreLib.MODID, Logger.LEVEL_SEVERE, "Erreur read config : file="+this.dir+"/"+this.fileName+", prop="+prop+", json parsed="+jsonStr);			e.printStackTrace();			if (Logger.getLevel() <= Logger.LEVEL_DEBUG) {							}		}		return null;	}		private Object parseConvert (Class classType, JsonNode json) throws Exception {				Object value = null;				if (classType.isAssignableFrom(String.class)) {			value = json.getText();		} else if (			classType.isAssignableFrom(Long.TYPE) ||			classType.isAssignableFrom(Long.class)		) { 			value = Long.parseLong(json.getNumberValue());		} else if (			classType.isAssignableFrom(Integer.TYPE) ||			classType.isAssignableFrom(Integer.class)		) { 			value = Integer.parseInt(json.getNumberValue());		} else if (			classType.isAssignableFrom(Short.TYPE) ||			classType.isAssignableFrom(Short.class)			) { 			value = Short.parseShort(json.getNumberValue());		} else if (			classType.isAssignableFrom(Byte.TYPE) ||			classType.isAssignableFrom(Byte.class)			) { 			value = Byte.parseByte(json.getNumberValue());		} else if (			classType.isAssignableFrom(Character.TYPE) ||			classType.isAssignableFrom(Character.class)			) { 			value = (char)((Byte.parseByte(json.getNumberValue())) & 0x00FF);		} else if (			classType.isAssignableFrom(Double.TYPE) ||			classType.isAssignableFrom(Double.class)			) { 			value = Double.parseDouble(json.getNumberValue());		} else if (			classType.isAssignableFrom(Float.TYPE) ||			classType.isAssignableFrom(Float.class)			) { 			value = Float.parseFloat(json.getNumberValue());		} else  if (			classType.isAssignableFrom(Boolean.TYPE) ||			classType.isAssignableFrom(Boolean.class)			) { 			value = json.getBooleanValue();		} else		if (IConfigJsonClass.class.isAssignableFrom(classType)) { 			value = classType.newInstance();			((IConfigJsonClass)value).readConfig(json);					} else if (IConfigJsonType.class.isAssignableFrom(classType)) { 				value = classType.newInstance();				((IConfigJsonType)value).readConfig(Json.create(json));							} else if (classType.isArray()) {						ArrayList<Object> tmp = new ArrayList<Object>();			Class subClass = classType.getComponentType();						for (JsonNode el : json.getElements()) {				Object subValue = this.parseConvert (subClass, el);				if (subValue != null) {					tmp.add(subValue);				}			}						value = Array.newInstance(subClass, tmp.size());			for (int i = 0; i < tmp.size(); i++) {				Array.set(value, i, tmp.get(i));			}		}				return value;	}		/**	 * Lit le fichier de config	 * @param file	 * @param types	 * @return	 * @throws Exception	 */	private HashMap parseConfig(File file, HashMap types) throws Exception {				HashMap config = new HashMap();		BufferedReader reader = new BufferedReader(new FileReader(file));		String strLine;				while ((strLine = reader.readLine()) != null) {						if ((!strLine.startsWith("#")) && (strLine.length() != 0)) {								int index = strLine.indexOf("=");								if ((index <= 0) || (index == strLine.length())) {										this.updateFile = true;									} else {										String name = strLine.substring(0, index).trim();					String prop = this.readValue(strLine.substring(index + 1), reader);										Logger.log(ModGollumCoreLib.MODID, Logger.LEVEL_DEBUG, "Read prop "+" : "+name+":"+prop);										if (!types.containsKey(name)) {						this.updateFile = true;											} else {						Class classType = ((Field) types.get(name)).getType();						Object value = this.parseConvert(classType, prop);												if (value != null) {							Logger.log(ModGollumCoreLib.MODID, Logger.LEVEL_DEBUG, "Read "+this.fileName+" : "+name+":"+value);							config.put(name, value);						}					}				}			}		}				reader.close();		return config;	}		private String readValue(String line, BufferedReader reader) throws Exception {		return this.readValue(line, 0, reader); 	}	private String readValue(String line, int i, BufferedReader reader) throws Exception {		if (line.length() <= i) {			i = 0;			if ((line = reader.readLine()) != null) {				return "";			}			return readValue(line, i, reader);		}				char c = line.charAt(i);				if (Character.isWhitespace (c)) {			return readValue(line, i + 1, reader);		}		if (c == '{') {			return c+readAccoladeValue(line, i+1, 1, reader);		}		if (c == '[') {			return c+readCrochetValue(line, i+1, 1, reader);		}				return c + ((i+1 != line.length()) ? line.substring(i+1) : "");	}	private String readAccoladeValue(String line, int i, int niveau, BufferedReader reader) throws Exception {		if (line.length() <= i) {			i = 0;			if ((line = reader.readLine()) == null) {				return "";			}			return readAccoladeValue(line, i, niveau, reader);		}		char c = line.charAt(i);				if (c == '{') {			return c+readAccoladeValue(line, i+1, niveau+1, reader);		}		if (c == '}') {			if (niveau == 1) {				return c + ((i+1 != line.length()) ? line.substring(i+1) : "");			}						return c+readAccoladeValue(line, i+1, niveau-1, reader);		}				return c+readAccoladeValue(line, i+1, niveau, reader);	}	private String readCrochetValue(String line, int i, int niveau, BufferedReader reader) throws Exception {		if (line.length() <= i) {			i = 0;			if ((line = reader.readLine()) == null) {				return "";			}			return readCrochetValue(line, i, niveau, reader);		}		char c = line.charAt(i);				if (c == '[') {			return c+readCrochetValue(line, i+1, niveau+1, reader);		}		if (c == ']') {			if (niveau == 1) {				return c + ((i+1 != line.length()) ? line.substring(i+1) : "");			}						return c+readCrochetValue(line, i+1, niveau-1, reader);		}				return c+readCrochetValue(line, i+1, niveau, reader);	}		private String toJsonValue (Field field) throws Exception {				Object value = field.get(this.config);				JsonRootNode json = JsonNodeFactories.object(			JsonNodeFactories.field("root", this.toJsonValue (value))		);				String out = this.formatter.format(json);		out = out.substring(1).trim().substring("\"root\":".length()).trim();		out = out.substring(0, out.length() - ("}".length()));				return out;	}		private JsonNode toJsonValue(Object value) {				JsonNode node = null;		if (value != null ) {						if (value instanceof String)    { node = JsonNodeFactories.string ((String)value);              } else			if (value instanceof Long)      { node = JsonNodeFactories.number ((Long)value);                } else			if (value instanceof Integer)   { node = JsonNodeFactories.number ((Integer)value);             } else			if (value instanceof Short)     { node = JsonNodeFactories.number ((Short)value);               } else			if (value instanceof Byte)      { node = JsonNodeFactories.number ((Byte)value);                } else			if (value instanceof Character) { node = JsonNodeFactories.number ((Character)value);           } else			if (value instanceof Double)    { node = JsonNodeFactories.number (((Double)value).toString()); } else			if (value instanceof Float)     { node = JsonNodeFactories.number (((Float)value).toString());  } else			if (value instanceof Boolean)   { node = JsonNodeFactories.booleanNode((Boolean)value);         } else							if (value instanceof IConfigJsonType) { 								node = ((IConfigJsonType)value).writeConfig().argoJson ().build();							} else 			if (value instanceof Json) { 								node = ((Json)value).argoJson().build();							} else 			if (value instanceof IConfigJsonClass) { 								node = ((IConfigJsonClass)value).writeConfig();							} else			if (value.getClass().isArray()) {								ArrayList<JsonNode> childs = new ArrayList<JsonNode>();				for (int i = 0; i< Array.getLength(value); i++) {					childs.add(this.toJsonValue (Array.get(value, i)));				}				node = JsonNodeFactories.lazyArray(childs);			}		}				return node;	}		/**	 * Met à jour la config	 */	public ConfigLoader writeConfig() {				File file = new File(this.dir, this.fileName);				try {			if (!file.exists()) {				file.createNewFile();			}						BufferedWriter out = new BufferedWriter(new FileWriter(file));						for (String groupName : this.groupList) {								String title = groupName;				if (groupName.equals("")) {					title = "General";				}				for (int i = 0; i < title.length() + 4 ; i++) out.write("#");				out.write(System.getProperty("line.separator") + "# " + title + " #" + System.getProperty("line.separator"));				for (int i = 0; i < title.length() + 4 ; i++) out.write("#");				out.write(System.getProperty("line.separator")+System.getProperty("line.separator"));								for (Field field : this.getFieldByGroup(groupName)) {										ConfigProp prop = (ConfigProp) field.getAnnotation(ConfigProp.class);					if (prop.info().length() != 0) {						out.write("#" + prop.info() + System.getProperty("line.separator"));					}										String name = !prop.name().isEmpty() ? prop.name() : field.getName();										try {												String value = this.toJsonValue(field);						out.write(name + "=" + value + System.getProperty("line.separator"));											} catch (Exception e) {						e.printStackTrace();					}				}				out.write(System.getProperty("line.separator"));				out.write(System.getProperty("line.separator"));			}			out.close();					} catch (IOException e) {			e.printStackTrace();		}				return this;	}}