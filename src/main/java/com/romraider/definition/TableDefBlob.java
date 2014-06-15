package com.romraider.definition;

import org.jdom2.Element;

public class TableDefBlob extends TableDef {
	
	public TableDefBlob(Element node, TableType type, Definition parent) {
		this(node, parent);
		tableType = type;
	}
	
	public TableDefBlob(Element node, Definition parent){
		super(node, parent);
	}
	
}
