package com.romraider.definition;

import org.jdom2.Attribute;
import org.jdom2.Element;


public class AxisDef {

	private boolean isBase;
	private TableDef parentTable;
	private String axisType;
	private Boolean isXAxis;
	private String name;
	private Integer elements;
	private String defaultScaling;
	private Long dataAddress;
	private String loggerParameter;
	
	//TODO: Synthesize XML node for export
	
	public AxisDef(Element node, TableDef parent){
		if(parent.isBase())
			isBase = true;
		else
			isBase = false;
		
		parentTable = parent;
		
		for(Attribute att : node.getAttributes()){
			switch (att.getName()){
			
			case Tags.TABLE_NAME:
				name = att.getValue();
				continue;
				
			case Tags.TABLE_DATA_ADDRESS:
				dataAddress = Long.parseLong(att.getValue().replace("0x", ""),16);
				continue;
				
			case Tags.TABLE_TYPE:
				if(att.getValue().toLowerCase().equals(Tags.AXIS_TYPE_X.toLowerCase()))
					isXAxis = true;
				else
					isXAxis = false;
				axisType = att.getValue();
				continue;
				
			case Tags.TABLE_SCALING:
				defaultScaling = att.getValue();
				continue;
				
			case Tags.TABLE_ELEMENTS:
				elements = Integer.parseInt(att.getValue());
				continue;
			
			case Tags.TABLE_LOGGER_PARAMETER:
				loggerParameter = att.getValue();
				continue;
			
			default:
				continue;
			}
		}
	}
	
	public AxisDef getBase(){
		switch(name.toLowerCase()){
		
		case Tags.INHHERIT_X_AXIS:
			return this.parentTable.getBaseXAxis();
		
		case Tags.INHERIT_Y_AXIS:
			return this.parentTable.getBaseYAxis();
			
		default:
			return this;
		}
	}
	
	public String getName(){
		if(isBase || name != null)
			return name;
		return getBase().getName();
	}
	
	public Long getDataAddress(){
		if(isBase || dataAddress != null)
			return dataAddress;
		return getBase().getDataAddress();
	}
	
	public String getAxisType(){
		if(isBase || axisType != null)
			return axisType;
		return getBase().getAxisType();
	}
	
	public Boolean getIsXAxis(){
		if(isBase || isXAxis != null)
			return isXAxis;
		return getBase().isXAxis;
	}
	
	public String getDefaultScaling(){
		if(isBase || this.defaultScaling != null)
			return defaultScaling;
		return getBase().getDefaultScaling();
	}
	
	public Integer getElements(){
		if(isBase || (this.elements != null && this.elements > 0))
			return elements;
		return getBase().getElements();
	}

	public String getLoggerParameter() {
		return loggerParameter;
	}

	/*		<table name="Idle Speed Error" type="X Axis" elements="9" scaling="RPM"/>
		<table name="Engine Speed Delta" type="Y Axis" elements="9" scaling="RPM"/>
		*/
	
}
