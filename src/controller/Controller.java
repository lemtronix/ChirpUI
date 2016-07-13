package controller;

import model.FrequencyGenerator;

import gui.MainFrame;

public class Controller {

    private MainFrame mainFrame;
    private FrequencyGenerator chirp;
    
    public Controller()
    {
        mainFrame = new MainFrame();
        chirp = new FrequencyGenerator();
        
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