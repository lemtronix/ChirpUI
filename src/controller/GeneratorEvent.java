package controller;

public class GeneratorEvent {
	private EventType EventTypeThatOccurred;
	
	public GeneratorEvent(EventType EventTypeThatOccurred)
	{
		this.EventTypeThatOccurred = EventTypeThatOccurred;
	}
	
	public EventType GetEventTypeThatOccurred()
	{
		return EventTypeThatOccurred;
	}
}
