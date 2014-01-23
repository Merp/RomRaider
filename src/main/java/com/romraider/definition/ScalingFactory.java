package com.romraider.definition;

import org.jdom2.Attribute;
import org.jdom2.Element;

import com.romraider.maps.Scale;
import com.romraider.util.ByteUtil;

public class ScalingFactory {
	
	//TODO: Synthesize XML Node for export
	
	public static Scale CreateScaling(Element node, Definition parent){
		return ReadECUFlashScaling(node,parent);
		//TODO, choose definition type???
	}
	
	private static Scale ReadECUFlashScaling(Element node, Definition parent) {
		Scale scale = new Scale();
		scale.setParentDefinition(parent);
		for (Attribute a : node.getAttributes()){
			switch(a.getName().toLowerCase()){

			case Tags.SCALING_NAME:
				scale.setName(a.getValue());
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
				scale.setStringFormat(a.getValue());
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
				scale.setStorageType(a.getValue());
				if(a.getValue() == Tags.BLOB_LIST)
					ReadECUFlashBlobScaling(node, scale);
				continue;
				
			default:
				//TODO: Log warning, also add exception/handling to this class.
				continue;
			}
		}
		return scale;
	}
	
	private static void ReadECUFlashBlobScaling(Element node, Scale scale){
		scale.setBlob(true);
		for(Element ch : node.getChildren()){
			if(ch.getName() == "data"){
				switch(ch.getAttribute("name").getValue()){
				case "disabled":
					scale.setDisabledBlob(ByteUtil.HexStringToByteArray(ch.getAttribute("value").getValue()));
					continue;
					
				case "enabled":
					scale.setEnabledBlob(ByteUtil.HexStringToByteArray(ch.getAttribute("value").getValue()));
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

}
