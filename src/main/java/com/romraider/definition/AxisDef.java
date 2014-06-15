package com.romraider.definition;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

public class AxisDef extends TableDef{
	private ArrayList<String> staticData;
	private TableDef parentTableDef;
	
	public AxisDef(Element node, TableDef parent, TableType type){
		this(node, parent);
		this.tableType = type;
		this.parentTableDef = parent;
	}
	
	public AxisDef(Element node, TableDef parent){
		super(node, parent.getParentDefinition());
		this.parentTableDef = parent;
		try{
			List<Element> sd = node.getChildren(Tags.AXIS_DATA);
			for(Element child : sd){
				staticData.add(child.getValue());
			}

		}catch(Exception e){
			//TODO
		}		
	}
	
	public ArrayList<String> getStaticData(){
		if(isBase() || (staticData != null && !this.staticData.isEmpty()))
			return staticData;
		else{
			AxisDef base = (AxisDef) this.getBase();
			return base.getStaticData();
		}
	}
	
	public boolean isStatic() {
		if(getTableType() == TableType.TABLE_STATIC_X_AXIS || getTableType() == TableType.TABLE_STATIC_Y_AXIS)
			return true;
		else 
			return false;
	}

}
