package com.romraider.definition;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.jdom2.Element;

public class DefinitionMetaData {
	
	private HashMap<String,String> romMetaData; //TODO: Create a class for this with an interface!
	private boolean isBase;
	
	private String include;
	
	private String XmlId;//todo populate this
	

	public DefinitionMetaData(){
		romMetaData = new HashMap<String,String>();
	}
	
	public void Load(List<Element> elements, String include){
		this.ReadECUFlashRomID(elements,include);
	}
	
	protected void ReadECUFlashRomID(List<Element> elements, String incl) {
		include = incl;
		//TODO: error handling for: missing romid, already populated romid, adding to romid?
		for ( Element e : elements )
			romMetaData.put(e.getName().toLowerCase(),e.getValue());
		  
		if(!romMetaData.containsKey(Tags.XMLID)){
			//THROW ERROR
		}
		else if(romMetaData.get(Tags.XMLID).toLowerCase().contains(Tags.BASE)){
			isBase = true;
		} else {
			isBase = false;
		}
	}

	protected SimpleEntry<Long, String> getInternalIDMap() {
		return new SimpleEntry<>(this.getInternalIDAddress(), this.getInternalID());
	}

	protected Long getInternalIDAddress() {
		return Long.parseLong(romMetaData.get(Tags.INTERNAL_ID_ADDRESS),16);
	}

	protected String getInternalID() {
		String id = romMetaData.get(Tags.INTERNAL_ID_STRING);
		if(id!=null)
			return id;
		else
			return romMetaData.get(Tags.INTERNAL_ID);
	}
	
	public String getIncludeString(){
		return this.include;
	}
	
	public String getXmlId() {
		return XmlId;
	}

	public boolean isBase() {
		return isBase;
	}
}
