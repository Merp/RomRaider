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

package com.romraider.definition;

import static com.romraider.Version.PRODUCT_NAME;
import static java.io.File.separator;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import ZoeloeSoft.projects.JFontChooser.JFontChooser;

import com.romraider.ECUExec;
import com.romraider.Settings;
import com.romraider.definition.DefinitionRepoManager;
import com.romraider.editor.ecu.ECUEditor;
import com.romraider.editor.ecu.ECUEditorManager;
import com.romraider.logger.ecu.EcuLogger;
import com.romraider.util.FileAssociator;
import com.romraider.util.SettingsManager;

import javax.swing.UIManager;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.event.MouseAdapter;

import javax.swing.ListSelectionModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.ComponentOrientation;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;
import javax.swing.JSeparator;
import javax.swing.JMenuBar;

public class DefinitionEditor extends JFrame implements MouseListener {
	public DefinitionEditor() {
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		springLayout.putConstraint(SpringLayout.NORTH, tabbedPane, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, tabbedPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, tabbedPane, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, tabbedPane, 452, SpringLayout.WEST, getContentPane());
		getContentPane().add(tabbedPane);
		
		JScrollPane scrollPane = new JScrollPane();
		
		
		
		JTree RomTree = new JTree(initRomTree());
		scrollPane.setViewportView(RomTree);
		//scrollPane.setColumnHeaderView(RomTree);
		
		tabbedPane.addTab("Rom Definitions", null, scrollPane, null);

		
		
		JTree ScalingTree = new JTree();
		tabbedPane.addTab("New tab", null, ScalingTree, null);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
	}
	
	private DefaultMutableTreeNode initRomTree(){
		DefaultMutableTreeNode top =
		        new DefaultMutableTreeNode("Definitions by Calibration ID");

		DefaultMutableTreeNode modelNode = null;
		
	    for(Entry<String, TreeMap<String, Definition>> e : ECUExec.getDefinitionManager().getDefinitionTreeMap().entrySet()){
			modelNode = new DefaultMutableTreeNode(e.getKey());
			for(Entry<String,Definition> defEntry : e.getValue().entrySet()){
				Definition tihd = defEntry.getValue().getInheritedDefinition();
				if(tihd != null && e.getValue().containsKey(tihd.getInternalID()))
					continue;
				modelNode.add(CreateInheritingNodes(defEntry.getValue()));
			}
			top.add(modelNode);
		}	
	    
	    return top;
	}
	
	private DefaultMutableTreeNode CreateInheritingNodes(Definition d){
		DefaultMutableTreeNode defNode = null;
		defNode = new DefaultMutableTreeNode(d.getInternalID());
		List<Definition> tdl = ECUExec.getDefinitionManager().getInheritingDefinitions(d);
		if(tdl != null && tdl.size()>0){
			for(Definition inhd : tdl)
				defNode.add(CreateInheritingNodes(inhd));
		}
		return defNode;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}