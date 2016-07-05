package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import controller.Controller;
import controller.EventType;
import controller.GeneratorEvent;
import controller.Observer;

public class MainFrame extends JFrame implements Observer {

	private Controller controller;
	
	private CommsPanel commsPanel;
	private WaveformPanel waveformPanel;
	private StatusPanel statusPanel;
	
	public MainFrame()
	{
		super("Chirp");
		
		setLayout(new BorderLayout());
		
		commsPanel = new CommsPanel();
		waveformPanel = new WaveformPanel();
		statusPanel = new StatusPanel();
		
		add(commsPanel, BorderLayout.WEST);
		
		setMinimumSize(new Dimension(500, 400));
        setSize(500, 400);

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
	public void EventOccurred(GeneratorEvent g) {
		
		if (g.GetEventTypeThatOccurred() == EventType.FrequencyEvent)
		{			
			System.out.println("View sees that the frequency value is: " + controller.GetFrequnecy());
		}
		else
		{
			System.out.println("View sees some other event...");
		}
	}

	// Waveform Panel
	
	// Values Panel
	
	// Comms Panel
}
