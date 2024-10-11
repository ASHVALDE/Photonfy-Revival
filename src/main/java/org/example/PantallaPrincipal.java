package org.example;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.io.IOException;
import java.util.Objects;
import java.util.Stack;

import static org.example.Constants.Commands;

public class PantallaPrincipal extends javax.swing.JFrame {
    public JComboBox Bluetooth_Devices_Combo;
    private JLabel label1;
    private JPanel PanelPrincipal;
    private JButton conectarButton;
    private JTabbedPane panelX;
    private JPanel GET_RAW_SPECTRUM;
    private JTextArea textArea1;
    private JPanel GET_GAIN;
    private JPanel GET_INTEGRATION_TIME;
    private JPanel GET_TRANSFER_FUNCTION;
    private JPanel STOP_STREAMING;
    private JButton Send_Command;
    private JPanel GET_DEFAULT_TRANSFER_FUNCTION;
    private JPanel GET_WAVELENGTH_CALIBRATION;
    private JPanel GET_DEFAULT_WAVELENGTH_CALIBRATION;
    private JPanel RELOAD_DEFAULT_WAVELENGTH_CALIBRATION;
    private JPanel CALIBRATE_BACKGROUND;
    private JPanel GET_BACKGROUND_CALIBRATIONS;
    private JPanel CALIBRATE_DEFAULT_BACKGROUND;
    private JPanel GET_DEFAULT_BACKGROUND_CALIBRATIONS;
    private JPanel RELOAD_DEFAULT_BACKGROUND_CALIBRATIONS;
    private JPanel GET_LUX_CALIBRATION;
    private JPanel GET_DEFAULT_LUX_CALIBRATION;
    private JPanel RELOAD_DEFAULT_LUX_CALIBRATION;
    private JPanel GET_TIME;
    private JPanel GET_DEVICE_STATE;
    private JPanel GET_DEVICE_INFO;
    private JPanel SET_SOFT_FACTORY_RESET;
    private JPanel SET_HARD_FACTORY_RESET;
    private JPanel GET_BACKGROUND_COEFFICIENTS;
    private JPanel GET_DEFAULT_BACKGROUND_COEFFICIENTS;
    private JPanel RELOAD_DEFAULT_BACKGROUND_COEFFICIENTS;
    private JPanel SET_INTEGRATION_TIME;
    private JTextArea SET_INTEGRATION_TIME_1;
    private JPanel SET_GAIN;
    private JTextArea SET_GAIN_1;
    private JTextPane textPane2;
    private JPanel SET_DEFAULT_TRANSFER_FUNCTION;
    private JPanel SET_WAVELENGTH_CALIBRATION;
    private JTextField SET_WAVELENGTH_CALIBRATION_1;
    private JTextField SET_WAVELENGTH_CALIBRATION_2;
    private JTextField SET_WAVELENGTH_CALIBRATION_3;
    private JTextField SET_WAVELENGTH_CALIBRATION_4;
    private JTextField SET_WAVELENGTH_CALIBRATION_5;
    private JTextField SET_WAVELENGTH_CALIBRATION_6;
    private JPanel SET_LUX_CALIBRATION;
    private JTextField luxCoef;
    private JPanel SET_TIME;
    private JPanel SET_BLUETOOTH_NAME;
    private JTextField SET_BLUETOOTH_NAME_1;
    private JPanel GET_FLICKERING;
    private JPanel GET_VIDEO_SAMPLE_RATE;
    private JPanel SET_VIDEO_SAMPLE_RATE;
    private JPanel SET_DEFAULT_LUX_CALIBRATION;
    private JTextField SET_DEFAULT_LUX_CALIBRATION_1;
    private JTextField SET_VIDEO_SAMPLE_RATE_1;
    private JTextField SET_TIME_1;
    private JLabel Hora;
    private JTextField SET_TIME_2;
    private JPanel SET_BACKGROUND;
    private JPanel SET_DEFAULT_BACKGROUND;
    private JTextField SET_BACKGROUND_1;
    private JTextField SET_BACKGROUND_2;
    private JTextField SET_BACKGROUND_3;
    private JTextField SET_DEFAULT_BACKGROUND_1;
    private JTextField SET_DEFAULT_BACKGROUND_2;
    private JTextField SET_DEFAULT_BACKGROUND_3;
    private JScrollBar scrollBar1;
    private JPanel ScrollPanel;
    dataSender sender=null;

