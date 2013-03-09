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
import org.yaml.snakeyaml.representer.Representer;

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
	}

	@Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property,
            Object propertyValue, Tag customTag) {
		if (javaBean instanceof Point && "location".equals(property.getName().toLowerCase())) {
            return null;
        }
		if(javaBean instanceof Dimension && "size".equals(property.getName().toLowerCase())){
			return null;
		}
		NodeTuple tuple = null;
        tuple = super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
        Node valueNode = tuple.getValueNode();
        
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
}
