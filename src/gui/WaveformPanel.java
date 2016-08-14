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
*/
package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.Controller;
import controller.WaveformType;

public class WaveformPanel extends JPanel implements Controllable {
    
    private Controller controller;
    
    private static final int FREQ_MIN = 0;
    private static final int FREQ_MAX = 8000000;
    private static final int FREQ_INIT = 0;
    private static final int FREQ_SCALE = 1;
    
    private static final int AMP_MIN = 0;
    private static final int AMP_MAX = 4000;
    private static final int AMP_INIT = 0;
    private static final int AMP_SCALE = 1000;
            
    private JSlider frequencySlider;
    private JSlider amplitudeSlider;
    private JTextField frequencyField;
    private JTextField amplitudeField;
    
    private ButtonGroup waveformButtonGroup;
    private JRadioButton offButton;
    private JRadioButton sineWaveButton;
    private JRadioButton triangleWaveButton;
    private JRadioButton squareWaveButton;
    
    private static final String offString = "Off";
    private static final String sineWaveString = "Sine";
    private static final String triangleWaveString = "Triangle";
    private static final String squareWaveString = "Square";
    //private static final String squareWaveDiv2String = "Square";
    
    // Radio buttons for field
    // JSeparator?
    public WaveformPanel()
    {        
        // Frequency Slider
        frequencyField = new JTextField(10);
        frequencyField.setText("0");
        frequencyField.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int frequencyValue = Integer.parseInt(frequencyField.getText());
                
                // Set the slider to the proper value
                if (frequencyValue >= FREQ_MIN && frequencyValue <= FREQ_MAX)
                {
                    frequencySlider.setValue(frequencyValue);
                }
            }
        });
        
        frequencySlider = new JSlider(JSlider.HORIZONTAL, FREQ_MIN, FREQ_MAX, FREQ_INIT);
        frequencySlider.setMajorTickSpacing(1000000);
        frequencySlider.setMinorTickSpacing(100000);
        frequencySlider.setPaintTicks(true);
        frequencySlider.setPaintLabels(false);
        // TODO bounded range model?
        
        // Should update the text box, 
        frequencySlider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                // Update the text box while the slider is moving
                int SliderValue = frequencySlider.getValue();
                String SliderString = Integer.toString(SliderValue);
                
                frequencyField.setText(SliderString);
                
                // But only send the command when no longer adjusting
                if (frequencySlider.getValueIsAdjusting() == false)
                {
                    int frequencyValue = frequencySlider.getValue();
                    
                    // TODO debug
                    // System.out.println("Sending frequency command value: " + frequencyValue);
                    
                    SendFrequency(frequencyValue);
                }
            }
        });
        
        // Amplitude Slider
        amplitudeField = new JTextField(10);
        amplitudeField.setText("0");
        amplitudeField.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                float amplitudeValueFloat = 0.0f;
                
                try
                {
                    amplitudeValueFloat = Float.parseFloat(amplitudeField.getText());
                }
                catch (NumberFormatException nfe)
                {
                    // If an invalid value was entered, set the output to 0
                    amplitudeValueFloat = 0.0f;
                }
                
                // Set the slider to the proper value
                if (amplitudeValueFloat >= (float)AMP_MIN && amplitudeValueFloat <= (float)AMP_MAX)
                {
                    // Convert the float back to an int and set the value
                    int amplitudeValue = (int)(amplitudeValueFloat*AMP_SCALE);
                    amplitudeSlider.setValue(amplitudeValue);
                }
            }
        });
        
        amplitudeSlider = new JSlider(JSlider.HORIZONTAL, AMP_MIN, AMP_MAX, AMP_INIT);
        amplitudeSlider.setMajorTickSpacing(AMP_SCALE);
        amplitudeSlider.setMinorTickSpacing(AMP_SCALE/10);
        amplitudeSlider.setPaintTicks(true);
        amplitudeSlider.setPaintLabels(false);
        // TODO bounded range model?
        
        amplitudeSlider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                // Update the text box while the slider is moving
                int SliderValue = amplitudeSlider.getValue();
                
                // Convert the slider value to float for the user
                float SliderValueFloat = ((float)SliderValue/AMP_SCALE);
                String SliderString = Float.toString(SliderValueFloat);
                
                amplitudeField.setText(SliderString);
                
                // But only send the command when no longer adjusting
                if (amplitudeSlider.getValueIsAdjusting() == false)
                {
                    int amplitudeValueMV = amplitudeSlider.getValue();
                    SendAmplitude(amplitudeValueMV);
                }
            }
        });
        
        // Waveform buttons
        offButton = new JRadioButton(offString);
        offButton.setSelected(true);
        offButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
