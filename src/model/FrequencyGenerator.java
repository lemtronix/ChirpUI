package model;

import controller.EventType;
import controller.GeneratorEvent;
import controller.Observable;
import controller.Observer;
import controller.WaveformType;
import gnu.io.CommPort;
import gnu.io.SerialPort;
import gnu.io.CommPortIdentifier;

public class FrequencyGenerator implements Observable {

    // @todo Has a serial port
    private int Frequency;
    private float Voltage;
    private WaveformType Waveform;
    private Boolean IsEnabled;
    private Observer SingleObserver;
    
    // Has Listeners 
    // Send Command
    // Receive Command
    
    public void SetFrequency(int NewFrequency)
    {
        System.out.println("New frequency is: " + NewFrequency);
        
        // Sends the frequency set command on the serial port
        
        // Reads the serial port to ensure the value has been changed

        // Updates its frequency value in the object
        this.Frequency = NewFrequency;
        
        // Sends an event to any subscribed listeners
        SingleObserver.EventOccurred(new GeneratorEvent(EventType.FrequencyEvent));
    }

    @Override
    public void SetObserver(Observer Observer) 
    {
        System.out.println("Attempting to register observer...");
        
        if (Observer != null)
        {
            System.out.println("Observer registered");
            
            this.SingleObserver = Observer;
        }
    }

    public int GetFrequency()
    {
        return Frequency;
    }
}
