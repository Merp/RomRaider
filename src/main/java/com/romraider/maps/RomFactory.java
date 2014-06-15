package com.romraider.maps;

import static com.romraider.xml.DOMHelper.unmarshallAttribute;
import static com.romraider.xml.DOMHelper.unmarshallText;
import static org.w3c.dom.Node.ELEMENT_NODE;

import java.util.Map.Entry;

import javax.management.modelmbean.XMLParseException;
import javax.swing.JOptionPane;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.romraider.Settings;
import com.romraider.definition.Definition;
import com.romraider.definition.TableDef;
import com.romraider.definition.TableType;
import com.romraider.definition.Tags;
import com.romraider.editor.ecu.ECUEditorManager;
import com.romraider.swing.DebugPanel;
import com.romraider.util.ObjectCloner;
import com.romraider.util.SettingsManager;
import com.romraider.xml.InvalidTableNameException;
import com.romraider.xml.RomAttributeParser;
import com.romraider.xml.TableIsOmittedException;
import com.romraider.xml.TableNotFoundException;

public class RomFactory {
	
	public static Rom CreateFromDefinition(Definition definition) throws Exception{
		Rom output = new Rom(definition);
		
		for(Entry<String,TableDef> entry : definition.getTableDefs().entrySet()){
			TableDef tableDef = entry.getValue();
			Table table;
			if(tableDef.getTableType() == TableType.TABLE_1D){
				table = new Table1D(tableDef);
			} else if(tableDef.getTableType() == TableType.TABLE_2D){
				table = new Table2D(tableDef);
			} else if (tableDef.getTableType() == TableType.TABLE_3D){
				table = new Table3D(tableDef);
			} else if (tableDef.getTableType() == TableType.TABLE_BLOB){
				table = new TableSwitch(tableDef);
			} else{
				throw new Exception("Error missing table type");//TODO fix this handling up
			}
		}
		return output; 
	}
}
