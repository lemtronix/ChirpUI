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
    
    public void GetStatus()
    {
        chirp.GetStatus();
    }
    
    public void Close()
    {
        chirp.Close();
    }
}
