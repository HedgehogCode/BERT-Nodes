/*
 * Copyright (c) 2022 Redfield AB.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, Version 3, as
 * published by the Free Software Foundation.
 *  
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 */

package se.redfield.bert.nodes.zstc;

import javax.swing.JLabel;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.StringValue;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.util.ColumnSelectionPanel;
import org.knime.dl.base.nodes.AbstractGridBagDialogComponentGroup;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.port.PortObjectSpec;

import se.redfield.bert.setting.ZeroShotTextClassifierSettings;
import se.redfield.bert.setting.ui.PythonNodeDialog;


/**
 * Implementation of the node dialog of {@link ZeroShotTextClassifierNodeModel} node.
 * 
 * @author Abderrahim Alakouche.
 */

public class ZeroShotTextClassifierNodeDialog extends PythonNodeDialog<ZeroShotTextClassifierSettings> {

	private DialogComponentColumnNameSelection sentenceColumn;
	
	@SuppressWarnings("unchecked")
    protected ZeroShotTextClassifierNodeDialog() {
        super(new ZeroShotTextClassifierSettings());
        
        sentenceColumn = new DialogComponentColumnNameSelection(settings.getSentenceColumnModel(), 
        														"Sentence column", 
        														ZeroShotTextClassifierNodeModel.PORT_DATA_TABLE,
        														StringValue.class);
        
        addTab("Settings", new SettingsTabGroup().getComponentGroupPanel());
        addTab("Multi-label", new AdvancedTabGroup().getComponentGroupPanel());
        addPythonTab();
  
    }
	
	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings, PortObjectSpec [] specs) throws NotConfigurableException{
		super.loadSettingsFrom(settings, specs);
		try {
			this.settings.validate((DataTableSpec) specs[ZeroShotTextClassifierNodeModel.PORT_DATA_TABLE]);
		} catch (InvalidSettingsException e) {
			// ignore
		}
		
		sentenceColumn.loadSettingsFrom(settings, specs);
		
	}
	
	
	
	private class SettingsTabGroup extends AbstractGridBagDialogComponentGroup {
		
		public SettingsTabGroup() {
			
			addDoubleColumnRow(new JLabel("Sentence column"), getFirstComponent(sentenceColumn, ColumnSelectionPanel.class));
			addStringEditRowComponent(settings.getCandidateLabelsModel(), "Candidate labels");
			
			addHorizontalSeparator();
			
			addCheckboxRow(settings.getUseCustomHypothesisModel(),"Use custom hypothesis", false);
			addStringEditRowComponent(settings.getHypothesisModel(), "Hypothesis");
			
			addCheckboxRow(settings.getAppendProbabilitiesModel(), "Append individual class probabilities", false);
				
		}		
	}
	
	private class AdvancedTabGroup extends AbstractGridBagDialogComponentGroup{
		
		public AdvancedTabGroup() {
			addCheckboxRow(settings.getMultilabelClassification(), "Multi-label classification", false);
			addCheckboxRow(settings.getUseCustomThreshouldModel(), "Use custom threshold for determining predictions", false);
			addNumberSpinnerRowComponent(settings.getPredictionThresholdModel(), "Probability threshold", 0.01);
		}
	}	
}