    public PantallaPrincipal(Constants constantes) throws IOException {
        // Hacemos la configuracion inicial de la ventana
        setVisible(true);
        setSize(700,600);
        setTitle("PhotonfyRe");
        setLocationRelativeTo(null);
        setContentPane(PanelPrincipal);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        // Escaneamos los dispositivos que tenemos vinculados (Ya debe estar el photonfy)
        LocalDevice device = LocalDevice.getLocalDevice();
        RemoteDevice[] devices = device.getDiscoveryAgent().retrieveDevices(DiscoveryAgent.PREKNOWN);

        // Metemos los dispositivos vinculados en una combobox para seleccionar cual es el photonfy
        for (int i = 0; i < devices.length; i++) {
            final String DeviceName = devices[i].getFriendlyName(false);
            final String DeviceMAC = devices[i].getBluetoothAddress();
            Bluetooth_Devices_Combo.addItem("Nombre: "+DeviceName+"||   MAC: "+DeviceMAC);
        }

        // Intentamos hacer la conexion BT
        conectarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final int IDbt = (Bluetooth_Devices_Combo.getSelectedIndex());
                final RemoteDevice Photonfy = devices[IDbt];
                try {
                    final String DeviceName = devices[IDbt].getFriendlyName(false);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                final String DeviceMAC = devices[IDbt].getBluetoothAddress();
                try {
                    StreamConnection conn = (StreamConnection) Connector.open("btspp://"+DeviceMAC+":5");
                    dataReceiver receiver = new dataReceiver(conn,textPane2);
                    receiver.start();
                    sender = new dataSender(conn);
                    conectarButton.setEnabled(false);

                } catch (IOException ex) {


                    throw new RuntimeException(ex);

                }
            }
        });

        Send_Command.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final int panelID = panelX.getSelectedIndex();
                final String tabName = panelX.getTitleAt(panelID);
                switch (Commands.get(tabName)){
                    // SET_INTEGRATION_TIME
                    case 1:
                        final String input_SET_INTEGRATION_TIME = SET_INTEGRATION_TIME_1.getText();

                        if(!Utils.isThisStringaNumber(input_SET_INTEGRATION_TIME)){
                            JOptionPane.showMessageDialog(new JFrame(), "La entrada debe ser un numero.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        final int INTEGRATION_TIME = Integer.parseInt(input_SET_INTEGRATION_TIME);
                        if(INTEGRATION_TIME <0 || INTEGRATION_TIME > 6000){
                            JOptionPane.showMessageDialog(new JFrame(), "La entrada debe ser un numero entre 0 y 6000.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        try {
                            sender.SET_INTEGRATION_TIME((short) INTEGRATION_TIME);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                    break;

                        case 3:
                            final String input_SET_GAIN = SET_GAIN_1.getText();
                            if(!Utils.isThisStringaNumber(input_SET_GAIN)){
                                JOptionPane.showMessageDialog(new JFrame(), "La entrada debe ser un numero.", "Dialog",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            final int GAIN_1 = Integer.parseInt(input_SET_GAIN);
                            if(!(GAIN_1==0 || GAIN_1 ==1)){
                                JOptionPane.showMessageDialog(new JFrame(), "La entrada debe ser 1 o 0.", "Dialog",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            try {
                                sender.SET_GAIN((byte) GAIN_1);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            break;
                    case 0xD:
                        Stack<Float> Coef = new Stack<>();
                        JTextField[] Coeficientes = {SET_WAVELENGTH_CALIBRATION_1, SET_WAVELENGTH_CALIBRATION_2, SET_WAVELENGTH_CALIBRATION_3, SET_WAVELENGTH_CALIBRATION_4, SET_WAVELENGTH_CALIBRATION_5, SET_WAVELENGTH_CALIBRATION_6};
                        // Checks:
                        for (int i = 0; i < Coeficientes.length; i++) {
                            if(Objects.equals(Coeficientes[i].getText(), "")){
                                JOptionPane.showMessageDialog(new JFrame(), "Debe llenar todos los espacios.", "Dialog",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            if(! Utils.isThisStringaNumber(Coeficientes[i].getText())){
                                JOptionPane.showMessageDialog(new JFrame(), "La entrada debe ser un numero.", "Dialog",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            float num = Float.parseFloat(Coeficientes[i].getText());
                            if(num<-380 || num>380){
                                JOptionPane.showMessageDialog(new JFrame(), "El valor debe estar entre -380 y 380 (nm)", "Dialog",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            Coef.push(num);
                        }
                        try {
                            sender.SET_WAVELENGTH_CALIBRATION(Coef);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                    case 0x18:
                        String Lux = luxCoef.getText();
                        if(Objects.equals(Lux, "")){
                            JOptionPane.showMessageDialog(new JFrame(), "Debe llenar todos los espacios.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if(! Utils.isThisStringaNumber(Lux)){
                            JOptionPane.showMessageDialog(new JFrame(), "La entrada debe ser un numero.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        sender.SET_LUX_CALIBRATION(Float.parseFloat(Lux));
                        break;
                    case 0x1F:

                        String Fecha = SET_TIME_1.getText();
                        String Hora = SET_TIME_2.getText();
                        if(Objects.equals(Fecha, "") || Objects.equals(Hora, "")){
                            JOptionPane.showMessageDialog(new JFrame(), "Debe llenar todos los espacios.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if(Utils.isDateValid(Fecha)){
                            JOptionPane.showMessageDialog(new JFrame(), "Introduzca una fecha valida.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (!Hora.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")){
                            JOptionPane.showMessageDialog(new JFrame(), "Introduzca una hora valida.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        sender.SET_TIME(Fecha, Hora);
                        break;
                    case 0x33:
                        String BTName = SET_BLUETOOTH_NAME_1.getText();
                        if(BTName.length()>23){
                            JOptionPane.showMessageDialog(new JFrame(), "Maximo 23 caracteres.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        sender.SET_BLUETOOTH_NAME(BTName);
                        break;
                    case 0x1A:
                        String DefaultLux = SET_DEFAULT_LUX_CALIBRATION_1.getText();
                        if(Objects.equals(DefaultLux, "")){
                            JOptionPane.showMessageDialog(new JFrame(), "Debe llenar todos los espacios.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if(! Utils.isThisStringaNumber(DefaultLux)){
                            JOptionPane.showMessageDialog(new JFrame(), "La entrada debe ser un numero.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        sender.SET_DEFAULT_LUX_CALIBRATION(Float.parseFloat(DefaultLux));
                        break;
                    case 0x1D:
                        String newSampleRate = SET_VIDEO_SAMPLE_RATE_1.getText();
                        if(Objects.equals(newSampleRate, "")){
                            JOptionPane.showMessageDialog(new JFrame(), "Debe llenar todos los espacios.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if(! Utils.isThisStringaNumber(newSampleRate)){
                            JOptionPane.showMessageDialog(new JFrame(), "La entrada debe ser un numero.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if(Float.parseFloat(newSampleRate)<=0){
                            JOptionPane.showMessageDialog(new JFrame(), "La entrada debe ser un numero positivo.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        sender.SET_VIDEO_SAMPLE_RATE(Float.parseFloat(newSampleRate));
                        break;
                    case 0x29:
                        String countBackground = SET_BACKGROUND_1.getText();
                        String integrationBackground = SET_BACKGROUND_2.getText();
                        String Temp = SET_BACKGROUND_3.getText();
                        if(Objects.equals(countBackground, "") || Objects.equals(integrationBackground, "") || Objects.equals(Temp, "")){
                            JOptionPane.showMessageDialog(new JFrame(), "Debe llenar todos los espacios.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if(! Utils.isThisStringaNumber(countBackground) || !Utils.isThisStringaNumber(integrationBackground) || !Utils.isThisStringaNumber(Temp)){
                            JOptionPane.showMessageDialog(new JFrame(), "Las entradas deben ser un numero.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        short Count = Short.parseShort(countBackground);
                        Short Integration = Short.parseShort(integrationBackground);
                        Float Temp1 = Float.parseFloat(Temp);
                        sender.SET_BACKGROUND(Count,Integration,Temp1);
                        break;

                    case 0x2A:
                        String countBackgroundDefault = SET_DEFAULT_BACKGROUND_1.getText();
                        String integrationBackgroundDefault = SET_DEFAULT_BACKGROUND_2.getText();
                        String TempDefault = SET_DEFAULT_BACKGROUND_3.getText();
                        if(Objects.equals(countBackgroundDefault, "") || Objects.equals(integrationBackgroundDefault, "") || Objects.equals(TempDefault, "")){
                            JOptionPane.showMessageDialog(new JFrame(), "Debe llenar todos los espacios.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if(! Utils.isThisStringaNumber(countBackgroundDefault) || !Utils.isThisStringaNumber(integrationBackgroundDefault) || !Utils.isThisStringaNumber(TempDefault)){
                            JOptionPane.showMessageDialog(new JFrame(), "Las entradas deben ser un numero.", "Dialog",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        short CountDefault = Short.parseShort(countBackgroundDefault);
                        Short IntegrationDefault = Short.parseShort(integrationBackgroundDefault);
                        Float Temp1Default = Float.parseFloat(TempDefault);
                        sender.SET_DEFAULT_BACKGROUND(CountDefault,IntegrationDefault,Temp1Default);
                        break;
                    default:
                        try {
                            sender.sendCommand(Commands.get(tabName));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    break;
                }


            }
        });

        textPane2.addComponentListener(new ComponentAdapter() {
        });
    }



}
