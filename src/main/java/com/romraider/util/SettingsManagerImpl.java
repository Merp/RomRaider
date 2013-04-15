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

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.apache.log4j.Logger.getLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import com.romraider.Settings;
import com.romraider.swing.JProgressPane;
import com.romraider.yaml.SkipEmptyRepresenterAwtSafe;

public final class SettingsManagerImpl implements SettingsManager {
	private static final Logger LOGGER = getLogger(SettingsManager.class);
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
    public void save(Settings settings, JProgressPane progress) {
        saveYaml(settings);
        //TODO implement saveYaml with progress bar.
    }
    
    private Settings loadYaml() {
    	try{
    		LOGGER.info("Loading YAML settings file from " + SETTINGS_YAML_FILE);
	    	Yaml yaml = new Yaml();
	    	FileReader fr = new FileReader(SETTINGS_YAML_FILE);
	    	return (Settings) yaml.load(fr);
	    } catch (FileNotFoundException e) {
	    	LOGGER.info("Yaml settings file not found, using default settings");
	        showMessageDialog(null, "Settings file not found.\nUsing default settings.",
	                "Error Loading Settings", INFORMATION_MESSAGE);
	        return new Settings();
	    } catch (Exception e) {
	    	LOGGER.error("Error loading YAML settings file: " + e.getMessage());
	        throw new RuntimeException(e);
	    }
    }
    
    private void saveYaml(Settings settings){
    	Yaml yaml = new Yaml(new SkipEmptyRepresenterAwtSafe());
    	try {
    		LOGGER.info("Saving YAML settings file to " + SETTINGS_YAML_FILE);
    		new File(HOME + "/.RomRaider").mkdir();
			FileWriter fw = new FileWriter(SETTINGS_YAML_FILE);
			yaml.dump(settings,fw);
			fw.close();
    	} catch (IOException e) {
    		LOGGER.error("Error saving YAML settings file: " + e.getMessage());
			throw new RuntimeException(e);
		}
    }
}
