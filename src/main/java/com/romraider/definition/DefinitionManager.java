package com.romraider.definition;

import static org.apache.log4j.Logger.getLogger;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.romraider.maps.Scale;
import com.romraider.util.SettingsManager;
import com.romraider.util.VectorUtils;

public class DefinitionManager {

	private HashMap<Entry<String,Long>,Definition> definitionAddressMap;
	private HashMap<String,Definition> definitionMap;
	
	private TreeMap<String,TreeMap<String,Definition>> definitionTreeMap;
	private Comparator<String> comparator;
	
	private HashMap<String, Scale> scalingMap;
	private HashMap<String, Definition> baseDefinitions;
	
	

	private static final Logger LOGGER = getLogger(DefinitionManager.class);
	
	public DefinitionManager(){
		scalingMap = new HashMap<String,Scale>();
		definitionAddressMap = new HashMap<Entry<String,Long>,Definition>();
		definitionMap = new HashMap<String,Definition>();
		baseDefinitions = new HashMap<String,Definition>();
        comparator = new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        };
        definitionTreeMap = new TreeMap<String,TreeMap<String,Definition>>(comparator);
        
	}
        
	public void Initialize(){
		try{
		WalkECUFlashDefinitionDirectory();
        
        TestReadECUFlashMetaData();
        
        TestReadECUFlashBases();
        
        TestReadECUFlashDefinitions();
        
        TestShowECUFlashDefinitions();
        
		}catch(Exception e){
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
		int i =0;
	}
	
	private void TestShowECUFlashDefinitions(){
		DefinitionEditor de = new DefinitionEditor();
		de.setVisible(true);
	}
	
    private void WalkECUFlashDefinitionDirectory(){
    	for(File f : VectorUtils.FilterCI(VectorUtils.Walk(SettingsManager.getSettings().ECUFLASHDEFREPO),".xml"))
			SettingsManager.getSettings().addEcuFlashDefinitionFile(f);
    	//TODO: store this stuff in the manager, not settings!!
    	//TODO: implement serializeable
	}
	
	public void TestReadECUFlashMetaData(){
		for(File defFile : SettingsManager.getSettings().getEcuFlashDefinitionFiles()){
			Definition def = new Definition(defFile);
			
			if(def.isBase())
				baseDefinitions.put(def.getXmlId(),def);
			//todo error handling
			SimpleEntry<String,Long> dim = def.getInternalIDMap();//todo: base maps should return offset zero???? OR NOT INCLUDED IN definitionMAP
			definitionAddressMap.put(dim, def);
			definitionMap.put(def.getInternalID(),def);
			if(!getDefinitionTreeMap().keySet().contains(def.getMetaData().getFullModel()))
				getDefinitionTreeMap().put(def.getMetaData().getFullModel(),new TreeMap<String,Definition>(comparator));
			TreeMap<String,Definition> th = definitionTreeMap.get(def.getMetaData().getFullModel());
			th.put(def.getInternalID(), def);
		}
	}
	
	public List<Definition> getInheritingDefinitions(Definition parent){
		List<Definition> ret = new ArrayList<Definition>();
		for(Definition d: definitionMap.values()){
			if(d.getInheritedDefinition()!= null){
				if(d.getInheritedDefinition().getInternalID() == parent.getInternalID())
					ret.add(d);
			}
		}
		return ret;
	}
	
    public void TestReadECUFlashBases(){
    	for(Definition def : baseDefinitions.values()){
    		def.parseXML();
    	}
    }

	public void TestReadECUFlashDefinitions(){
		for(Definition def : definitionMap.values()){
			try{
				def.parseXML();
			}catch(Exception e){
				LOGGER.error("Error parsing definition: " + def.getFile() + " " + e.getMessage());
			}
		}
	}

	public Definition getDefinitionByXmlID(String includeXmlId) {
		Definition d = this.definitionMap.get(includeXmlId);
		return d;
	}

	public Scale getScaling(String attributeValue) {
		return scalingMap.get(attributeValue);
	}

	public void addScaling(Scale sc) {
		if(!scalingMap.containsKey(sc.getName()))
			scalingMap.put(sc.getName(), sc);
		//TODO:else
			//handle error!		
	}

	public HashMap<Entry<String,Long>,Definition> getDefinitionAddressMap() {
		return this.definitionAddressMap;
	}

	public HashMap<String,Definition> getDefinitionMap(){
		return this.definitionMap;
	}
	
	public TreeMap<String,TreeMap<String,Definition>> getDefinitionTreeMap() {
		return definitionTreeMap;
	}

}
