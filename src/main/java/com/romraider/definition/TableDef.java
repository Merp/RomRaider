package com.romraider.definition;

import org.jdom2.Attribute;
import org.jdom2.Element;

import com.romraider.ECUExec;

public class TableDef {

	private Boolean isBase;
	private TableDef inheritedTable;
	private ScalingDef defaultScaling;
	private Definition parentDefinition;
	private String tableType;
	private String category;
	private Integer userLevel;
	private String description;
	private Long dataAddress;
	//TODO: Add support for lookup tables
	//TODO: Synthesize XML Node for export, also in child classes
	
	public TableDef(Element node, String typ, Definition parent){
		this.parentDefinition = parent;
		this.tableType = typ;
		ReadECUFlashTable(node);
	}	
	
	private void ReadECUFlashTable(Element node) {
		if(parentDefinition.isBase() || node.getAttributeValue(Tags.TABLE_DATA_ADDRESS) == null)
			isBase = true;
		else
			isBase = false;
		
		for(Attribute a : node.getAttributes()){
			switch(a.getName()){
			
			case Tags.TABLE_TYPE:
				this.tableType = a.getValue();
				continue;
								
			case Tags.TABLE_DATA_ADDRESS:
				this.dataAddress = Long.parseLong(a.getValue().replace("0x",""),16);
				
			case Tags.TABLE_CATEGORY:
				this.category = a.getValue();
				continue;
				
			case Tags.TABLE_USER_LEVEL:
				this.userLevel = Integer.parseInt(a.getValue());
				continue;
				
			case Tags.TABLE_SCALING:
				this.defaultScaling = ECUExec.getDefinitionManager().getScaling(a.getValue());
				continue;
				
			case Tags.TABLE_DESCRIPTION:
				this.description = a.getValue();
				continue;
				
			default:
				continue;
			}
		}
		if(node.getChild(Tags.TABLE_DESCRIPTION) != null){
			this.description = node.getChild(Tags.TABLE_DESCRIPTION).getValue();			
		}
	}

	public AxisDef getBaseXAxis() {
		return null;
	}

	public AxisDef getBaseYAxis() {
		return null;
	}

	public boolean isBase() {
		return isBase;
	}
	
	public TableDef getBase(){
		if(isBase)
			return this;
		else
			return this.inheritedTable;
	}
	
	public String getCategory(){
		if(category != null)
			return category;
		else
			return getBase().getCategory();
	}
	
	public String getTableType(){
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
	
	public ScalingDef getDefaultScaling(){
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
}
