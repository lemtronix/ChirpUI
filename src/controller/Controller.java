package controller;

import model.FrequencyGenerator;

import gui.MainFrame;

public class Controller {

	private MainFrame MainPanel = new MainFrame();
	private FrequencyGenerator Chirp = new FrequencyGenerator();
	
	public Controller()
	{
		MainPanel.SetController(this);
		
		// @todo test code
		System.out.println("Controller hard coded test code...");
		Chirp.SetFrequency(100);
	}
	
	public void RegisterObserver(Observer observer)
	{
		if (observer != null)
		{
			Chirp.SetObserver(observer);
		}
	}
	
	public void SetFrequency(int Frequency)
	{
		Chirp.SetFrequency(Frequency);
	}
	
	public int GetFrequnecy()
	{
		return Chirp.GetFrequency();
	}
}