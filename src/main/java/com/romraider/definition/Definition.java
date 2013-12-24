package com.romraider.definition;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.romraider.ECUExec;
import com.romraider.maps.Scale;
import com.romraider.maps.Table;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;

public class Definition {
	//TODO: special BaseDefinition class inherits Definition????
	private Document document;
	private Element root;
	private SAXBuilder saxBuilder;
	
	private boolean isParsed;
	
	private DefinitionMetaData metaData;
	private File file;
	
	private String XmlId;
	
	private HashMap<String,TableDef> tables;
	
	public Definition(){
		tables = new HashMap<String,TableDef>();
	}
	
	public Definition(File f) {
		this();
		this.file = f;
		saxBuilder = new SAXBuilder();
			
		try {
			document = saxBuilder.build(file);
			} catch (JDOMException e) {
				  // TODO Auto-generated catch block
				  e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  
			root = document.getRootElement();
			
			ReadECUFlashMetaData();
		}
	
		public void ReadECUFlashMetaData(){
			DefinitionMetaData MD = new DefinitionMetaData();
			List<Element> romid = root.getChild(Tags.METADATA).getChildren();
			String include = null;
			if(root.getChild(Tags.INCLUDE) != null)
				include = root.getChild(Tags.INCLUDE).getValue();
			MD.Load(romid,include);
			this.metaData = MD;
		}
		  
		public void ReadECUFlashDefinition(){
			if(this.metaData == null)
				ReadECUFlashMetaData();
			for (Element node : root.getChildren(Tags.TABLE_SCALING)){
					ScalingDef sd = new ScalingDef(node,this);
					ECUExec.getDefinitionManager().addScaling(sd);
			}
			for (Element node : root.getChildren(Tags.TABLE)){		
					TableFactory(node);
			}
		}
		
		private void TableFactory(Element node){
			if(node.getAttributeValue(Tags.TABLE_TYPE) == null){
				//first try inheritance
				ECUExec.getDefinitionManager().getTableType(node.getAttributeValue(Tags.TABLE_NAME));
				//then try implicit
				if(node.getChildren(Tags.TABLE)!=null){
					switch(node.getChildren(Tags.TABLE).size()){
					case 2:
						CreateTable3D(node);
						break;
					case 1:
						CreateTable2D(node);
						break;
					default:
						CreateTable1D(node);
						break;
					}
				}
				else
					CreateTable1D(node);
				
			}
			else{
				switch(node.getAttributeValue(Tags.TABLE_TYPE)){
				case Tags.TABLE_TYPE_1D:
					CreateTable1D(node);
					break;
					
				case Tags.TABLE_TYPE_2D:
					CreateTable2D(node);
					break;
					
				case Tags.TABLE_TYPE_3D:
					CreateTable3D(node);
					break;
					
				case Tags.TABLE_TYPE_BLOB:
					CreateTableBlob(node);
					break;
					
				default:
					break;
				}
			}
		}
		private void CreateTable1D(Element node){
			String scalingname = node.getAttributeValue(Tags.TABLE_SCALING);
			DefinitionManager dm = ECUExec.getDefinitionManager();
			ScalingDef sd = dm.getScaling(scalingname);
			if(sd != null && !sd.isBlob())
				this.tables.put(node.getAttributeValue(Tags.TABLE_NAME), new TableDef1D(node,this));
			else
				this.tables.put(node.getAttributeValue(Tags.TABLE_NAME), new TableDefBlob(node,this));
		}
		
		private void CreateTable2D(Element node){
			this.tables.put(node.getAttributeValue(Tags.TABLE_NAME), new TableDef2D(node,this));
		}
		
		private void CreateTable3D(Element node){
			this.tables.put(node.getAttributeValue(Tags.TABLE_NAME), new TableDef3D(node,this));
		}
		
		private void CreateTableBlob(Element node){
			this.tables.put(node.getAttributeValue(Tags.TABLE_NAME), new TableDefBlob(node,this));
		}
		
		public DefinitionMetaData getMetaData() {
			return this.metaData;
		}

		public SimpleEntry<String,Long> getInternalIDMap() {
			return this.metaData.getInternalIDMap();
		}
		
		public Definition getInheritedDefinition() {
			String includeXmlId = this.metaData.getIncludeString();
			return ECUExec.getDefinitionManager().getDefinitionByXmlID(includeXmlId);
			//TODO: error and base handling
		}
		
		public List<String> getInheritanceList(){
			List<String> childlist = new ArrayList<String>();
			if(!metaData.isBase()){
				childlist.addAll(getInheritedDefinition().getInheritanceList());
			}
			childlist.add(this.metaData.getXmlId());
			return childlist;
		}
		
		public Definition getBase(){
			if(metaData.isBase())
				return this;
			else{
				return this.getInheritedDefinition().getBase();
			}
		}

		public String getXmlId() {
			return metaData.getXmlId();
		}

		public void parseXML() {
			this.ReadECUFlashDefinition();			
		}

		public boolean isBase() {
			return this.metaData.isBase();
		}

		public String getFile() {
			return this.file.getAbsolutePath();
		}

		public String getInternalID() {
			return this.metaData.getInternalID();
		}


	/*
		  public static void SearchDocument() {
			
			// situation 1
			// searching for the tag-Node
				for (Element node : root.getChildren()) {
					if (node.getName().equals("tag")) {
						LOGGER.trace(node.getName());
					}
				}
			// iterating for the tag-Node			
				for (Element node : root.getChildren("tag")) {
					LOGGER.trace(node.getName());
				}
			// searching for the other-Node with attribute type		
				for (Element node : root.getChildren("other")) {
					if (node.getAttribute("type") != null) {
						LOGGER.trace(node.getAttribute("type").getValue());
					}
				}			
			// iterating over all nodes			
				for (Content content : root.getDescendants()) {
					LOGGER.trace(content);
				}
				
				XPathFactory xpathFactory = XPathFactory.instance();
				String titelTextPath = "root/deep/tag/other/text()";
				XPathExpression<Object> expr = xpathFactory.compile(titelTextPath);
				List<Object> xPathSearchedNodes = expr.evaluate(document);
				for (int i = 0; i < xPathSearchedNodes.size(); i++) {
					Content content = (Content) xPathSearchedNodes.get(i);
					LOGGER.trace(content.getValue());
				}
		    }
		  }
		  }
		  */
	}
