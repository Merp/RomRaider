package com.romraider.definition;

import static com.romraider.xml.DOMHelper.unmarshallText;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.jdom2.Element;

import com.romraider.xml.RomAttributeParser;

public class DefinitionMetaData {
	
	private HashMap<String,String> romMetaData;
	private boolean isBase;
	private String include;
	
	//TODO: Pull everything out of the HashMap and store it in fields
	
	//TODO: Synthesize XML Node for export

	private String xmlid;               //ID stored in XML
    private int    internalIdAddress;   //address of ECU version in image
    private String internalIdString;    //ID stored in image
    private String caseId;              //ECU hardware version
    private String ecuId;
    private String make;                //manufacturer
    private String market;
    private String model;
    private String subModel;            //trim, ie WRX
    private String transmission;
    private String year = "Unknown";
    private String flashMethod;         //flash method string used for ecuflash
    private String memModel;            //model used for reflashing with ecuflash
    private String editStamp;           //YYYY-MM-DD and v, the save count for this ROM
    private int    fileSize;
    private int    ramOffset;
    private boolean obsolete;           // whether a more recent revision exists

    public String toString() {
        return String.format(
                "%n   ---- RomID %s ----" +
                "%n   Internal ID Address: %s" +
                "%n   Internal ID String: %s" +
                "%n   Case ID: %s" +
                "%n   ECU ID: %s" +
                "%n   Make: %s" +
                "%n   Market: %s" +
                "%n   Model: %s" +
                "%n   Submodel: %s" +
                "%n   Transmission: %s" +
                "%n   Year: %s" +
                "%n   Flash Method: %s" +
                "%n   Memory Model: %s" +
                "%n   ---- End RomID %s ----",
                xmlid,
                internalIdAddress,
                internalIdString,
                caseId,
                ecuId,
                make,
                market,
                model,
                subModel,
                transmission,
                year,
                flashMethod,
                memModel,
                xmlid);
    }

	public DefinitionMetaData(){
		romMetaData = new HashMap<String,String>();
        this.internalIdString = "";
        this.caseId = "";
	}
	
	public void Load(List<Element> elements, String include){
		this.ReadECUFlashRomID(elements,include);
	}
	
	protected void ReadECUFlashRomID(List<Element> elements, String incl) {
		include = incl;
		//TODO: error handling for: missing romid, already populated romid, adding to romid?
		//TODO: reconcile differences between ECUFlash style and RR style
		
		for ( Element e : elements ){
			romMetaData.put(e.getName().toLowerCase(),e.getValue());
			
			if (e.getName().equalsIgnoreCase(Tags.XMLID)) {
                this.setXmlid(e.getValue());

            } else if (e.getName()
                    .equalsIgnoreCase(Tags.INTERNAL_ID_ADDRESS)) {
                this.setInternalIdAddress(RomAttributeParser
                        .parseHexString(e.getValue()));

            } else if (e.getName().equalsIgnoreCase(Tags.INTERNAL_ID_STRING)) {
                this.setInternalIdString(e.getValue());
                if (this.getInternalIdString() == null) {
                    this.setInternalIdString("");
                }

            } else if (e.getName().equalsIgnoreCase(Tags.CASE_ID)) {
                this.setCaseId(e.getValue());

            } else if (e.getName().equalsIgnoreCase(Tags.ECU_ID)) {
                this.setEcuId(e.getValue());

            } else if (e.getName().equalsIgnoreCase(Tags.MAKE)) {
                this.setMake(e.getValue());

            } else if (e.getName().equalsIgnoreCase(Tags.MARKET)) {
                this.setMarket(e.getValue());

            } else if (e.getName().equalsIgnoreCase(Tags.MODEL)) {
                this.setModel(e.getValue());

            } else if (e.getName().equalsIgnoreCase(Tags.SUB_MODEL)) {
                this.setSubModel(e.getValue());

            } else if (e.getName().equalsIgnoreCase(Tags.TRANSMISSION)) {
                this.setTransmission(e.getValue());

            } else if (e.getName().equalsIgnoreCase(Tags.YEAR)) {
                this.setYear(e.getValue());

            } else if (e.getName().equalsIgnoreCase(Tags.FLASHMETHOD)) {
                this.setFlashMethod(e.getValue());

            } else if (e.getName().equalsIgnoreCase(Tags.MEMORY_MODEL)) {
                this.setMemModel(e.getValue());

            } else if (e.getName().equalsIgnoreCase(Tags.FILESIZE)) {
                this.setFileSize(RomAttributeParser
                        .parseFileSize(e.getValue()));

            } else if (e.getName().equalsIgnoreCase(Tags.OBSOLETE)) {
                this.setObsolete(Boolean.parseBoolean(e.getValue()));

            } else { /* unexpected element in this (skip) */
            }	
		}
		  
		if(!romMetaData.containsKey(Tags.XMLID)){
			//THROW ERROR
		}
		else if(romMetaData.get(Tags.XMLID).toLowerCase().contains(Tags.BASE)){
			isBase = true;
		} else {
			isBase = false;
		}
	}

