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

package com.romraider.swing;

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
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;

import ZoeloeSoft.projects.JFontChooser.JFontChooser;

import com.romraider.ECUExec;
import com.romraider.Settings;
import com.romraider.editor.ecu.ECUEditor;
import com.romraider.logger.ecu.EcuLogger;
import com.romraider.util.FileAssociator;
import javax.swing.UIManager;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.border.BevelBorder;
import java.awt.event.MouseAdapter;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.ComponentOrientation;
import javax.swing.JScrollPane;

public class SettingsForm extends JFrame implements MouseListener {

    private static final long serialVersionUID = 3910602424260147767L;
    Settings settings = ECUExec.settings;
    ECUEditor parentEditor;
    EcuLogger parentLogger;

    public static int MOVE_UP = 0;
    public static int MOVE_DOWN = 1;
    private Vector<String> ecuDefFileNames = new Vector<String>();	
    
    /**
     * @wbp.parser.constructor
     */
    public SettingsForm(ECUEditor parent) {
        this.setIconImage(parent.getIconImage());
        this.parentEditor = parent;
        initComponents();
        initSettings();

        maxColor.addMouseListener(this);
        minColor.addMouseListener(this);
        highlightColor.addMouseListener(this);
        axisColor.addMouseListener(this);
        increaseColor.addMouseListener(this);
        decreaseColor.addMouseListener(this);
        warningColor.addMouseListener(this);

        btnOk.addMouseListener(this);
        btnApply.addMouseListener(this);
        btnCancel.addMouseListener(this);
        btnChooseFont.addMouseListener(this);
        reset.addMouseListener(this);
        btnAddAssocs.addMouseListener(this);
        btnRemoveAssocs.addMouseListener(this);

        tableClickCount.setBackground(Color.WHITE);

        // disable file association buttons if user is not in Windows
        StringTokenizer osName = new StringTokenizer(System.getProperties().getProperty("os.name"));
        if (!osName.nextToken().equalsIgnoreCase("windows")) {
            btnAddAssocs.setEnabled(false);
            btnRemoveAssocs.setEnabled(false);
            extensionHex.setEnabled(false);
            extensionBin.setEnabled(false);
        }

    }
    
    public SettingsForm(EcuLogger parent) {
        this.setIconImage(parent.getIconImage());
        this.parentLogger = parent;
        initComponents();
        initSettings();

        maxColor.addMouseListener(this);
        minColor.addMouseListener(this);
        highlightColor.addMouseListener(this);
        axisColor.addMouseListener(this);
        increaseColor.addMouseListener(this);
        decreaseColor.addMouseListener(this);
        warningColor.addMouseListener(this);

        btnOk.addMouseListener(this);
        btnApply.addMouseListener(this);
        btnCancel.addMouseListener(this);
        btnChooseFont.addMouseListener(this);
        reset.addMouseListener(this);
        btnAddAssocs.addMouseListener(this);
        btnRemoveAssocs.addMouseListener(this);

        tableClickCount.setBackground(Color.WHITE);

        // disable file association buttons if user is not in Windows
        StringTokenizer osName = new StringTokenizer(System.getProperties().getProperty("os.name"));
        if (!osName.nextToken().equalsIgnoreCase("windows")) {
            btnAddAssocs.setEnabled(false);
            btnRemoveAssocs.setEnabled(false);
            extensionHex.setEnabled(false);
            extensionBin.setEnabled(false);
        }

    }


