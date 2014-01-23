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
			if(tableDef.getTableType() == TableType.TABLE1D){
				table = new Table1D(tableDef);
			} else if(tableDef.getTableType() == TableType.TABLE2D){
				table = new Table2D(tableDef);
			} else if (tableDef.getTableType() == TableType.TABLE3D){
				table = new Table3D(tableDef);
			} else if (tableDef.getTableType() == TableType.TABLEBLOB){
				table = new TableSwitch(tableDef);
			} else{
				throw new Exception("Error missing table type");//TODO fix this handling up
			}
		}
		return output; 
	}

	  private Table unmarshallTable(Node tableNode, Table table, Rom rom)
	            throws XMLParseException, TableIsOmittedException, Exception {

		  //TODO: go through all of these and make appropritate changes in table class to reference TableDef class instead
		  
	        // unmarshall table attributes
	        table.setName(unmarshallAttribute(tableNode, "name", table.getName()));
	        table.setType(RomAttributeParser.parseTableType(unmarshallAttribute(
	                tableNode, "type", String.valueOf(table.getType()))));
	        if (unmarshallAttribute(tableNode, "beforeram", "false")
	                .equalsIgnoreCase("true")) {
	            table.setBeforeRam(true);
	        }

	        table.setCategory(unmarshallAttribute(tableNode, "category",
	                table.getCategory()));
	        if (table.getStorageType() < 1) {
	            table.setSignedData(RomAttributeParser
	                    .parseStorageDataSign(unmarshallAttribute(tableNode,
	                            "storagetype",
	                            String.valueOf(table.getStorageType()))));
	        }
	        table.setStorageType(RomAttributeParser
	                .parseStorageType(unmarshallAttribute(tableNode, "storagetype",
	                        String.valueOf(table.getStorageType()))));
	        table.setEndian(RomAttributeParser.parseEndian(unmarshallAttribute(
	                tableNode, "endian", String.valueOf(table.getEndian()))));
	        table.setStorageAddress(RomAttributeParser
	                .parseHexString(unmarshallAttribute(tableNode,
	                        "storageaddress",
	                        String.valueOf(table.getStorageAddress()))));
	        table.setDescription(unmarshallAttribute(tableNode, "description",
	                table.getDescription()));
	        table.setDataSize(unmarshallAttribute(tableNode, "sizey",
	                unmarshallAttribute(tableNode, "sizex", table.getDataSize())));
	        table.setFlip(unmarshallAttribute(tableNode, "flipy",
	                unmarshallAttribute(tableNode, "flipx", table.getFlip())));
	        table.setUserLevel(unmarshallAttribute(tableNode, "userlevel",
	                table.getUserLevel()));
	        table.setLocked(unmarshallAttribute(tableNode, "locked",
	                table.isLocked()));
	        table.setLogParam(unmarshallAttribute(tableNode, "logparam",
	                table.getLogParam()));

	        if (table.getType() == Settings.TABLE_3D) {
	            ((Table3D) table).setSwapXY(unmarshallAttribute(tableNode,
	                    "swapxy", ((Table3D) table).getSwapXY()));
	            ((Table3D) table).setFlipX(unmarshallAttribute(tableNode, "flipx",
	                    ((Table3D) table).getFlipX()));
	            ((Table3D) table).setFlipY(unmarshallAttribute(tableNode, "flipy",
	                    ((Table3D) table).getFlipY()));
	            ((Table3D) table).setSizeX(unmarshallAttribute(tableNode, "sizex",
	                    ((Table3D) table).getSizeX()));
	            ((Table3D) table).setSizeY(unmarshallAttribute(tableNode, "sizey",
	                    ((Table3D) table).getSizeY()));
	        }

	        Node n;
	        NodeList nodes = tableNode.getChildNodes();

	        for (int i = 0; i < nodes.getLength(); i++) {
	            n = nodes.item(i);

	            if (n.getNodeType() == ELEMENT_NODE) {
	                if (n.getNodeName().equalsIgnoreCase("table")) {

	                    if (table.getType() == Settings.TABLE_2D) { // if table is 2D,
	                        // parse axis

	                        if (RomAttributeParser
	                                .parseTableType(unmarshallAttribute(n, "type",
	                                        "unknown")) == Settings.TABLE_Y_AXIS
	                                        || RomAttributeParser
	                                        .parseTableType(unmarshallAttribute(n,
	                                                "type", "unknown")) == Settings.TABLE_X_AXIS) {

	                            Table1D tempTable = (Table1D) unmarshallTable(n,
	                                    ((Table2D) table).getAxis(), rom);
	                            if (tempTable.getDataSize() != table.getDataSize()) {
	                                tempTable.setDataSize(table.getDataSize());
	                            }
	                            tempTable.setData(((Table2D) table).getAxis()
	                                    .getData());
	                            ((Table2D) table).setAxis(tempTable);

	                        }
	                    } else if (table.getType() == Settings.TABLE_3D) { // if table
	                        // is 3D,
	                        // populate
	                        // xAxis
	                        if (RomAttributeParser
	                                .parseTableType(unmarshallAttribute(n, "type",
	                                        "unknown")) == Settings.TABLE_X_AXIS) {

	                            Table1D tempTable = (Table1D) unmarshallTable(n,
	                                    ((Table3D) table).getXAxis(), rom);
	                            if (tempTable.getDataSize() != ((Table3D) table)
	                                    .getSizeX()) {
	                                tempTable.setDataSize(((Table3D) table)
	                                        .getSizeX());
	                            }
	                            tempTable.setData(((Table3D) table).getXAxis()
	                                    .getData());
	                            ((Table3D) table).setXAxis(tempTable);

	                        } else if (RomAttributeParser
	                                .parseTableType(unmarshallAttribute(n, "type",
	                                        "unknown")) == Settings.TABLE_Y_AXIS) {

	                            Table1D tempTable = (Table1D) unmarshallTable(n,
	                                    ((Table3D) table).getYAxis(), rom);
	                            if (tempTable.getDataSize() != ((Table3D) table)
	                                    .getSizeY()) {
	                                tempTable.setDataSize(((Table3D) table)
	                                        .getSizeY());
	                            }
	                            tempTable.setData(((Table3D) table).getYAxis()
	                                    .getData());
	                            ((Table3D) table).setYAxis(tempTable);

	                        }
	                    }

	                } else if (n.getNodeName().equalsIgnoreCase("scaling")) {
	                    // check whether scale already exists. if so, modify, else
	                    // use new instance
	                    Scale baseScale = table.getScale(unmarshallAttribute(n,"name", "Default"));
	                    table.addScale(unmarshallScale(n, baseScale));

	                } else if (n.getNodeName().equalsIgnoreCase("data")) {
	                    // parse and add data to table
	                    DataCell dataCell = new DataCell(table, unmarshallText(n));
	                    if(table instanceof Table1D) {
	                        ((Table1D)table).addStaticDataCell(dataCell);
	                    } else {
	                        // Why would this happen.  Static should only be for axis.
	                        LOGGER.error("Error adding static data cell.");
	                    }

	                } else if (n.getNodeName().equalsIgnoreCase("description")) {
	                    table.setDescription(unmarshallText(n));

	                } else if (n.getNodeName().equalsIgnoreCase("state")) {
	                    ((TableSwitch) table).setValues(
	                            unmarshallAttribute(n, "name", ""),
	                            unmarshallAttribute(n, "data", "0.0"));

	                } else { /* unexpected element in Table (skip) */
	                }
	            } else { /* unexpected node-type in Table (skip) */
	            }
	        }

	        return table;
	    }
	  private Scale unmarshallScale(Node scaleNode, Scale scale) {

	        // look for base scale first
	        String base = unmarshallAttribute(scaleNode, "base", "none");
	        if (!base.equalsIgnoreCase("none")) {
	            for (Scale scaleItem : scales) {

	                // check whether name matches base and set scale if so
	                if (scaleItem.getName().equalsIgnoreCase(base)) {
	                    try {
	                        scale = (Scale) ObjectCloner.deepCopy(scaleItem);

	                    } catch (Exception ex) {
	                        JOptionPane.showMessageDialog(
	                                ECUEditorManager.getECUEditor(),
	                                new DebugPanel(ex, SettingsManager.getSettings()
	                                        .getSupportURL()), "Exception",
	                                        JOptionPane.ERROR_MESSAGE);
	                    }
	                }
	            }
	        }

	        // set remaining attributes
	        scale.setName(unmarshallAttribute(scaleNode, "name", "Default"));
	        scale.setUnit(unmarshallAttribute(scaleNode, "units", scale.getUnit()));
	        scale.setExpression(unmarshallAttribute(scaleNode, "expression",
	                scale.getExpression()));
	        scale.setByteExpression(unmarshallAttribute(scaleNode, "to_byte",
	                scale.getByteExpression()));
	        scale.setDecimalFormat(unmarshallAttribute(scaleNode, "format", "#"));
	        scale.setMax(unmarshallAttribute(scaleNode, "max", 0.0));
	        scale.setMin(unmarshallAttribute(scaleNode, "min", 0.0));

	        // get coarse increment with new attribute name (coarseincrement), else
	        // look for old (increment)
	        scale.setCoarseIncrement(unmarshallAttribute(
	                scaleNode,
	                "coarseincrement",
	                unmarshallAttribute(scaleNode, "increment",
	                        scale.getCoarseIncrement())));

	        scale.setFineIncrement(unmarshallAttribute(scaleNode, "fineincrement",
	                scale.getFineIncrement()));

	        return scale;
	    }

}
