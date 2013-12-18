package com.romraider.definition;

import org.jdom2.Element;

public class TableDef3D extends TableDef {

	private AxisDef xAxisDef;
	private AxisDef yAxisDef;
	
	public TableDef3D(Element node, Definition parent) {
		super(node,Tags.TABLE_TYPE_3D, parent);
		
		for(Element ch : node.getChildren()){
			if(ch.getName() == Tags.TABLE_AXIS){
				if(ch.getAttribute(Tags.TABLE_TYPE).getValue().toLowerCase() == Tags.AXIS_TYPE_X)//TODO: left off here
					xAxisDef = new AxisDef(ch,this);
				else if(ch.getAttribute(Tags.TABLE_TYPE).getValue().toLowerCase() == Tags.AXIS_TYPE_Y)
					xAxisDef = new AxisDef(ch,this);
			}
		}
	}
	
	public AxisDef getXAxis(){
		return xAxisDef;
	}
		
	public AxisDef getYAxis(){
		return yAxisDef;
	}

}