    private void initSettings() {

        obsoleteWarning.setSelected(settings.isObsoleteWarning());
        calcConflictWarning.setSelected(settings.isCalcConflictWarning());
        displayHighTables.setSelected(settings.isDisplayHighTables());
        saveDebugTables.setSelected(settings.isSaveDebugTables());
        debug.setSelected(settings.isDebug());

        maxColor.setBackground(settings.getMaxColor());
        minColor.setBackground(settings.getMinColor());
        highlightColor.setBackground(settings.getHighlightColor());
        axisColor.setBackground(settings.getAxisColor());
        increaseColor.setBackground(settings.getIncreaseBorder());
        decreaseColor.setBackground(settings.getDecreaseBorder());

        cellWidth.setText(((int) settings.getCellSize().getWidth()) + "");
        cellHeight.setText(((int) settings.getCellSize().getHeight()) + "");

        btnChooseFont.setFont(settings.getTableFont());
        btnChooseFont.setText(settings.getTableFont().getFontName());

        if (settings.getTableClickCount() == 1) { // single click opens table
            tableClickCount.setSelectedIndex(0);
        } else { // double click opens table
            tableClickCount.setSelectedIndex(1);
        }

        valueLimitWarning.setSelected(settings.isValueLimitWarning());
        warningColor.setBackground(settings.getWarningColor());

        if(settings.getTableClipboardFormat().equalsIgnoreCase(Settings.AIRBOYS_CLIPBOARD_FORMAT)) {
            this.rdbtnAirboys.setSelected(true);
        } else if(settings.getTableClipboardFormat().equalsIgnoreCase(Settings.CUSTOM_CLIPBOARD_FORMAT)) {
            this.rdbtnCustom.setSelected(true);
        } else {
            this.rdbtnDefault.setSelected(true);
        }

        this.textFieldEditorIconScale.setText(String.valueOf(settings.getEditorIconScale()));
        this.textFieldTableIconScale.setText(String.valueOf(settings.getTableIconScale()));
        
        // add definitions to list
        Vector<File> definitionFiles = settings.getEcuDefinitionFiles();
        ecuDefFileNames = new Vector<String>();

        for (int i = 0; i < definitionFiles.size(); i++) {
            ecuDefFileNames.add(definitionFiles.get(i).getAbsolutePath());
        }

        updateEcuDefListModel();

    }

    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        obsoleteWarning = new javax.swing.JCheckBox();
        calcConflictWarning = new javax.swing.JCheckBox();
        debug = new javax.swing.JCheckBox();
        btnCancel = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        btnApply = new javax.swing.JButton();
        reset = new javax.swing.JButton();
        settingsTabbedPane = new javax.swing.JTabbedPane();
        jPanelClipboard = new javax.swing.JPanel();
        jPanelDefault = new javax.swing.JPanel();
        jPanelTableDisplay = new javax.swing.JPanel();
        jPanelIcons = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblAxis = new javax.swing.JLabel();
        lblHighlight = new javax.swing.JLabel();
        lblMin = new javax.swing.JLabel();
        lblMax = new javax.swing.JLabel();
        maxColor = new javax.swing.JLabel();
        minColor = new javax.swing.JLabel();
        highlightColor = new javax.swing.JLabel();
        axisColor = new javax.swing.JLabel();
        warningColor = new javax.swing.JLabel();
        lblWarning = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblIncrease = new javax.swing.JLabel();
        increaseColor = new javax.swing.JLabel();
        decreaseColor = new javax.swing.JLabel();
        lblDecrease = new javax.swing.JLabel();
        lblCellHeight = new javax.swing.JLabel();
        cellHeight = new javax.swing.JTextField();
        cellWidth = new javax.swing.JTextField();
        lblCellWidth = new javax.swing.JLabel();
        lblFont = new javax.swing.JLabel();
        btnChooseFont = new javax.swing.JButton();
        saveDebugTables = new javax.swing.JCheckBox();
        displayHighTables = new javax.swing.JCheckBox();
        valueLimitWarning = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        extensionHex = new javax.swing.JCheckBox();
        extensionBin = new javax.swing.JCheckBox();
        btnAddAssocs = new javax.swing.JButton();
        btnRemoveAssocs = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tableClickCount = new javax.swing.JComboBox();
        editorIconsPanel = new javax.swing.JPanel();
        tableIconsPanel = new javax.swing.JPanel();;

        clipboardButtonGroup = new ButtonGroup();
        rdbtnDefault = new JRadioButton("RomRaider Default");
        rdbtnAirboys = new JRadioButton("Airboys Spreadsheet");
        rdbtnCustom = new JRadioButton("Custom (manually specify formats in settings.xml)");
        clipboardButtonGroup.add(this.rdbtnDefault);
        clipboardButtonGroup.add(this.rdbtnAirboys);
        clipboardButtonGroup.add(this.rdbtnCustom);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(PRODUCT_NAME + " Settings");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFont(new java.awt.Font("Tahoma", 0, 11));
        obsoleteWarning.setText("Warn me when opening out of date ECU image revision");
        obsoleteWarning.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        obsoleteWarning.setMargin(new java.awt.Insets(0, 0, 0, 0));

        calcConflictWarning.setText("Warn me when real and byte value calculations conflict");
        calcConflictWarning.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        calcConflictWarning.setMargin(new java.awt.Insets(0, 0, 0, 0));

        debug.setText("Debug mode");
        debug.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        debug.setEnabled(false);
        debug.setMargin(new java.awt.Insets(0, 0, 0, 0));

        btnCancel.setMnemonic('C');
        btnCancel.setText("Cancel");

        btnOk.setMnemonic('O');
        btnOk.setText("OK");

        btnApply.setMnemonic('A');
        btnApply.setText("Apply");

        reset.setText("Restore Defaults");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Background"));
        lblAxis.setText("Axis Cell:");

        lblHighlight.setText("Highlighted Cell:");

        lblMin.setText("Minimum Value:");

        lblMax.setText("Maximum Value:");

        maxColor.setBackground(new java.awt.Color(255, 0, 0));
        maxColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        maxColor.setOpaque(true);

        minColor.setBackground(new java.awt.Color(255, 0, 0));
        minColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        minColor.setOpaque(true);

        highlightColor.setBackground(new java.awt.Color(255, 0, 0));
        highlightColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        highlightColor.setOpaque(true);

        axisColor.setBackground(new java.awt.Color(255, 0, 0));
        axisColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        axisColor.setOpaque(true);

        warningColor.setBackground(new java.awt.Color(255, 0, 0));
        warningColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        warningColor.setOpaque(true);

