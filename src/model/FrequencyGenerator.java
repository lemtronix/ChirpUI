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
    
    Copyright 2016 Mike Lemberger
*/

package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import controller.EventType;
import controller.FrequencyGeneratorListener;
import controller.GeneratorEvent;
import controller.WaveformType;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.NRSerialPort;

public class FrequencyGenerator implements SerialPortEventListener
{

    private HashSet<String> comNameHashSet;
    
    // TODO move this into its own class
    private NRSerialPort serialPort;
    //private SerialPort serialPort;
    private DataInputStream inputStream; // A BufferedReader which will be fed by a InputStreamReader converting the bytes into characters making the displayed results code page independent
    private DataOutputStream outputStream; // The output stream to the port
    private static final int TIME_OUT = 2000; // Milliseconds to block while waiting for port open
    private static final int DATA_RATE = 57600; // Default bits per second for COM port.
    private static final int BUFFER_SIZE = 128;
    private byte[] buffer = new byte[BUFFER_SIZE];
    private int numberOfChars = 0;

    private int frequency;
    private float amplitude;
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
        
        Set<String> thePorts = NRSerialPort.getAvailableSerialPorts();
        
        for (String string : thePorts)
        {
            // Try to open and close the com port, if successful, then add it to the hash set
            try
            {
                NRSerialPort serial = new NRSerialPort(string, DATA_RATE);
                if (serial.connect() == true)
                {
                    comNameHashSet.add(string);
                    serial.disconnect();
                }
            }
            catch (Exception e)
            {
                System.err.println("Failed to open port: " + string);
                e.printStackTrace();
            }
        }
    }
    
    public HashSet<String> getAvailableSerialPorts()
    {
        return comNameHashSet;
    }

    public void setSerialPort(String comName)
    {
        String comFound = null;
        
        Iterator<String> itr = comNameHashSet.iterator();
        
        // Look for the specified serial port in the has set
        while (itr.hasNext())
        {
            String comInHash = itr.next();
            
            if (comInHash.equals(comName))
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
            serialPort = new NRSerialPort(comFound, DATA_RATE);
            serialPort.connect();
            
            // set port parameters
            // serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            
            // open the streams
            inputStream = new DataInputStream(serialPort.getInputStream());
            outputStream = new DataOutputStream(serialPort.getOutputStream());

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
        
        this.amplitude = newVoltage;
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
                    SendCommand(new String("wsq2"));
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
        return amplitude;
    }
    
    public void GetStatus() 
    {
        try 
        {
            // Clear the serial buffer
            inputStream.reset();
            
            // Send a status command (currently just an enter key to get the status prompt)
            SendCommand(new String(""));
            
            // use a semaphore to wait for the serialEvent?
            
            // serialEvent needs to parse the status string from Chirp
            
            // Send an event to any registered listeners that an update has occurred
            
            // Menu system processes the event and either knows it needs to get values or the values are contained in the object
        }
        catch (IOException e)
        {
            System.err.println("Exception attempting to reset the input stream...");
            e.printStackTrace();
        }
    }
    
    public void Close()
    {
        if ((serialPort != null) && (serialPort.isConnected() == true))
        {
            System.out.println("Closing session...");
            serialPort.disconnect();
        }
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
                    // Filter the input...
                    if (charReceived == '\r')
                    {
                        // Ignore these characters
                    }
                    else
                    {
                        // Otherwise capture the other characters
                        buffer[numberOfChars] = (byte) charReceived;
                        numberOfChars++;
                    }
                    
                    // If an enter key is pressed or the buffer is exceeded, then process the message
                    if (charReceived == '\n' || numberOfChars >= BUFFER_SIZE)
                    {
                        String message = new String(buffer, 0, numberOfChars);
                        
                        // Parse message, format is "Q_WAV_F#####_A#####_P###_OFF>" delimiters are '_' or '>'
                        StringTokenizer st = new StringTokenizer(message, "[_>]");
                        StringTokenizer stTest = new StringTokenizer(message, "[_>]");
                        
                        while (stTest.hasMoreElements())
                        {
                            System.out.println(stTest.nextToken());
                        }
                        
                        if (st.countTokens() >= 5)
                        {
                            // Then we have a status message
                            
                            // If the first token is a Q, then we're in quick mode

                            // Parse Waveform
                            String waveformString = st.nextToken();
                            
                            if (waveformString != null)
                            {
                                System.out.print("Waveform: ");
                                
                                if (waveformString.equals("SIN"))
                                {
                                    System.out.println("Sine");
                                }
                                else if (waveformString.equals("TRI"))
                                {
                                    System.out.println("Triangle");
                                }
                                else if (waveformString.equals("SQ"))
                                {
                                    System.out.println("Square");
                                }
                                else if (waveformString.equals("SQ2"))
                                {
                                    System.out.println("SquareDiv2");
                                }
                                else
                                {
                                    System.out.println("Unknown");
                                }
                            }
                            
                            // Parse Frequency
                            // Parse Amplitude
                            // Parse Phase?
                            StringValueParser(st.nextToken(), 'F');
                            StringValueParser(st.nextToken(), 'A');
                            StringValueParser(st.nextToken(), 'P');

                            // Parse On or Off
                        }

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
    
    private int StringValueParser(String stringWithValueToParse, char characterDelimiter)
    {
        int value = 0;
        
        if (stringWithValueToParse != null)
        {
            System.out.print("Value: ");
            
            StringTokenizer token = new StringTokenizer(stringWithValueToParse, String.valueOf(characterDelimiter));
            
            try
            {
                value = Integer.parseInt(token.nextToken());
                System.out.println(value);
            }
            catch (NumberFormatException nfe)
            {
                System.err.println("Parsing Chirp input results in a Number Format Exception");
            }
        }
        return value;
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
