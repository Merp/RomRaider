package com.romraider.definition;

import java.util.List;

import org.jdom2.Element;

public class TableDef2D extends TableDef{

	private AxisDef axisDef;
	
	public TableDef2D(Element node, TableType type, Definition parent){
		this(node, parent);
		tableType = type;
	}
	
	public TableDef2D(Element node, Definition parent) {
		super(node, parent);
		tableType = TableType.TABLE_2D;
		try{
			List<Element> childTables = node.getChildren(Tags.TABLE_AXIS);
			if(childTables.size() == 1){
				axisDef = new AxisDef(childTables.get(0),this);
			}
			else
				throw new Exception("todo");//TODO
		}catch(Exception e){
			//TODO
		}
	}

	public AxisDef getAxisDef() {
		return axisDef;
	}

}
