package controller;

import model.FrequencyGenerator;

import gui.MainFrame;

public class Controller {

    private MainFrame mainFrame = new MainFrame();
    private FrequencyGenerator chirp = new FrequencyGenerator();
    
    public Controller()
    {
        mainFrame.SetController(this);
        
        // TODO test code
        System.out.println("Controller hard coded test code...");
        chirp.SetFrequency(100);
    }
    
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
    
    public int GetFrequnecy()
    {
        return chirp.GetFrequency();
    }
}