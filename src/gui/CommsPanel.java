/*
 * CommsPanel is responsible for setting the communications port
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.Controller;

public class CommsPanel extends JPanel implements Controllable {

    private Controller controller;
    private JComboBox<String> commComboBox;
    private JLabel commPortText;
    private JButton autoDetectButton;
    
    public CommsPanel()
    {
        commPortText = new JLabel("Com Port:");
        autoDetectButton = new JButton("Auto Detect");
        
        autoDetectButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // TODO replace with auto detection implementation
                System.out.println("Auto detection clicked");
            }
        });
        
        // TODO Auto detection of Com ports
        commComboBox = new JComboBox<String>();
                
        add(commPortText);
        add(commComboBox);
        add(autoDetectButton);
    }

    @Override
    public void SetController(Controller controller)
    {
        if (controller != null)
        {
            this.controller = controller;
            
            // Add all of the available COM ports to the drop down
            HashSet<String> comNameHashSet = controller.GetAvailableSerialPorts();
            
            DefaultComboBoxModel<String> commModel = new DefaultComboBoxModel<String>();
            
            if (comNameHashSet != null)
            {
            	for (String comName : comNameHashSet) 
            	{
            		commModel.addElement(comName);
            	}
            }
            
            commComboBox.setModel(commModel);
            commComboBox.setSelectedIndex(-1);
            
            commComboBox.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                	String comPortName = commComboBox.getSelectedItem().toString();
                    System.out.println(comPortName + " selected");
                    controller.SetSerialPort(comPortName);
                }
            });
        }
    }
}
