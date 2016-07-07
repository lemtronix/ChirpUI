/*
 * CommsPanel is responsible for setting the communications port
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CommsPanel extends JPanel {

    private JComboBox<String> commComboBox;
    private JLabel commPortText;
    private JButton autoDetectButton;
    
    public CommsPanel()
    {
        commPortText = new JLabel("Comm Port:");
        autoDetectButton = new JButton("Auto Detect");
        
        autoDetectButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // TODO replace with implementation
                System.out.println("Auto detection clicked");
            }
        });
        
        // TODO Auto detection of Com ports
        commComboBox = new JComboBox<String>();
        
        DefaultComboBoxModel<String> commModel = new DefaultComboBoxModel<String>();
        commModel.addElement("Com1");
        commModel.addElement("Com2");
        commModel.addElement("Com3");
        commModel.addElement("Com4");
        
        commComboBox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("Com" + commComboBox.getSelectedIndex() +" selected");
            }
        });
        
        commComboBox.setModel(commModel);
        commComboBox.setSelectedIndex(0);
        
        add(commPortText);
        add(commComboBox);
        add(autoDetectButton);
    }

}
