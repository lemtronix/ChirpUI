package controller;

import java.util.HashSet;

import model.FrequencyGenerator;
import gui.MainFrame;

public class Controller {

    private MainFrame mainFrame;
    private FrequencyGenerator chirp;
    
    public Controller()
    {
    	chirp = new FrequencyGenerator();
    	
        mainFrame = new MainFrame();
        mainFrame.SetController(this);
    }
    
    // TODO will this ever be used?  Helpful for sending messages back to the GUI from the frequency generator
    public void RegisterObserver(FrequencyGeneratorListener listener)
    {
        if (listener != null)
        {
            chirp.AddListener(listener);
        }
    }
    
    public HashSet<String> GetAvailableSerialPorts()
    {
    	return chirp.getAvailableSerialPorts();
    }
    
    public void SetSerialPort(String ComPortName)
    {
    	chirp.setSerialPort(ComPortName);
    }
    
    public void SetFrequency(int Frequency)
    {
        chirp.SetFrequency(Frequency);
    }
    
    public void SetVoltage(int Voltage)
    {
        chirp.SetVoltage(Voltage);
    }
    
    public void SetWaveform(WaveformType Waveform)
    {
        chirp.SetWaveform(Waveform);
    }
    
    public int GetFrequnecy()
    {
        return chirp.GetFrequency();
    }
}