//                System.out.println("Off selected.");
                controller.SetWaveform(WaveformType.Off);
                
            }
        });
        
        sineWaveButton = new JRadioButton(sineWaveString);
        sineWaveButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //System.out.println("Sine wave selected.");
                controller.SetWaveform(WaveformType.Sine);
                
            }
        });
        
        triangleWaveButton = new JRadioButton(triangleWaveString);
        triangleWaveButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
//                System.out.println("Triangle wave selected.");
                controller.SetWaveform(WaveformType.Triangle);
                
            }
        });
        
        squareWaveButton = new JRadioButton(squareWaveString);
        squareWaveButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
//                System.out.println("Square wave selected.");
                controller.SetWaveform(WaveformType.Square);
                
            }
        });
        
        waveformButtonGroup = new ButtonGroup();
        waveformButtonGroup.add(offButton);
        waveformButtonGroup.add(sineWaveButton);
        waveformButtonGroup.add(triangleWaveButton);
        waveformButtonGroup.add(squareWaveButton);
        
        layoutComponents();
        
    }
    
    public void layoutComponents()
    {
        //setPreferredSize(new Dimension(250, 10));

        // Use grid-bag layout
        setLayout(new GridBagLayout());
        
        // Setup the constraints; x is left to right, y is top to bottom; Do not
        // resize the components
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.CENTER;
        
        // weight determines how bunched objects should be together; higher
        // number = more breathing room between components.
        gc.weightx = 1;
        gc.weighty = 1;
        
        /// FIRST ROW ///
        gc.gridy = 0;
        gc.fill = GridBagConstraints.NONE;
        
        gc.gridx = 0;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("Frequency: "), gc);
        
        gc.gridx++;
        gc.anchor = GridBagConstraints.LINE_START;
        add(frequencyField, gc);
        
        /// NEXT ROW ///
        gc.gridy++;
        gc.fill = GridBagConstraints.HORIZONTAL;
        
        gc.gridx = 0;
        gc.gridwidth = 2;
        
        add(frequencySlider, gc);
        
        /// NEXT ROW ///
        gc.gridy++;
        gc.fill = GridBagConstraints.NONE;
        
        gc.gridx = 0;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("Amplitude: "), gc);
        
        gc.gridx++;
        gc.anchor = GridBagConstraints.LINE_START;
        add(amplitudeField, gc);
        
        /// NEXT ROW ///
        gc.gridy++;
        gc.fill = GridBagConstraints.HORIZONTAL;
        
        gc.gridx = 0;
        gc.gridwidth = 2;
        add(amplitudeSlider, gc);

        /// NEXT ROW ///
        gc.gridy++;
        gc.fill = GridBagConstraints.HORIZONTAL;
        
        gc.gridx = 0;
        gc.gridwidth = 2;
        add(new JSeparator(JSeparator.HORIZONTAL), gc);
        
        /// NEXT ROW ///
        gc.gridy++;
        gc.weighty = 0.1;
        gc.fill = GridBagConstraints.NONE;
        
        gc.gridx = 0;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("Waveform:"), gc);
        
        gc.gridx++;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.insets = new Insets(0, 0, 5, 0);
        add(offButton, gc);
        
        /// NEXT ROW ///
        gc.gridy++;
        
        gc.gridx = 1;
        add(sineWaveButton, gc);
        
        /// NEXT ROW ///
        gc.gridy++;
        
        gc.gridx = 1;
        add(triangleWaveButton, gc);
        
        /// NEXT ROW ///
        gc.gridy++;
        
        gc.gridx = 1;
        add(squareWaveButton, gc);
        
        /// EMPTY ROW ///
        gc.gridy++;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.weightx = 1;
        
        gc.weighty = 1;
        gc.gridwidth = 2;
        add(new JSeparator(JSeparator.HORIZONTAL), gc);
    }

    @Override
    public void SetController(Controller controller)
    {
        if (controller != null)
        {
            this.controller = controller;
        }
    }
    
    private void SendFrequency(int newFrequency)
    {
        controller.SetFrequency(newFrequency);
    }
    
    private void SendAmplitude(int newAmplitude)
    {
        controller.SetVoltage(newAmplitude);
    }
}
