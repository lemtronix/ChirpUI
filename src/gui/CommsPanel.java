/*
    This file is part of Chirp.

    Chirp is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    Chirp is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Chirp.  If not, see <http://www.gnu.org/licenses/>.
    
    Copyright 2016 Mike Lemberger
*/

package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

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
        
//        autoDetectButton.addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(ActionEvent e)
//            {
//                // TODO replace with auto detection implementation
//                System.out.println("Auto detection clicked");
//            }
//        });
        
        commComboBox = new JComboBox<String>();
                
        add(commPortText);
        add(commComboBox);
//        add(autoDetectButton);
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
