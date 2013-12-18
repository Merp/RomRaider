package com.romraider.definition;

import org.jdom2.Attribute;
import org.jdom2.Element;

import com.romraider.maps.Scale;
import com.romraider.util.ByteUtil;

public class ScalingDef {
	
	private boolean isBlob;
	private Definition parentDefinition;
	private String name;
	private String storageType;
	private Scale scale;
	private byte[] disabledBlob;
	private byte[] enabledBlob;
	
	public ScalingDef(Element node,Definition parent){
		parentDefinition = parent;
		ReadECUFlashScaling(node);
	}
	
	private void ReadECUFlashScaling(Element node) {
		this.scale = new Scale();
		for (Attribute a : node.getAttributes()){
			switch(a.getName().toLowerCase()){

			case Tags.SCALING_NAME:
				scale.setName(a.getValue());
				name = a.getValue();
				continue;
				
			case Tags.UNITS:
				scale.setUnit(a.getValue());
				continue;
				
			case Tags.TO_EXPRESSION:
				scale.setExpression(a.getValue());
				continue;
			
			case Tags.FROM_EXPRESSION:
				scale.setByteExpression(a.getValue());
				continue;
				
			case Tags.FORMAT:
				scale.setFormat(a.getValue());//TODO: THESE ARE INCOMPATIBLE!! PULL CODE FROM SHARPTUNE??
				continue;
			
			case Tags.INCREMENT:
				scale.setCoarseIncrement(Double.parseDouble(a.getValue()));
				scale.setFineIncrement(scale.getCoarseIncrement()/2);
				continue;
				
			case Tags.MINIMUM:
				scale.setMin(Double.parseDouble(a.getValue()));
				continue;
				
			case Tags.MAXIMUM:
				scale.setMax(Double.parseDouble(a.getValue()));
				continue;
			
			case Tags.STORAGE_TYPE:
				storageType = a.getValue();
				if(a.getValue() == Tags.BLOB_LIST)
					ReadECUFlashBlobScaling(node);
				//scale.setStorageType(a.getValue())//TODO: DECIDE WHAT TO DO WITH THIS!!!
				///I THINK THIS SHOULD BE GROUPED WITH THIS DATA, if we split things off into:
				/// 1: table data scaling to native units AND 2: interchangeable data scaling
				/// we will need to split these!!
				/// 1. requires: storagetype, units, min/max? toexpr, frexpr, name
				/// 2. requires defining the relationships between different units.
				/// THEN, users can select default units.
				continue;
			}
			if(scale.isReady()){
				//TODO: do something with these scales now.
			}
		}
	}
	
	private void ReadECUFlashBlobScaling(Element node){
		this.isBlob = true;
		for(Element ch : node.getChildren()){
			if(ch.getName() == "data"){
				switch(ch.getAttribute("name").getValue()){
				case "disabled":
					disabledBlob = ByteUtil.HexStringToByteArray(ch.getAttribute("value").getValue());
					continue;
					
				case "enabled":
					enabledBlob = ByteUtil.HexStringToByteArray(ch.getAttribute("value").getValue());
					continue;
					
				default:
					continue;
				}
			}
		}
	}
/*
* <scaling name="KnockCorrectionAdvanceAlternateMode" storagetype="bloblist">
<data name="disabled" value="FF" />
<data name="enabled" value="00" />
</scaling>
*/

	public boolean isBlob() {
		return isBlob;
	}

	public String getName() {
		return name;
	}

}
