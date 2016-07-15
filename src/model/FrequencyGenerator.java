package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import controller.EventType;
import controller.FrequencyGeneratorListener;
import controller.GeneratorEvent;
import controller.WaveformType;
import gnu.io.CommPort;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.CommPortIdentifier;

public class FrequencyGenerator implements SerialPortEventListener
{

    private HashSet<String> comNameHashSet;
    private HashSet<CommPortIdentifier> comHashSet;
    
    // TODO move this into its own class
    private SerialPort serialPort;
    private BufferedReader inputStream; // A BufferedReader which will be fed by a InputStreamReader converting the bytes into characters making the displayed results code page independent
    private OutputStream outputStream; // The output stream to the port
    private static final int TIME_OUT = 2000; // Milliseconds to block while waiting for port open
    private static final int DATA_RATE = 57600; // Default bits per second for COM port.
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
        // TODO instead of assuming the waveform state on startup, this should actually query and get the response back
        // On startup, the waveform is off

        waveform = WaveformType.Off;
        
        comNameHashSet = new HashSet<String>();
        comHashSet = new HashSet<CommPortIdentifier>();
        
        Enumeration<CommPortIdentifier> thePorts = CommPortIdentifier.getPortIdentifiers();
        
        while (thePorts.hasMoreElements())
        {
            CommPortIdentifier com = thePorts.nextElement();

            // We're only interested in serial ports            
            if (com.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
                // Try to open and close the com port, if successful, then add it to the hash set
                try
                {
                    CommPort comPort = com.open("CommUtil", 50);
                    comPort.close();
                    comNameHashSet.add(com.getName());
                    comHashSet.add(com);
                }
                catch (PortInUseException e)
                {
                    System.out.println("Port: " + com.getName() + "is in use.");
                }
                catch (Exception e)
                {
                    System.err.println("Failed to open port: " + com.getName());
                    e.printStackTrace();
                }
            }
        }
    }
    
    public HashSet<String> getAvailableSerialPorts()
    {
        return comNameHashSet;
    }

    public void setSerialPort(String comName)
    {
        CommPortIdentifier comFound = null;
        
        Iterator<CommPortIdentifier> itr = comHashSet.iterator();
        
        // Look for the specified serial port in the has set
        while (itr.hasNext())
        {
            CommPortIdentifier comInHash = itr.next();
            
            if (comInHash.getName().equals(comName))
            {
                // We have a string match
                comFound = comInHash;
                break;
            }
        }
        
        if (comFound == null)
        {
            System.out.println("Could not find COM port.");
        }
        
        try
        {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) comFound.open(this.getClass().getName(), TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            // open the streams
            inputStream = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            outputStream = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            
            System.out.println("Successfully set " + comName);
        }
        catch (Exception e)
        {
            System.err.println("Unable to open serial port...");
            System.err.println(e.toString());
        }
    }
    
    public void SetFrequency(int newFrequency)
    {
        // System.out.println("New frequency is: " + newFrequency);
        
        String setFrequencyCommand = new String("f" + newFrequency);
        
        SendCommand(setFrequencyCommand);
        
        // TODO Reads the serial port to ensure the value has been changed? 

        // Updates its frequency value in the object
        this.frequency = newFrequency;

        // Sends an event to any subscribed listeners
        singleListener.FrequencyGeneratorEventOccurred(new GeneratorEvent(EventType.FrequencyEvent));
    }
    
    public void SetVoltage(int newVoltage)
    {
        // System.out.println("New voltage is: " + newVoltage);
        String setVoltageCommand = new String("a" + newVoltage);
        
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

    public void SetWaveform(WaveformType requestedWaveform)
    {        
        WaveformType previousWaveform = this.waveform;
        
        switch (requestedWaveform)
        {
            case Off:
                if (previousWaveform != WaveformType.Off)
                {
                    // Turn the waveform off by sending the command and remembering the state
                    OutputDisable();
                }
            break;
            case Sine:
                if (previousWaveform != WaveformType.Sine)
                {
                    SendCommand(new String("wsin"));
                }
            break;
            case Triangle:
                if (previousWaveform != WaveformType.Triangle)
                {
                    SendCommand(new String("wtri"));
                }
            break;
            case Square:
                if (previousWaveform != WaveformType.Square)
                {
                    SendCommand(new String("wsq"));
                }
            break;
            default:
                System.err.println("Invalid waveform sent!");
            break;
        }
        
        // Requested waveform requested not off but the current state but only enable if it was previously off
        if (requestedWaveform != WaveformType.Off && previousWaveform == WaveformType.Off)
        {
            // Turn the waveform on by sending the command and remembering the state
            OutputEnable();
        }
        
        this.waveform = requestedWaveform;
    }
    
    public int GetFrequency()
    {
        return frequency;
    }
    
    public float GetVoltage()
    {
        return voltage;
    }
    
    @Override
    public void serialEvent(SerialPortEvent serialPortEvent)
    {
        if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try 
            {
                int charReceived = 0;

                if ((charReceived = inputStream.read()) > -1)
                {
                    buffer[numberOfChars] = (byte) charReceived;
                    numberOfChars++;
                    
                    // TODO debug only ... was + || charReceived == '\r' ||
                    if (charReceived == '\n' || numberOfChars >= BUFFER_SIZE)
                    {
                        // Finish the sequence with a \N to indicate a new line
                        // System.out.println("\\N");
                        
                        // Newline character received or buffer at limit then exit
                        // System.out.println(new String(buffer, 0, numberOfChars));
                        System.out.print(new String(buffer, 0, numberOfChars));
                        numberOfChars = 0;
                    }
                }
            }
            catch (Exception e)
            {
                System.err.println(e.toString());
            }
        }       
    }
    
    private void SendCommand(String command)
    {
        try
        {
            // TODO still needed? was \n\r
            String LineFeed = new String("\n");
            
            // Sends the frequency set command on the serial port
            outputStream.write(command.getBytes(), 0, command.length());
            outputStream.write(LineFeed.getBytes(), 0, LineFeed.length());
        }
        catch (IOException e)
        {
            System.err.println(e.toString());
        }
    }
    
    private void OutputEnable()
    {
        this.isEnabled = true;
        SendCommand(new String("O"));
    }
    
    private void OutputDisable()
    {
        this.isEnabled = false;
        SendCommand(new String("o"));
    }
}
