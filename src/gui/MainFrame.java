package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import controller.Controller;
import controller.EventType;
import controller.FrequencyGeneratorListener;
import controller.GeneratorEvent;

public class MainFrame extends JFrame implements FrequencyGeneratorListener {

    private Controller controller;
    
    private CommsPanel commsPanel;
    private WaveformPanel waveformPanel;
    private StatusPanel statusPanel;
    
    public MainFrame()
    {
        super("Chirp - Waveform Generator");
        
        setLayout(new BorderLayout());
        
        // Waveform Panel
        // Values Panel
        // Comms Panel
        
        commsPanel = new CommsPanel();
        waveformPanel = new WaveformPanel();
        statusPanel = new StatusPanel();
        
        add(commsPanel, BorderLayout.SOUTH);
        add(waveformPanel, BorderLayout.CENTER);
        
        setMinimumSize(new Dimension(300, 300));
        setSize(300, 300);

        setVisible(true);
    }
    
    public void SetController(Controller controller)
    {
        if (controller != null)
        {
            this.controller = controller;
            
            // Tell the controller we're interested in events
            controller.RegisterObserver(this);
        }
    }
    
    @Override
    public void FrequencyGeneratorEventOccurred(GeneratorEvent g)
    {
        if (g.GetEventTypeThatOccurred() == EventType.FrequencyEvent)
        {           
            System.out.println("View sees that the frequency value is: " + controller.GetFrequnecy());
        }
        else
        {
            System.out.println("View sees some other event...");
        }
    }
}
