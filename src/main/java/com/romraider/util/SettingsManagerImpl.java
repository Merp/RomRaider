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

package com.romraider.util;

import static com.romraider.Version.VERSION;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.yaml.snakeyaml.Yaml;
import com.romraider.Settings;
import com.romraider.swing.JProgressPane;
import com.romraider.xml.DOMSettingsBuilder;
import com.romraider.xml.DOMSettingsUnmarshaller;
import com.romraider.yaml.SkipEmptyRepresenterAwtSafe;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;

public final class SettingsManagerImpl implements SettingsManager {
    private static final String SETTINGS_FILE = "/.RomRaider/settings.xml";
    private static final String HOME = System.getProperty("user.home");
    private static final String SETTINGS_YAML_FILE = HOME + "/.RomRaider/settings.yaml";

    @Override
    public Settings load(){
    	return loadYaml();
    }
    
    @Override
    public void save(Settings s){
    	saveYaml(s);
    }
    
    @Override
    public Settings loadYaml() {
    	try{
	    	Yaml yaml = new Yaml();
	    	FileReader fr = new FileReader(SETTINGS_YAML_FILE);
	    	return (Settings) yaml.load(fr);
	    } catch (FileNotFoundException e) {
	        showMessageDialog(null, "Settings file not found.\nUsing default settings.",
	                "Error Loading Settings", INFORMATION_MESSAGE);
	        return new Settings();
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
    }
    
    @Override
    public void saveYaml(Settings settings){
    	Yaml yaml = new Yaml(new SkipEmptyRepresenterAwtSafe());
    	try {
    		new File(HOME + "/.RomRaider").mkdir();
			FileWriter fw = new FileWriter(SETTINGS_YAML_FILE);
			yaml.dump(settings,fw);
			fw.close();
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
    public Settings loadXML() {
        try {
            InputSource src = new InputSource(new FileInputStream(new File(HOME + SETTINGS_FILE)));
            DOMSettingsUnmarshaller domUms = new DOMSettingsUnmarshaller();
            DOMParser parser = new DOMParser();
            parser.parse(src);
            Document doc = parser.getDocument();
            return domUms.unmarshallSettings(doc.getDocumentElement());
        } catch (FileNotFoundException e) {
            showMessageDialog(null, "Settings file not found.\nUsing default settings.",
                    "Error Loading Settings", INFORMATION_MESSAGE);
            return new Settings();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveXML(Settings settings) {
        save(settings, new JProgressPane());
    }

    @Override
    public void save(Settings settings, JProgressPane progress) {
        DOMSettingsBuilder builder = new DOMSettingsBuilder();
        try {
            new File(HOME + "/.RomRaider/").mkdir();		// Creates directory if it does not exist
            builder.buildSettings(settings, new File(HOME + SETTINGS_FILE), progress, VERSION);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
