package com.romraider.definition;

import org.jdom2.Attribute;
import org.jdom2.Element;

import com.romraider.definition.DefinitionAttributeParser;
import com.romraider.maps.Scale;
import com.romraider.ECUExec;

public class TableDef {

	protected TableDef baseTableDef;
	protected Scale defaultScaling;
	protected Definition parentDefinition;
	protected TableType tableType;
	protected String category;
	protected Integer userLevel;
	protected Integer elements;
	protected String loggerParameter;
	protected String description;
	protected Long dataAddress;
	//TODO: Add support for lookup tables
	//TODO: Synthesize XML Node for export, also in child classes
	
	protected String name;
	
	//BASE TABLE CONSTRUCTOR
	public TableDef(Element node, Definition parent){
		this.parentDefinition = parent;
		ReadECUFlashTable(node);
	}
	
	private void ReadECUFlashTable(Element node) {
		for(Attribute a : node.getAttributes()){
			if(a.getName().equals(Tags.TABLE_TYPE))
				tableType = DefinitionAttributeParser.ParseTableType(a.getValue());
			else if(a.getName().equals(Tags.TABLE_DATA_ADDRESS))
				this.dataAddress = Long.parseLong(a.getValue().replace("0x",""),16);
			else if(a.getName().equals(Tags.TABLE_CATEGORY))
				this.category = a.getValue();
			else if(a.getName().equals(Tags.TABLE_USER_LEVEL))
				this.userLevel = Integer.parseInt(a.getValue());
			else if(a.getName().equals(Tags.TABLE_SCALING))
				this.defaultScaling = ECUExec.getDefinitionManager().getScaling(a.getValue());
			else if(a.getName().equals(Tags.TABLE_DESCRIPTION))
				this.description = a.getValue();
			else if(a.getName().equals(Tags.TABLE_ELEMENTS))
				elements = Integer.parseInt(a.getValue());
			else if(a.getName().equals(Tags.TABLE_LOGGER_PARAMETER))
				loggerParameter = a.getValue();
		}
		if(node.getChild(Tags.TABLE_DESCRIPTION) != null){
			this.description = node.getChild(Tags.TABLE_DESCRIPTION).getValue();			
		}
	}

	public boolean isBase() {
		return parentDefinition.isBase();
	}
	
	public TableDef getBase(){
		if(isBase())
			return this;
		else
			return this.baseTableDef;
	}
	
	public String getCategory(){
		if(category != null)
			return category;
		else
			return getBase().getCategory();
	}
	
	public TableType getTableType(){
		if(tableType != null)
			return tableType;
		else
			return getBase().getTableType();
	}
	
	public Integer getUserLevel(){
		if(userLevel != null)
			return userLevel;
		else
			return getBase().getUserLevel();
	}
	
	public Scale getDefaultScaling(){
		if(defaultScaling != null)
			return defaultScaling;
		else
			return getBase().getDefaultScaling();
	}
	
	public String getDescription(){
		if(description != null)
			return description;
		else
			return getBase().getDescription();
	}
	
	public Long getDataAddress(){
		if(dataAddress != null)
			return dataAddress;
		else
			return getBase().getDataAddress();
	}
	
	public String getName(){
		if(name != null && name != Tags.INHHERIT_X_AXIS && name != Tags.INHERIT_Y_AXIS)
			return name;
		else
			return getBase().getName();
	}
	
	public Definition getParentDefinition(){
		return this.parentDefinition;
	}
}
