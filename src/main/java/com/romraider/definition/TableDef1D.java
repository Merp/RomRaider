package com.romraider.definition;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;

public class TableDef1D extends TableDef {
	
	public TableDef1D(Element node, TableType type, Definition parent){
		this(node, parent);
		this.tableType = type;
	}
	
	public TableDef1D(Element node, Definition parent) {
		super(node, parent);
	}
	
}
