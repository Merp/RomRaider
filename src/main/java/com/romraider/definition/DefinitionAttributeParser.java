package com.romraider.definition;

public class DefinitionAttributeParser {
	
	public static TableType ParseTableType(String input){
		TableType tableType;
		switch(input){
		case Tags.TABLE_TYPE_1D:
			tableType = TableType.TABLE_1D;
			break;
		case Tags.TABLE_TYPE_2D:
			tableType = TableType.TABLE_2D;
			break;
		case Tags.TABLE_TYPE_3D:
			tableType = TableType.TABLE_3D;
			break;
		case Tags.TABLE_TYPE_BLOB:
			tableType = TableType.TABLE_BLOB;
			break;
		case Tags.AXIS_TYPE_X:
			tableType = TableType.TABLE_X_AXIS;
			break;
		case Tags.AXIS_TYPE_STATIC_X:
			tableType = TableType.TABLE_STATIC_X_AXIS;
			break;
		case Tags.AXIS_TYPE_Y:
			tableType = TableType.TABLE_Y_AXIS;
			break;
		case Tags.AXIS_TYPE_STATIC_Y:
			tableType = TableType.TABLE_STATIC_Y_AXIS;
			break;
		default:
			tableType = TableType.UNKNOWN;
			break;
		}
		return tableType;
	}
}
