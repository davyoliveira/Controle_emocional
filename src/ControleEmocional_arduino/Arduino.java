package ControleEmocional_arduino;

import ControleEmocional_interfaces.JFrameMedicao_1;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Arduino implements SerialPortEventListener {

    SerialPort serialPort;
    Timer timer;
    /**
     * The port we're normally going to use.
     */
    private static final String PORT_NAMES[] = {
        "/dev/ttyUSB0", // Linux
        "COM3", // Windows
    };
    /**
     * A BufferedReader which will be fed by a InputStreamReader converting the
     * bytes into characters making the displayed results codepage independent
     */
    private BufferedReader input;
    /**
     * The output stream to the port
     */
    private OutputStream output;
    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 2000;
    /**
     * Default bits per second for COM port.
     */
    private static final int DATA_RATE = 115200;

    private String linha = null;
    private FileReader arq;

    /*private PrintWriter gravarArq = null;
    private FileWriter arq2 = null;*/

    //private ArrayList<String> batimentos = new ArrayList<String>();

    /*public ArrayList<String> getBatimentos() {
        return batimentos;
    }*/
    public void initialize() {

        try {
            arq = new FileReader("src/Data/UsuarioAtual.txt");
            BufferedReader lerArq = new BufferedReader(arq);
            linha = lerArq.readLine();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JFrameMedicao_1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JFrameMedicao_1.class.getName()).log(Level.SEVERE, null, ex);
        }

        ////////////////////////////////////////////////////////////////////
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        //System.out.println("cpppp");
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {

                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        //System.out.println("ctttt");
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }

        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    /**
     * This should be called when you stop using the port. This will prevent
     * port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine = input.readLine();
                System.out.println(inputLine);
                if (Character.isDigit(inputLine.charAt(0))) {
                    //batimentos.add(inputLine);
                    PrintWriter gravarArq;
                    FileWriter arq2;
                    arq2 = new FileWriter("src/ControleEmocional_Data_weka/" + linha + "dados.arff", true);
                    gravarArq = new PrintWriter(arq2);
                    gravarArq.printf(inputLine + ",?\n");
                    arq2.close();
                }
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }

    /*public void begin() throws Exception {
        Arduino main = new Arduino();
        main.initialize();
        Thread t = new Thread() {
            public void run() {
                //the following line will keep this app alive for 1000 seconds,
                //waiting for events to occur and responding to them (printing incoming messages to console).
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ie) {
                }
            }
        };        
        t.start();
        System.out.println("Started");
        
    }*/
}