        lblWarning.setText("Warning:");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(lblWarning)
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jPanel2Layout.createSequentialGroup()
                                                .add(4, 4, 4)
                                                .add(lblMin))
                                                .add(lblMax)))
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jPanel2Layout.createSequentialGroup()
                                                                .add(maxColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 22, Short.MAX_VALUE)
                                                                .add(lblHighlight)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                .add(highlightColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                                .add(jPanel2Layout.createSequentialGroup()
                                                                        .add(minColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 55, Short.MAX_VALUE)
                                                                        .add(lblAxis)
                                                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(axisColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                                        .add(warningColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                                        .addContainerGap())
                );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(lblMax)
                                .add(maxColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(highlightColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(lblHighlight))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(lblMin)
                                        .add(minColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(axisColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(lblAxis))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                .add(warningColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .add(lblWarning)))
                );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Cell Borders"));
        lblIncrease.setText("Increased:");

        increaseColor.setBackground(new java.awt.Color(255, 0, 0));
        increaseColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        increaseColor.setOpaque(true);

        decreaseColor.setBackground(new java.awt.Color(255, 0, 0));
        decreaseColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        decreaseColor.setOpaque(true);

        lblDecrease.setText("Decreased:");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(lblIncrease)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(increaseColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 59, Short.MAX_VALUE)
                        .add(lblDecrease)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(decreaseColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(decreaseColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(lblDecrease)
                        .add(lblIncrease)
                        .add(increaseColor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                );

        lblCellHeight.setText("Cell Height:");

        lblCellWidth.setText("Cell Width:");

        lblFont.setText("Font:");

        btnChooseFont.setText("Choose");

        saveDebugTables.setText("Save changes made on tables in debug mode");
        saveDebugTables.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        saveDebugTables.setMargin(new java.awt.Insets(0, 0, 0, 0));

        displayHighTables.setText("List tables that are above my userlevel");
        displayHighTables.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        displayHighTables.setMargin(new java.awt.Insets(0, 0, 0, 0));

        valueLimitWarning.setText("Warn when values exceed limits");
        valueLimitWarning.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        valueLimitWarning.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("File Associations"));
        extensionHex.setText("HEX");
        extensionHex.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        extensionHex.setMargin(new java.awt.Insets(0, 0, 0, 0));

        extensionBin.setText("BIN");
        extensionBin.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        extensionBin.setMargin(new java.awt.Insets(0, 0, 0, 0));

        btnAddAssocs.setText("Add Associations");

        btnRemoveAssocs.setText("Remove Associations");

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(extensionBin)
                                .add(extensionHex))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 93, Short.MAX_VALUE)
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                        .add(btnAddAssocs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(btnRemoveAssocs))
                                        .add(25, 25, 25))
                );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel4Layout.createSequentialGroup()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(btnAddAssocs)
                                .add(extensionHex))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(btnRemoveAssocs)
                                        .add(extensionBin)))
                );

        jLabel1.setText("click to open tables");

        tableClickCount.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Single", "Double"}));

        initTabs();

        settingsTabbedPane.addTab("General", jPanelDefault);
        
        JPanel jPanelDefs = new JPanel();
        
        settingsTabbedPane.addTab("Definitions", null, jPanelDefs, null);
        
        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Git Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        JLabel lblRepositoryUrl = new JLabel("Remote URL:");
        
        textFieldGitRepo = new JTextField(settings.getGitCurrentRemoteUrl());
        textFieldGitRepo.setToolTipText("The percentage of the icons original size.");
        textFieldGitRepo.setColumns(10);
        
        JLabel lblBranch = new JLabel("Branch:");
        
        comboBoxGitBranch = new JComboBox();
        comboBoxGitBranch.setEditable(true);
        updateComboBoxGitBranch();
        
        btnAddRemote = new JButton("Add + Fetch Remote");
        btnAddRemote.addMouseListener(this);

        btnFetchResetGit = new JButton("Fetch + Checkout Branch");
        btnFetchResetGit.addMouseListener(this);
        
        lblRemoteName = new JLabel("Remote Name:");
        
        textFieldRemoteName = new JTextField(settings.getGitCurrentRemoteName());
        textFieldRemoteName.setToolTipText("Enter a short name for this remote repository");
        textFieldRemoteName.setColumns(10);
       
        
        GroupLayout gl_panel_1 = new GroupLayout(panel_1);
        gl_panel_1.setHorizontalGroup(
        	gl_panel_1.createParallelGroup(Alignment.LEADING)
        		.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
        				.addComponent(btnFetchResetGit)
        				.addGroup(gl_panel_1.createSequentialGroup()
        					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
        						.addGroup(gl_panel_1.createSequentialGroup()
        							.addComponent(lblRemoteName)
        							.addGap(6))
        						.addGroup(gl_panel_1.createSequentialGroup()
        							.addComponent(lblBranch, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.RELATED))
        						.addGroup(gl_panel_1.createSequentialGroup()
        							.addComponent(lblRepositoryUrl)
        							.addPreferredGap(ComponentPlacement.RELATED)))
        					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
        						.addComponent(textFieldGitRepo, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
        						.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
        							.addComponent(comboBoxGitBranch, Alignment.LEADING, 0, 300, Short.MAX_VALUE)
        							.addGroup(gl_panel_1.createSequentialGroup()
        								.addComponent(textFieldRemoteName, GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
        								.addPreferredGap(ComponentPlacement.RELATED)
        								.addComponent(btnAddRemote))))))
        			.addGap(285))
        );
        gl_panel_1.setVerticalGroup(
        	gl_panel_1.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_panel_1.createSequentialGroup()
        			.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
        				.addComponent(textFieldGitRepo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(lblRepositoryUrl))
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
        				.addComponent(textFieldRemoteName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(lblRemoteName)
        				.addComponent(btnAddRemote))
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
        				.addComponent(comboBoxGitBranch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(lblBranch))
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(btnFetchResetGit)
        			.addGap(57))
        );
        panel_1.setLayout(gl_panel_1);
        
        JPanel panel_2 = new JPanel();
        panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "ECU Definition File Priority", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        buttonEcuDefMoveUp = new JButton();
        buttonEcuDefMoveUp.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		moveSelection(MOVE_UP);
        	}
        });
        buttonEcuDefMoveUp.setText("Move Up");
        
        buttonEcuDefAdd = new JButton();
        buttonEcuDefAdd.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		addEcuDefFile();
        	}
        });
        buttonEcuDefAdd.setText("Add...");
        
        buttonEcuDefRemove = new JButton();
        buttonEcuDefRemove.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		removeSelection();
        	}
        });
        buttonEcuDefRemove.setText("Remove");
        
        buttonEcuDefMoveDown = new JButton();
        buttonEcuDefMoveDown.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		moveSelection(MOVE_DOWN);
        	}
        });
        buttonEcuDefMoveDown.setText("Move Down");
        
        JScrollPane scrollPane = new JScrollPane();
        GroupLayout gl_panel_2 = new GroupLayout(panel_2);
        gl_panel_2.setHorizontalGroup(
        	gl_panel_2.createParallelGroup(Alignment.TRAILING)
        		.addGroup(Alignment.LEADING, gl_panel_2.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
        				.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
        				.addGroup(gl_panel_2.createSequentialGroup()
        					.addComponent(buttonEcuDefMoveDown)
        					.addGap(6)
        					.addComponent(buttonEcuDefMoveUp)
        					.addPreferredGap(ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
        					.addComponent(buttonEcuDefAdd)
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addComponent(buttonEcuDefRemove)))
        			.addContainerGap())
        );
        gl_panel_2.setVerticalGroup(
        	gl_panel_2.createParallelGroup(Alignment.LEADING)
        		.addGroup(Alignment.TRAILING, gl_panel_2.createSequentialGroup()
        			.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
        				.addComponent(buttonEcuDefMoveDown)
        				.addComponent(buttonEcuDefMoveUp)
        				.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
        					.addComponent(buttonEcuDefRemove)
        					.addComponent(buttonEcuDefAdd)))
        			.addGap(6))
        );
        
        ecuDefinitionList = new JList();
        scrollPane.setViewportView(ecuDefinitionList);
        ecuDefinitionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ecuDefinitionList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        panel_2.setLayout(gl_panel_2);
        
        panel = new JPanel();
        panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Logger Definition Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        lblFilepath = new JLabel("FilePath:");
        
        comboBoxLoggerDef = new JComboBox(settings.getAvailableLoggerDefs().keySet().toArray());
        comboBoxLoggerDef.setSelectedItem(new File(settings.getLoggerDefFilePath()).getName());
        comboBoxLoggerDef.setSelectedItem(settings.getLoggerDefFilePath());
        comboBoxLoggerDef.setEditable(true);
        
        btnLoggerDefChooseFile = new JButton("Choose External File...");
        btnLoggerDefChooseFile.addMouseListener(this);
        
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
        	gl_panel.createParallelGroup(Alignment.TRAILING)
        		.addGroup(gl_panel.createSequentialGroup()
        			.addContainerGap(31, Short.MAX_VALUE)
        			.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
        				.addComponent(btnLoggerDefChooseFile)
        				.addGroup(gl_panel.createSequentialGroup()
        					.addComponent(lblFilepath)
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addComponent(comboBoxLoggerDef, GroupLayout.PREFERRED_SIZE, 288, GroupLayout.PREFERRED_SIZE)))
        			.addGap(24))
        );
        gl_panel.setVerticalGroup(
        	gl_panel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_panel.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(lblFilepath)
        				.addComponent(comboBoxLoggerDef, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addPreferredGap(ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
        			.addComponent(btnLoggerDefChooseFile)
        			.addContainerGap())
        );
        panel.setLayout(gl_panel);
        GroupLayout gl_jPanelDefs = new GroupLayout(jPanelDefs);
        gl_jPanelDefs.setHorizontalGroup(
        	gl_jPanelDefs.createParallelGroup(Alignment.LEADING)
        		.addGroup(Alignment.TRAILING, gl_jPanelDefs.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(gl_jPanelDefs.createParallelGroup(Alignment.TRAILING)
        				.addComponent(panel_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
        				.addComponent(panel_1, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 407, Short.MAX_VALUE)
        				.addComponent(panel, GroupLayout.PREFERRED_SIZE, 407, GroupLayout.PREFERRED_SIZE))
        			.addContainerGap())
        );
        gl_jPanelDefs.setVerticalGroup(
        	gl_jPanelDefs.createParallelGroup(Alignment.TRAILING)
        		.addGroup(gl_jPanelDefs.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(panel, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap())
        );
        jPanelDefs.setLayout(gl_jPanelDefs);
        settingsTabbedPane.addTab("Table Display", jPanelTableDisplay);
        settingsTabbedPane.addTab("Clipboard", jPanelClipboard);
        settingsTabbedPane.addTab("Icons", jPanelIcons);

        editorIconsPanel = new JPanel();

        // Content Pane Layout
        GroupLayout layout = new GroupLayout(getContentPane());
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addComponent(settingsTabbedPane, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 432, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(reset)
                                        .addPreferredGap(ComponentPlacement.RELATED, 136, Short.MAX_VALUE)
                                        .addComponent(btnApply)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(btnOk)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(btnCancel)))
                                        .addContainerGap())
                );
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(settingsTabbedPane, GroupLayout.PREFERRED_SIZE, 542, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(btnCancel)
                                .addComponent(btnOk)
                                .addComponent(btnApply)
                                .addComponent(reset))
                                .addContainerGap())
                );
        getContentPane().setLayout(layout);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initTabs() {
        // Init Default Tab Panel
        GroupLayout jPanelDefaultLayout = new GroupLayout(jPanelDefault);
        jPanelDefaultLayout.setVerticalGroup(
                jPanelDefaultLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(jPanelDefaultLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelDefaultLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(tableClickCount, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(obsoleteWarning)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(calcConflictWarning)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(debug)
                                .addGap(17)
                                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
        jPanelDefaultLayout.setHorizontalGroup(
                jPanelDefaultLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(Alignment.LEADING, jPanelDefaultLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelDefaultLayout.createParallelGroup(Alignment.LEADING)
                                .addGroup(jPanelDefaultLayout.createSequentialGroup()
                                        .addGroup(jPanelDefaultLayout.createParallelGroup(Alignment.LEADING)
                                                .addComponent(calcConflictWarning)
                                                .addComponent(obsoleteWarning)
                                                .addGroup(jPanelDefaultLayout.createSequentialGroup()
                                                        .addComponent(tableClickCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                        .addComponent(jLabel1))
                                                        .addComponent(debug))
                                                        .addContainerGap(45, Short.MAX_VALUE)))
                                                        .addContainerGap())
                );
        jPanelDefault.setLayout(jPanelDefaultLayout);

        // Init Table Display Tab
        GroupLayout jPanelTableDisplayLayout = new GroupLayout(jPanelTableDisplay);
        jPanelTableDisplayLayout.setHorizontalGroup(
                jPanelTableDisplayLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(jPanelTableDisplayLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelTableDisplayLayout.createParallelGroup(Alignment.TRAILING)
                                .addGroup(jPanelTableDisplayLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(saveDebugTables)
                                        .addComponent(displayHighTables)
                                        .addComponent(valueLimitWarning))
                                        .addGroup(jPanelTableDisplayLayout.createSequentialGroup()
                                                .addGroup(jPanelTableDisplayLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(lblCellHeight)
                                                        .addComponent(lblFont))
                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                        .addGroup(jPanelTableDisplayLayout.createParallelGroup(Alignment.LEADING)
                                                                .addComponent(btnChooseFont)
                                                                .addGroup(jPanelTableDisplayLayout.createSequentialGroup()
                                                                        .addComponent(cellHeight, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(ComponentPlacement.RELATED, 139, Short.MAX_VALUE)
                                                                        .addComponent(lblCellWidth)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(cellWidth, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)))))
                                                                        .addGap(47))
                                                                        .addGroup(Alignment.LEADING, jPanelTableDisplayLayout.createSequentialGroup()
                                                                                .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                                                                                .addContainerGap())
                                                                                .addGroup(Alignment.LEADING, jPanelTableDisplayLayout.createSequentialGroup()
                                                                                        .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                                                                                        .addContainerGap())
                                                                                        .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                );
        jPanelTableDisplayLayout.setVerticalGroup(
                jPanelTableDisplayLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(jPanelTableDisplayLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(saveDebugTables)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(displayHighTables)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(valueLimitWarning)
                        .addGap(27)
                        .addGroup(jPanelTableDisplayLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblCellWidth)
                                .addComponent(cellWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblCellHeight)
                                .addComponent(cellHeight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(jPanelTableDisplayLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(lblFont)
                                        .addComponent(btnChooseFont, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap())
                );
        jPanelTableDisplay.setLayout(jPanelTableDisplayLayout);

        // Init Clipboard Tab Panel
        GroupLayout jPanelClipboardLayout = new GroupLayout(jPanelClipboard);
        jPanelClipboardLayout.setHorizontalGroup(
                jPanelClipboardLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(jPanelClipboardLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelClipboardLayout.createParallelGroup(Alignment.LEADING)
                                .addGroup(jPanelClipboardLayout.createSequentialGroup()
                                        .addGap(17)
                                        .addGroup(jPanelClipboardLayout.createParallelGroup(Alignment.LEADING)
                                                .addComponent(rdbtnAirboys)
                                                .addComponent(rdbtnDefault)
                                                .addComponent(rdbtnCustom))))
                                                .addGap(157))
                );
        jPanelClipboardLayout.setVerticalGroup(
                jPanelClipboardLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(jPanelClipboardLayout.createSequentialGroup()
                        .addContainerGap()
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(rdbtnDefault)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(rdbtnAirboys)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(rdbtnCustom)
                        .addGap(435))
                );
        jPanelClipboard.setLayout(jPanelClipboardLayout);

        // Init Icons Tab panel
        editorIconsPanel.setBorder(new TitledBorder(null, "Editor Toolbar Icons", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        tableIconsPanel.setBorder(new TitledBorder(null, "Table Toolbar Icons", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        GroupLayout jPanelIconsLayout = new GroupLayout(jPanelIcons);
        jPanelIconsLayout.setHorizontalGroup(
                jPanelIconsLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(jPanelIconsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelIconsLayout.createParallelGroup(Alignment.LEADING)
                                .addComponent(editorIconsPanel, GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                                .addComponent(tableIconsPanel, GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE))
                                .addContainerGap())
                );
        jPanelIconsLayout.setVerticalGroup(
                jPanelIconsLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(jPanelIconsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(editorIconsPanel, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(tableIconsPanel, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(367, Short.MAX_VALUE))
                );

        JLabel lblTableIconScale = new JLabel("Scale:");

        textFieldTableIconScale = new JTextField();
        textFieldTableIconScale.setToolTipText("The percentage of the icons original size.");
        textFieldTableIconScale.setColumns(10);

        JLabel labelTableScalePercent = new JLabel("%");
        GroupLayout tableIconsPanelLayout = new GroupLayout(tableIconsPanel);
        tableIconsPanelLayout.setHorizontalGroup(
                tableIconsPanelLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(tableIconsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblTableIconScale)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(textFieldTableIconScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(labelTableScalePercent)
                        .addContainerGap(216, Short.MAX_VALUE))
                );
        tableIconsPanelLayout.setVerticalGroup(
                tableIconsPanelLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(tableIconsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(tableIconsPanelLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblTableIconScale)
                                .addComponent(textFieldTableIconScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(labelTableScalePercent))
                                .addContainerGap(116, Short.MAX_VALUE))
                );
        tableIconsPanel.setLayout(tableIconsPanelLayout);

        JLabel lblEditorIconScale = new JLabel("Scale:");

        textFieldEditorIconScale = new JTextField();
        textFieldEditorIconScale.setToolTipText("The percentage of the icons original size.");
        textFieldEditorIconScale.setColumns(10);

        JLabel labelEditorScalePercent = new JLabel("%");
        GroupLayout editorIconsPanelLayout = new GroupLayout(editorIconsPanel);
        editorIconsPanelLayout.setHorizontalGroup(
                editorIconsPanelLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(editorIconsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblEditorIconScale)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(textFieldEditorIconScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(labelEditorScalePercent)
                        .addContainerGap(216, Short.MAX_VALUE))
                );
        editorIconsPanelLayout.setVerticalGroup(
                editorIconsPanelLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(editorIconsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(editorIconsPanelLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblEditorIconScale)
                                .addComponent(textFieldEditorIconScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(labelEditorScalePercent))
                                .addContainerGap(95, Short.MAX_VALUE))
                );
        editorIconsPanel.setLayout(editorIconsPanelLayout);

        jPanelIcons.setLayout(jPanelIconsLayout);
    }

	
    public void AddLoggerDefFile() {
        JFileChooser fc = new JFileChooser(Settings.RR_LOGGER_REPO);
        fc.setFileFilter(new XMLFilter());

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION && fc.getSelectedFile().getName().toLowerCase().contains(".xml")) {
        	if(settings.getAvailableLoggerDefFiles().containsValue(fc.getSelectedFile()))
        	{
        		comboBoxLoggerDef.setSelectedItem(fc.getSelectedFile().getName());
        	}
        	else
        	{
	        	settings.addAvailableLoggerDefFile(fc.getSelectedFile(),true);
	        	settings.setLoggerDefFilePath(settings.getAvailableLoggerDefFiles().get(fc.getSelectedFile().getName()).getAbsolutePath());//TODO abstract this?
	        	DefaultComboBoxModel cmb  = new DefaultComboBoxModel(settings.getAvailableLoggerDefs().keySet().toArray());
	        	comboBoxLoggerDef.setModel(cmb);
	        	comboBoxLoggerDef.setSelectedItem(fc.getSelectedFile().getAbsolutePath());
        	}
        }
    }
    
    public void saveecuDefinitionList() {
        Vector<File> output = new Vector<File>();

        // create file vector
        for (int i = 0; i < ecuDefFileNames.size(); i++) {
            output.add(new File(ecuDefFileNames.get(i)));
        }

        // save
        //parent.getSettings().
        settings.setEcuDefinitionFiles(output);
    }

    public void addEcuDefFile() {
        JFileChooser fc = new JFileChooser(Settings.RRECUDEFREPO);
        fc.setFileFilter(new XMLFilter());

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            ecuDefFileNames.add(fc.getSelectedFile().getAbsolutePath());
            updateEcuDefListModel();
        }
    }

    public void moveSelection(int direction) {
        int selectedIndex = ecuDefinitionList.getSelectedIndex();
        String fileName = ecuDefFileNames.get(selectedIndex);

        if (direction == MOVE_UP && selectedIndex > 0) {
            ecuDefFileNames.remove(selectedIndex);
            ecuDefFileNames.add(--selectedIndex, fileName);

        } else if (direction == MOVE_DOWN && selectedIndex < ecuDefinitionList.getModel().getSize()) {
            ecuDefFileNames.remove(selectedIndex);
            ecuDefFileNames.add(++selectedIndex, fileName);

        }
        updateEcuDefListModel();
        ecuDefinitionList.setSelectedIndex(selectedIndex);
    }

    public void removeSelection() {
        int index = ecuDefinitionList.getSelectedIndex();
        if (index < 0) return;
        ecuDefFileNames.remove(index);
        updateEcuDefListModel();

    }

    public void updateEcuDefListModel() {
        ecuDefinitionList.setListData(ecuDefFileNames);
    }
    
    public void addGitRemoteAndFetch()
    {
    	if(settings.getGitRemotes().containsKey(this.textFieldRemoteName.getText()))
    	{
    		showMessageDialog(this,
                    "A remote named " + this.textFieldRemoteName.getText() + " already exists!",
                    "Git Config", INFORMATION_MESSAGE);
    	}
    	else if(settings.getGitRemotes().containsValue(this.textFieldGitRepo.getText()))
    	{
    		showMessageDialog(this,
                    "A remote with URL " + this.textFieldGitRepo.getText() + " already exists!",
                    "Git Config", INFORMATION_MESSAGE);
    	}
    	else
    	{
	    	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    	if(this.textFieldGitRepo.getText() != null && this.textFieldRemoteName.getText() != null)
	    		ECUExec.definitionRepoManager.AddRemote(this.textFieldRemoteName.getText(),this.textFieldGitRepo.getText());;
	    	setCursor(null);
    	}
    }
    
    public void updateComboBoxGitBranch(){
		DefaultComboBoxModel dcmb = new DefaultComboBoxModel(ECUExec.definitionRepoManager.getAvailableBranches());
		this.comboBoxGitBranch.setModel(dcmb);
		this.comboBoxGitBranch.setSelectedItem(settings.getGitBranch());
	}
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == maxColor) {
            Color color = JColorChooser.showDialog(this.getContentPane(),
            		
                    "Background Color", settings.getMaxColor());
            if (color != null) {
                maxColor.setBackground(color);
            }
        } else if (e.getSource() == minColor) {
            Color color = JColorChooser.showDialog(this.getContentPane(),
                    "Background Color", settings.getMinColor());
            if (color != null) {
                minColor.setBackground(color);
            }
        } else if (e.getSource() == highlightColor) {
            Color color = JColorChooser.showDialog(this.getContentPane(),
                    "Background Color", settings.getHighlightColor());
            if (color != null) {
                highlightColor.setBackground(color);
            }
        } else if (e.getSource() == axisColor) {
            Color color = JColorChooser.showDialog(this.getContentPane(),
                    "Background Color", settings.getAxisColor());
            if (color != null) {
                axisColor.setBackground(color);
            }
        } else if (e.getSource() == increaseColor) {
            Color color = JColorChooser.showDialog(this.getContentPane(),
                    "Background Color", settings.getIncreaseBorder());
            if (color != null) {
                increaseColor.setBackground(color);
            }
        } else if (e.getSource() == decreaseColor) {
            Color color = JColorChooser.showDialog(this.getContentPane(),
                    "Background Color", settings.getDecreaseBorder());
            if (color != null) {
                decreaseColor.setBackground(color);
            }
        } else if (e.getSource() == warningColor) {
            Color color = JColorChooser.showDialog(this.getContentPane(),
                    "Warning Color", settings.getWarningColor());
            if (color != null) {
                warningColor.setBackground(color);
            }
        } else if (e.getSource() == btnApply) {
            applySettings();
        } else if (e.getSource() == btnOk) {
            // Apply settings to Settings object.
            applySettings();
            // Write settings to file.
            saveSettings();
            this.dispose();
        } else if (e.getSource() == btnCancel) {
            this.dispose();
        } else if (e.getSource() == btnChooseFont) {
            JFontChooser fc = new JFontChooser(this);
            fc.setLocationRelativeTo(this);
            if (fc.showDialog(settings.getTableFont()) == JFontChooser.OK_OPTION) {
                btnChooseFont.setFont(fc.getFont());
                btnChooseFont.setText(fc.getFont().getFontName());
            }
        } else if (e.getSource() == reset) {
            settings = new Settings();
            initSettings();
        } else if (e.getSource() == btnAddAssocs) {
            // add file associations for selected file types
            try {
                if (extensionHex.isSelected()) {
                    FileAssociator.addAssociation("HEX", new File(".").getCanonicalPath() + separator + PRODUCT_NAME + ".exe", "ECU Image");
                }

                if (extensionBin.isSelected()) {
                    FileAssociator.addAssociation("BIN", new File(".").getCanonicalPath() + separator + PRODUCT_NAME + ".exe", "ECU Image");
                }
            } catch (Exception ex) {
            }

        } else if (e.getSource() == btnRemoveAssocs) {
            // remove file associations for selected file types
            if (extensionHex.isSelected()) {
                FileAssociator.removeAssociation("HEX");
            }

            if (extensionBin.isSelected()) {
                FileAssociator.removeAssociation("BIN");
            }

        } else if (e.getSource() == this.btnLoggerDefChooseFile){
        	AddLoggerDefFile();  
        } else if (e.getSource() == btnFetchResetGit){
        	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    		ECUExec.definitionRepoManager.CheckoutBranch(comboBoxGitBranch.getSelectedItem().toString());
    		setCursor(null);
        } else if (e.getSource() == btnAddRemote){
        	addGitRemoteAndFetch();
        	updateComboBoxGitBranch();
        }
    }

    public void applySettings() {
        try {
            Integer.parseInt(cellHeight.getText());
        } catch (NumberFormatException ex) {
            // number formatted imporperly, reset
            cellHeight.setText((int) (settings.getCellSize().getHeight()) + "");
        }
        try {
            Integer.parseInt(cellWidth.getText());
        } catch (NumberFormatException ex) {
            // number formatted imporperly, reset
            cellWidth.setText((int) (settings.getCellSize().getWidth()) + "");
        }

        settings.setObsoleteWarning(obsoleteWarning.isSelected());
        settings.setCalcConflictWarning(calcConflictWarning.isSelected());
        settings.setDisplayHighTables(displayHighTables.isSelected());
        settings.setSaveDebugTables(saveDebugTables.isSelected());
        settings.setDebug(debug.isSelected());

        settings.setMaxColor(maxColor.getBackground());
        settings.setMinColor(minColor.getBackground());
        settings.setHighlightColor(highlightColor.getBackground());
        settings.setAxisColor(axisColor.getBackground());
        settings.setIncreaseBorder(increaseColor.getBackground());
        settings.setDecreaseBorder(decreaseColor.getBackground());

        settings.setCellSize(new Dimension(Integer.parseInt(cellWidth.getText()),
                Integer.parseInt(cellHeight.getText())));

        settings.setTableFont(btnChooseFont.getFont());

        if (tableClickCount.getSelectedIndex() == 0) { // single click opens table
            settings.setTableClickCount(1);
        } else { // double click opens table
            settings.setTableClickCount(2);
        }

        settings.setValueLimitWarning(valueLimitWarning.isSelected());
        settings.setWarningColor(warningColor.getBackground());

        if(rdbtnAirboys.isSelected())
        {
            settings.setAirboysFormat();
        } else if(rdbtnCustom.isSelected()) {
            settings.setTableClipboardFormat(Settings.CUSTOM_CLIPBOARD_FORMAT);
            // Table Header settings need to be manually edited in the settings.xml file;
        } else {
            settings.setDefaultFormat();
        }

        try{
            settings.setEditorIconScale(Integer.parseInt(textFieldEditorIconScale.getText()));
            if(parentEditor != null)
            	parentEditor.getToolBar().updateIcons();
        } catch(NumberFormatException ex) {
            // Number formatted incorrectly reset.
            textFieldEditorIconScale.setText(String.valueOf(settings.getEditorIconScale()));
        }

        try{
            settings.setTableIconScale(Integer.parseInt(textFieldTableIconScale.getText()));
            if(parentEditor != null)
            	parentEditor.getTableToolBar().updateIcons();
        } catch(NumberFormatException ex) {
            // Number formatted incorrectly reset.
            textFieldTableIconScale.setText(String.valueOf(settings.getTableIconScale()));
        }

        Vector<File> output = new Vector<File>();
        for (int i = 0; i < ecuDefFileNames.size(); i++) {
            output.add(new File(ecuDefFileNames.get(i)));
        }
        settings.setEcuDefinitionFiles(output);
        int index = 0;
        String s = comboBoxLoggerDef.getSelectedItem().toString();
        File f = settings.getAvailableLoggerDefs().get(s);
        settings.setLoggerDefFilePath(f.getAbsolutePath());
        
        if(parentLogger != null)
        {
	        try{
	            parentLogger.loadLoggerParams();
	        } catch (Exception e) {
	        	parentLogger.reportError(e);
	    	}
        }
    }

    public void saveSettings()
    {
        ECUExec.settingsManager.save(settings);
    }
    
    

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel axisColor;
    private javax.swing.JButton btnAddAssocs;
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnChooseFont;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnRemoveAssocs;
    private javax.swing.JCheckBox calcConflictWarning;
    private javax.swing.JTextField cellHeight;
    private javax.swing.JTextField cellWidth;
    private javax.swing.JCheckBox debug;
    private javax.swing.JLabel decreaseColor;
    private javax.swing.JCheckBox displayHighTables;
    private javax.swing.JCheckBox extensionBin;
    private javax.swing.JCheckBox extensionHex;
    private javax.swing.JLabel highlightColor;
    private javax.swing.JLabel increaseColor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTabbedPane settingsTabbedPane;
    private javax.swing.JPanel jPanelDefault;
    private javax.swing.JPanel jPanelClipboard;
    private javax.swing.JPanel jPanelTableDisplay;
    private javax.swing.JPanel jPanelIcons;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lblAxis;
    private javax.swing.JLabel lblCellHeight;
    private javax.swing.JLabel lblCellWidth;
    private javax.swing.JLabel lblDecrease;
    private javax.swing.JLabel lblFont;
    private javax.swing.JLabel lblHighlight;
    private javax.swing.JLabel lblIncrease;
    private javax.swing.JLabel lblMax;
    private javax.swing.JLabel lblMin;
    private javax.swing.JLabel lblWarning;
    private javax.swing.JLabel maxColor;
    private javax.swing.JLabel minColor;
    private javax.swing.JCheckBox obsoleteWarning;
    private javax.swing.JButton reset;
    private javax.swing.JCheckBox saveDebugTables;
    private javax.swing.JComboBox tableClickCount;
    private javax.swing.JCheckBox valueLimitWarning;
    private javax.swing.JLabel warningColor;
    private ButtonGroup clipboardButtonGroup;
    private javax.swing.JRadioButton rdbtnDefault;
    private javax.swing.JRadioButton rdbtnAirboys;
    private javax.swing.JRadioButton rdbtnCustom;
    private JPanel editorIconsPanel;
    private JPanel tableIconsPanel;
    private JTextField textFieldTableIconScale;
    private JTextField textFieldEditorIconScale;
    private JTextField textFieldGitRepo;
    private JButton buttonEcuDefMoveUp;
    private JButton buttonEcuDefAdd;
    private JButton buttonEcuDefRemove;
    private JButton buttonEcuDefMoveDown;
    private JList ecuDefinitionList;
    private JPanel panel;
    private JLabel lblFilepath;
    private JComboBox comboBoxLoggerDef;
    private JComboBox comboBoxGitBranch;
    private JButton btnAddRemote;
    private JButton btnFetchResetGit;
    private JButton btnLoggerDefChooseFile;
    private JLabel lblRemoteName;
    private JTextField textFieldRemoteName;
}