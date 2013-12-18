package com.romraider.definition;

import static org.apache.log4j.Logger.getLogger;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.romraider.maps.Scale;
import com.romraider.util.SettingsManager;
import com.romraider.util.VectorUtils;

public class DefinitionManager {
	
	private DefinitionRepoManager definitionRepoManager;
	private HashMap<Entry<Long,String>,Definition> definitionMap;
	
	private HashMap<String, ScalingDef> scalingMap;
	private HashMap<String, Definition> baseDefinitions;
	

	private static final Logger LOGGER = getLogger(DefinitionRepoManager.class);
	
	public DefinitionManager(){
		scalingMap = new HashMap<String,ScalingDef>();
		definitionMap = new HashMap<Entry<Long,String>,Definition>();
		baseDefinitions = new HashMap<String,Definition>();
		definitionRepoManager = new DefinitionRepoManager();
        definitionRepoManager.Load();
        
	}
        
	public void Initialize(){
		try{
		WalkECUFlashDefinitionDirectory();
        
        TestReadECUFlashMetaData();
        
        TestReadECUFlashBases();
        
        TestReadECUFlashDefinitions();
		}catch(Exception e){
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
		int i =0;
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
			SimpleEntry<Long,String> dim = def.getInternalIDMap();//todo: base maps should return offset zero???? OR NOT INCLUDED IN definitionMAP
			definitionMap.put(dim, def);
		}
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
	public DefinitionRepoManager getRepoManager() {
		return definitionRepoManager;
	}

	public Definition getDefinitionByXmlID(String includeXmlId) {
		return this.definitionMap.get(includeXmlId);
	}

	public ScalingDef getScaling(String attributeValue) {
		return scalingMap.get(attributeValue);
	}

	public void addScaling(ScalingDef sd) {
		if(!scalingMap.containsKey(sd.getName()))
			scalingMap.put(sd.getName(), sd);
		//else
			//handle error!		
	}

	public void getTableType(String attributeValue) {
		// TODO Auto-generated method stub
		
	}
}
