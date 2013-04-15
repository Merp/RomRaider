package com.romraider.yaml;

import java.awt.Dimension;
import java.awt.Point;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import com.romraider.io.connection.ConnectionProperties;
import com.romraider.io.connection.ConnectionPropertiesImpl;
import com.romraider.logger.ecu.definition.EcuDefinition;
import com.romraider.logger.ecu.definition.EcuDefinitionImpl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Skips empty and null values and prevents stack overflow caused by recursion
 * Due to java.awt.Point and Dimension classes returning instances of themselves (DERP).
 */
public class SkipEmptyRepresenterAwtSafe extends Representer {
    public SkipEmptyRepresenterAwtSafe(PropertyUtils propUtils) {
    	super();
    	this.setPropertyUtils(propUtils);
	}

	public SkipEmptyRepresenterAwtSafe() {
		super();
		this.representers.put(File.class, (Represent) new FileRepresenter());
	}

	@Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property,
            Object propertyValue, Tag customTag) {
		if (javaBean instanceof Point && "location".equals(property.getName().toLowerCase())) {
            return null;
        }
		if(javaBean instanceof Dimension && "size".equals(property.getName().toLowerCase())){
			return null;
		};
		Node valueNode;
		NodeTuple tuple = null;
        tuple = super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
        valueNode = tuple.getValueNode();
        
		if (Tag.NULL.equals(valueNode.getTag())) {
            return null;// skip 'null' values
        }
        if (valueNode instanceof CollectionNode) {
            if (Tag.SEQ.equals(valueNode.getTag())) {
                SequenceNode seq = (SequenceNode) valueNode;
                if (seq.getValue().isEmpty()) {
                    return null;// skip empty lists
                }
            }
            if (Tag.MAP.equals(valueNode.getTag())) {
                MappingNode seq = (MappingNode) valueNode;
                if (seq.getValue().isEmpty()) {
                    return null;// skip empty maps
                }
            }
        }
        return tuple;
    }
	
	

public class FileRepresenter implements Represent {
    public Node representData(Object data) {
        File file = (File) data;
        Node scalar = representScalar(new Tag("!!java.io.File"), file.getAbsolutePath());
        return scalar;
    }
}

}
