package com.romraider.definition;

import java.util.List;

import org.jdom2.Element;

public class TableDef3D extends TableDef {

	private AxisDef xAxisDef;
	private AxisDef yAxisDef;
	
	public TableDef3D(Element node, TableType type, Definition parent) {
		this(node, parent);
		tableType = type;
	}
	
	public TableDef3D(Element node, Definition parent){
		super(node, parent);
		try{
			List<Element> childTables = node.getChildren(Tags.TABLE_AXIS);
			if(!childTables.isEmpty()){
				for (Element ch : childTables){
					AxisDef tempAxisDef = new AxisDef(ch,this);
					TableType type = tempAxisDef.getTableType();
					if(type == TableType.TABLE_X_AXIS || type == TableType.TABLE_STATIC_X_AXIS)
						xAxisDef = tempAxisDef;
					else if(type == TableType.TABLE_Y_AXIS || type == TableType.TABLE_STATIC_Y_AXIS)
						yAxisDef = tempAxisDef;
					else
						throw new Exception("todo");//TODO
				}
				if(xAxisDef == null || yAxisDef == null)
					throw new Exception("todo");//TODO
			}
			else
				throw new Exception("todo");//TODO
		}catch(Exception e){
			//TODO:
		}
	}
	
	public AxisDef getXAxisDef(){
		return xAxisDef;
	}
		
	public AxisDef getYAxisDef(){
		return yAxisDef;
	}

}
