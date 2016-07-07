package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

import controller.EventType;
import controller.FrequencyGeneratorListener;
import controller.GeneratorEvent;
import controller.WaveformType;
import gnu.io.CommPort;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.CommPortIdentifier;

public class FrequencyGenerator
{
//    private static final String PORT_NAMES[] = { "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "COM10", "COM11",
//            "COM12", "COM13", "COM14", "COM15", };
    private static final String PORT_NAMES[] = { "COM10" };
    

    // TODO move this into its own class
    private SerialPort serialPort;
    private BufferedReader inputStream; // A BufferedReader which will be fed by a InputStreamReader converting the bytes into characters making the displayed results code page independent
    private OutputStream outputStream; // The output stream to the port
    private static final int TIME_OUT = 2000; // Milliseconds to block while waiting for port open
    private static final int DATA_RATE = 9600; // Default bits per second for COM port.
    private static final int BUFFER_SIZE = 128;
    private byte[] buffer = new byte[BUFFER_SIZE];
    private int numberOfChars = 0;

    private int frequency;
    private float voltage;
    private WaveformType waveform;
    private Boolean isEnabled;
    private FrequencyGeneratorListener singleListener; // TODO is this still used?

    // Has Listeners
    // Send Command
    // Receive Command

    public FrequencyGenerator()
    {
        CommPortIdentifier portId = null;
        Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();

        // First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements())
        {
            CommPortIdentifier currPortId = portEnum.nextElement();

            for (String portName : PORT_NAMES)
            {
                if (currPortId.getName().equals(portName))
                {
                    portId = currPortId;
                    break;
                }
            }
        }

        if (portId == null)
        {
            System.out.println("Could not find COM port.");
            return;
        }

        try
        {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            // open the streams
            inputStream = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            outputStream = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(new SerialPortEventListener()
            {
                
                @Override
                public void serialEvent(SerialPortEvent oEvent)
                {
                    if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE)
                    {
                        try 
                        {
                            int charReceived = 0;

                            if ((charReceived = inputStream.read()) > -1)
                            {
                                if (charReceived == '\n' || charReceived == '\r' || numberOfChars >= BUFFER_SIZE)
                                {
                                    // Finish the sequence with a \N to indicate a new line
                                    System.out.println("\\N");
                                    
                                    // Newline character received or buffer at limit then exit
                                    System.out.println(new String(buffer, 0, numberOfChars));
                                    numberOfChars = 0;
                                }
                                else
                                {
                                    // TODO make this debug only
                                    // Print a dot for every character received
                                    System.out.print(".");
                                    
                                    // Store the value in the buffer
                                    buffer[numberOfChars] = (byte) charReceived;
                                    numberOfChars++;
                                }
                            }

//                            String inputLine = inputStream.readLine();
//                            System.out.println(inputLine);
                        }
                        catch (Exception e)
                        {
                            System.err.println(e.toString());
                        }
                    }
                }
            });

            serialPort.notifyOnDataAvailable(true);
        }
        catch (Exception e)
        {
            System.err.println("Unable to open serial port...");
            System.err.println(e.toString());
        }
    }

    public void SetFrequency(int newFrequency)
    {
        System.out.println("New frequency is: " + newFrequency);
        
        String setFrequencyCommand = new String("F" + newFrequency + "\n\r");
        
        SendCommand(setFrequencyCommand);
        
        // TODO Reads the serial port to ensure the value has been changed? 

        // Updates its frequency value in the object
        this.frequency = newFrequency;

        // Sends an event to any subscribed listeners
        singleListener.FrequencyGeneratorEventOccurred(new GeneratorEvent(EventType.FrequencyEvent));
    }
    
    public void SetVoltage(int newVoltage)
    {
        System.out.println("New voltage is: " + newVoltage);
        String setVoltageCommand = new String("V" + newVoltage + "\n\r");
        
        SendCommand(setVoltageCommand);
        
        this.voltage = newVoltage;
    }
    
    public void AddListener(FrequencyGeneratorListener listener)
    {
        System.out.println("Attempting to register observer...");

        if (listener != null)
        {
            System.out.println("Observer registered");

            this.singleListener = listener;
        }
    }

    public int GetFrequency()
    {
        return frequency;
    }
    
    public float GetVoltage()
    {
        return voltage;
    }
    
    private void SendCommand(String command)
    {
        try
        {
            // Sends the frequency set command on the serial port
            outputStream.write(command.getBytes(), 0, command.length());
        }
        catch (IOException e )
        {
            System.err.println(e.toString());
        }
    }
}
