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
	private DefinitionManager manager = ECUExec.getDefinitionManager();
	private Document document;
	private Element root;
	private SAXBuilder saxBuilder;	
	private DefinitionMetaData metaData;
	private File file;
	private HashMap<String,TableDef> tables;
	private boolean populated = false;

	//TODO: special BaseDefinition class inherits Definition????
	//TODO: Synthesize XML Node for export
	
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
					ECUExec.getDefinitionManager().addScaling(ScalingFactory.CreateScaling(node,this));
			}
			for (Element node : root.getChildren(Tags.TABLE)){		
					TableDefFactory(node);
			}
			populated = true;
		}
		
		///Determine if the table is a base, and what type.
		private void TableDefFactory(Element node){
			try{
				TableType type;
				String name = node.getAttributeValue(Tags.TABLE_NAME);
				if(name.equals(null))
					throw new Exception("Error, table name missing in definition " + this.getFile());
				
				if(node.getAttributeValue(Tags.TABLE_TYPE).equals(null)){
					if(isBase())
						throw new Exception("Error, table type missing in table: " + name + ". In definition: "+ this.getFile());
					else
					{
						TableDef base = this.getBase().getTableDefs().get(name);
						
						if(!base.equals(null))
							throw new Exception("Error, base table not found for: " + name + ". In definition: "+ this.getFile());
						else
							type = base.getTableType();
					}
				}
				else
					 type = DefinitionAttributeParser.ParseTableType(node.getAttributeValue(Tags.TABLE_TYPE));

	
				if(type.equals(TableType.TABLE_1D))//also handles blob tables
					CreateTableDef1D(name, type, node);
				else if(type.equals(TableType.TABLE_2D))
					CreateTableDef2D(name, type, node);
				else if(type.equals(TableType.TABLE_3D))
					CreateTableDef3D(name, type, node);
				else if(type.equals(TableType.TABLE_BLOB))
					CreateTableDefBlob(name, type, node);
				else
					throw new Exception("Error, invalid table type in table: " + name + ". In definition: "+ this.getFile());
				
			}catch(Exception e){
				//TODO
			}
		}
		
		private void CreateTableDef1D(String name, TableType type, Element node){
			String scalingname = node.getAttributeValue(Tags.TABLE_SCALING);
			DefinitionManager dm = ECUExec.getDefinitionManager();
			Scale sd = dm.getScaling(scalingname);
			if(sd != null && !sd.isBlob())
				this.tables.put(name, new TableDef1D(node, type, this));
			else
				this.tables.put(name, new TableDefBlob(node, type, this));
		}
		
		private void CreateTableDef2D(String name, TableType type, Element node){
			this.tables.put(name, new TableDef2D(node, type, this));
		}
		
		private void CreateTableDef3D(String name, TableType type, Element node){
			this.tables.put(name, new TableDef3D(node,type, this));
		}
		
		private void CreateTableDefBlob(String name, TableType type, Element node){
			this.tables.put(name, new TableDefBlob(node, type, this));
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
		
		public List<Definition> getInheritanceList(){
			List<Definition> childlist = new ArrayList<Definition>();
			if(!metaData.isBase()){
				childlist.addAll(getInheritedDefinition().getInheritanceList());
			}
			childlist.add(this);
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

		public HashMap<String,TableDef> getTableDefs() {
			return this.tables;
		}
		
		public Boolean isPopulated() {
			return this.populated;
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
