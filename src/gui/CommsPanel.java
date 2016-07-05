/*
 * CommsPanel is responsible for setting the communications port
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CommsPanel extends JPanel {

	private JComboBox<String> commComboBox;
	private JLabel commPortText = new JLabel("Comm Port:");
	
	public CommsPanel()
	{
		commComboBox = new JComboBox<String>();
		
		DefaultComboBoxModel<String> commModel = new DefaultComboBoxModel<String>();
		commModel.addElement("Com1");
		commModel.addElement("Com2");
		commModel.addElement("Com3");
		commModel.addElement("Com4");
		
		commComboBox.setModel(commModel);
		commComboBox.setSelectedIndex(0);
		
		add(commPortText);
		add(commComboBox);
	}

}
