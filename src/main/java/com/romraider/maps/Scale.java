/*
 * RomRaider Open-Source Tuning, Logging and Reflashing
 * Copyright (C) 2006-2012 RomRaider.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

//This object defines the scaling factor and offset for calculating real values

package com.romraider.maps;

import java.io.Serializable;
import java.text.DecimalFormat;

import com.romraider.definition.Definition;

public class Scale implements Serializable {

    private static final long serialVersionUID = 5836610685159474795L;

    private String name = "Raw Value";
    private String unit = "0x";
    private String expression = "x";
    private String byteExpression = "x";
    private String format = "#.##";
    private String stringFormat;
    private String storageType;
    private double coarseIncrement = 2;
    private double fineIncrement = 1;
    private double min = 0.0;
    private double max = 0.0;

	private boolean isBlob;
	private byte[] disabledBlob;
	private byte[] enabledBlob;

	private Definition parentDefinition;

    public Scale() {
    }

    @Override
    public String toString() {
        return  "\n      ---- Scale ----" +
                "\n      Name: " + getName() +
                "\n      Expression: " + getExpression() +
                "\n      Byte Expression: " + getByteExpression() +
                "\n      Unit: " + getUnit() +
                "\n      Format: " + getFormat() +
                "\n      Coarse Increment: " + getCoarseIncrement() +
                "\n      Fine Increment: " + getFineIncrement() +
                "\n      Min: " + getMin() +
                "\n      Max: " + getMax() +
                "\n      ---- End Scale ----\n";
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

	public String format(double realValue) {

		if(stringFormat != null){
			return String.format(stringFormat, realValue);
		}
		else {
			DecimalFormat formatter = new DecimalFormat(format);
			return formatter.format(realValue);
		}
	}
	
    public String getFormat() {
    	if(stringFormat != null)
        	return stringFormat;
    	else
    		return format;
    }

	public void setStringFormat(String value) {
		this.stringFormat = value;		
	}
	
    public void setDecimalFormat(String format) {
        this.format = format;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public double getCoarseIncrement() {
        return coarseIncrement;
    }

    public void setCoarseIncrement(double increment) {
        this.coarseIncrement = increment;
    }

    public boolean isReady() {
        if (unit == null) {
            return false;
        } else if (expression == null) {
            return false;
        } else if (stringFormat == null && format == null) {
            return false;
        } else if (coarseIncrement < 1) {
            return false;
        }

        return true;
    }

    public String getByteExpression() {
        return byteExpression;
    }

    public void setByteExpression(String byteExpression) {
        this.byteExpression = byteExpression;
    }

    public double getFineIncrement() {
        return fineIncrement;
    }

    public void setFineIncrement(double fineIncrement) {
        this.fineIncrement = fineIncrement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    @Override
    public boolean equals(Object other) {
        try {
            if(null == other) {
                return false;
            }

            if(other == this) {
                return true;
            }

            if(!(other instanceof Scale)) {
                return false;
            }

            Scale otherScale = (Scale)other;

            if( (null == this.getName() && null == otherScale.getName())
                    || (this.getName().isEmpty() && otherScale.getName().isEmpty()) ) {
                ;// Skip name compare if name is null or empty.
            } else {
                if(!this.getName().equalsIgnoreCase(otherScale.getName())) {
                    return false;
                }
            }

            if(!this.getUnit().equals(otherScale.getUnit()))
            {
                return false;
            }

            if(!this.getExpression().equals(otherScale.getExpression()))
            {
                return false;
            }

            if(!this.getByteExpression().equals(otherScale.getByteExpression()))
            {
                return false;
            }

            if(!this.getFormat().equals(otherScale.getFormat()))
            {
                return false;
            }

            if(this.getCoarseIncrement() != otherScale.getCoarseIncrement())
            {
                return false;
            }

            if(this.getFineIncrement() != otherScale.getFineIncrement())
            {
                return false;
            }

            if(this.getMin() != otherScale.getMin())
            {
                return false;
            }

            if(this.getMax() != otherScale.getMax())
            {
                return false;
            }

            return true;
        } catch(Exception ex) {
            return false;
        }
    }

	public String getStorageType() {
		return storageType;
	}

	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}

	public boolean isBlob() {
		return isBlob;
	}

	public void setBlob(boolean isBlob) {
		this.isBlob = isBlob;
	}

	public void setDisabledBlob(byte[] blob) {
		this.disabledBlob = blob;
	}
	
	public byte[] getDisabledBlob(){
		return this.disabledBlob;
	}
	
	public void setEnabledBlob(byte[] blob) {
		this.enabledBlob = blob;
	}
	
	public byte[] getEnabledBlob(){
		return this.enabledBlob;
	}

	public void setParentDefinition(Definition parent) {
		this.parentDefinition = parent;
	}
	
	public Definition getParentDefinition(){
		return this.parentDefinition;
	}

}