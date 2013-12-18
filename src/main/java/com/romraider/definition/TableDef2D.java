package com.romraider.definition;

import org.jdom2.Element;

public class TableDef2D extends TableDef{

	private AxisDef axis;
	
	public TableDef2D(Element node, Definition parent) {
		super(node, Tags.TABLE_TYPE_2D, parent);
		
		axis = new AxisDef(node.getChild("table"),this);
		
	}
	
	public AxisDef getXAxis(){
		return axis;
	}
		
	public AxisDef getYAxis(){
		return axis;
	}

}