	protected SimpleEntry<String,Long> getInternalIDMap() {
		if(isBase)
			return new SimpleEntry<>(this.getXmlId(),this.getInternalIDAddress());
		else
			return new SimpleEntry<>(this.getInternalID(),this.getInternalIDAddress());
	}

	protected Long getInternalIDAddress() {
		return Long.parseLong(romMetaData.get(Tags.INTERNAL_ID_ADDRESS),16);
	}

	protected String getInternalID() {
		String id = romMetaData.get(Tags.INTERNAL_ID_STRING);
		if(isBase)
			id = getXmlId();
		if(id!=null)
			return id;
		else
			return romMetaData.get(Tags.INTERNAL_ID);
	}
	
	public String getIncludeString(){
		return this.include;
	}
	
	public String getXmlId() {
		String id = romMetaData.get(Tags.XMLID);
		if(id != null)
			return id;
		else
			return "Unknown";
	}

	public boolean isBase() {
		return isBase;
	}

	public String getFullModel() {
		String model = romMetaData.get(Tags.MODEL);
		String sub = romMetaData.get(Tags.SUB_MODEL);
		if(model != null){
			if(sub != null)
				return model + " " + sub;
			return model;
		}
		else if(isBase)
			return "Base";
		else
			return "Unknown";
	}
	
	
    public String getXmlid() {
        return xmlid;
    }

    public void setXmlid(String xmlid) {
        this.xmlid = xmlid;
    }

    public int getInternalIdAddress() {
        return internalIdAddress;
    }

    public void setInternalIdAddress(int internalIdAddress) {
        this.internalIdAddress = internalIdAddress;
    }

    public String getInternalIdString() {
        return internalIdString;
    }

    public void setInternalIdString(String internalIdString) {
        this.internalIdString = internalIdString;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getEcuId() {
        return ecuId;
    }

    public void setEcuId(String ecuId) {
        this.ecuId = ecuId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSubModel() {
        return subModel;
    }

    public void setSubModel(String subModel) {
        this.subModel = subModel;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getFlashMethod() {
        return flashMethod;
    }

    public void setFlashMethod(String flashMethod) {
        this.flashMethod = flashMethod;
    }

    public String getMemModel() {
        return memModel;
    }

    public void setMemModel(String memModel) {
        this.memModel = memModel;
    }

    public int getRamOffset() {
        return ramOffset;
    }

    public void setRamOffset(int ramOffset) {
        this.ramOffset = ramOffset;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isObsolete() {
        return obsolete;
    }

    public void setObsolete(boolean obsolete) {
        this.obsolete = obsolete;
    }

    public String getEditStamp() {
        return editStamp;
    }

    public void setEditStamp(String editStamp) {
        this.editStamp = editStamp;
    }
}
