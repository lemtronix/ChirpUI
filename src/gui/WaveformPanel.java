package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class WaveformPanel extends JPanel {
    private static final int FREQ_MIN = 0;
    private static final int FREQ_MAX = 8000000;
    private static final int FREQ_INIT = 0;
    private static final int FREQ_SCALE = 1;
    
    private static final int AMP_MIN = 0;
    private static final int AMP_MAX = 50;
    private static final int AMP_INIT = 0;
    private static final int AMP_SCALE = 10;
            
    private JSlider frequencySlider;
    private JSlider amplitudeSlider;
    private JTextField frequencyField;
    private JTextField amplitudeField;
    
    // Radio buttons for field
    // JSeparator?
    public WaveformPanel()
    {
        // Frequency Slider
        frequencyField = new JTextField(10);
        
        frequencySlider = new JSlider(JSlider.HORIZONTAL, FREQ_MIN, FREQ_MAX, FREQ_INIT);
        frequencySlider.setMajorTickSpacing(1000000);
        frequencySlider.setMinorTickSpacing(100000);
        frequencySlider.setPaintTicks(true);
        frequencySlider.setPaintLabels(false);
        // @todo bounded range model?
        
        // Should update the text box, 
        frequencySlider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                if (frequencySlider.getValueIsAdjusting() == false)
                {
                    int SliderValue = frequencySlider.getValue();
                    String SliderString = Integer.toString(SliderValue);
                    
                    frequencyField.setText(SliderString);
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
                int amplitudeValue = Integer.parseInt(amplitudeField.getText());
                
                // Set the slider to the proper value
                if (amplitudeValue >= 0 && amplitudeValue <= 50)
                {
                    amplitudeSlider.setValue(amplitudeValue);
                }
            }
        });
        
        amplitudeSlider = new JSlider(JSlider.HORIZONTAL, AMP_MIN, AMP_MAX, AMP_INIT);
        amplitudeSlider.setMajorTickSpacing(10);
        amplitudeSlider.setMinorTickSpacing(1);
        amplitudeSlider.setPaintTicks(true);
        amplitudeSlider.setPaintLabels(false);
        // @todo bounded range model?
        
        amplitudeSlider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                if (amplitudeSlider.getValueIsAdjusting() == false)
                {
                    int SliderValue = amplitudeSlider.getValue();
                    String SliderString = Integer.toString(SliderValue);
                    
                    amplitudeField.setText(SliderString);
                }
            }
        });
        
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
        gc.weighty = 0.1;
        
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

    }
}
