package com.romraider.definition;

import org.jdom2.Element;

public class TableDefBlob extends TableDef {
	
	public TableDefBlob(Element node, Definition parent) {
		super(node, Tags.TABLE_TYPE_BLOB, parent);
	}
	
}
