package org.example;

import com.github.ASHVALDE.DataPackage;
import com.github.ASHVALDE.Photonfy;
import com.github.ASHVALDE.PhotonfyCodes;
import javafx.scene.layout.Pane;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.sound.midi.SysexMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;


public class PantallaPrincipal extends javax.swing.JFrame {
    public JComboBox Bluetooth_Devices_Combo;
    private JLabel label1;
    private JPanel PanelPrincipal;
    private JButton conectarButton;
    private JTextArea textArea1;
    private JButton Send_Command;
    private JTextPane textPane2;
    private JComboBox comboBox1;
    private JPanel panelFormulario;
    private JScrollPane TextoPane;
    private JScrollBar scrollBar1;
    private JPanel ScrollPanel;
    Photonfy photonfy;
    Stack<JTextArea> Texts = new Stack<>();


    Map<Integer, String[]> map = new HashMap<Integer, String[]>();
    Map<Integer, String> Hints = new HashMap<Integer, String>();



    public PantallaPrincipal() throws IOException {
        // Hacemos la configuracion inicial de la ventana
        setVisible(true);
        setSize(700,600);
        setTitle("Photonfy");
        setLocationRelativeTo(null);
        setContentPane(PanelPrincipal);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        String[] Excluded = new String[]{"GET_DEFAULT_BACKGROUND_COEFFICIENTS","GET_BACKGROUND_COEFFICIENTS","GET_BACKGROUND_COEFFICIENTS" ,"GET_DEFAULT_BACKGROUND_CALIBRATIONS","GET_BACKGROUND_CALIBRATIONS","GET_TIME", "GET_VIDEO_SAMPLE_RATE","GET_DEFAULT_LUX_CALIBRATION","GET_LUX_CALIBRATION","GET_DEFAULT_WAVELENGTH_CALIBRATION","GET_WAVELENGTH_CALIBRATION","GET_DEFAULT_TRANSFER_FUNCTION","GET_TRANSFER_FUNCTION","GET_GAIN","GET_INTEGRATION_TIME","START_STREAMING","STOP_STREAMING", "NOP","SET_FIRMWARE_UPDATE","SET_DEFAULT_FIRMWARE_UPDATE","SET_FIRMWARE_UPDATE_METADATA","NEXT_SYNC_SPECTRA","SET_DEVICE_INFO"};
        Hints.put(PhotonfyCodes.UNKNOWN.getHexCode(), "This application is an initiative by the National University of Colombia to continue supporting the Photonfy system,following the shutdown of its original servers.\n\nThe application is fully open source:\nhttps://github.com/ASHVALDE/PhotonfyCore\nhttps://github.com/ASHVALDE/PhotonfyRevival-Android\n\nWe would like to express our sincere gratitude to Professor Jesús María Quintero for his guidance and support in ensuring the continuation of this project.");

        Hints.put(PhotonfyCodes.SET_INTEGRATION_TIME.getHexCode(), "Time the sensor is open obtaining data. The longest the time the more light impacts the diode sensor (similar to *exposure time* in photography).");
        map.put(PhotonfyCodes.SET_INTEGRATION_TIME.getHexCode(), new String[]{"Integration Time (5-7000ms):"});
        Hints.put(PhotonfyCodes.SET_GAIN.getHexCode(), "Activates or deactivates the amplification of the received light signal modifying the wideness of the signal entry (similar concept to how much the shutter is open in photography).");
        map.put(PhotonfyCodes.SET_GAIN.getHexCode(), new String[]{"1 to Activate, 0 to Disable:"});


        Hints.put(PhotonfyCodes.GET_RAW_SPECTRUM.getHexCode(), "Gets the spectrum of the light received by the spectrometer. The data is sent raw from the sensor without prior treatment (only the tension between 0.0 V and 3.3 V is transformed to a 0 to 4095 count per pixel value). This is the counts passed to the micro by the 256 pixels of the sensor.");
        Hints.put(PhotonfyCodes.SET_TRANSFER_FUNCTION.getHexCode(),"Sets the spectrum calibration. This is an array of numbers that allow to go from pixel counts to microwatts per nanometer (µW/nm) by multiplication at the same time the function is calibrated. The calibration part means the transfer includes an adjustment that compensates the difference between the spectrum computed by the spectrometer and the same spectrum detected by the calibration sphere at the laboratory. That is, an adjustment from the sensor count to the lab measurement microwatts.");
        Hints.put(PhotonfyCodes.SET_DEFAULT_TRANSFER_FUNCTION.getHexCode(),"Sets the default spectrum calibration. This is an array of numbers that allow to go from pixel counts to microwatts per nanometer (µW/nm) by multiplication at the same time the function is calibrated. The calibration part means the transfer includes an adjustment that compensates the difference between the spectrum computed by the spectrometer and the same spectrum detected by the calibration sphere at the laboratory. That is, an adjustment from the sensor count to the lab measurement microwatts.");
        Hints.put(PhotonfyCodes.SET_WAVELENGTH_CALIBRATION.getHexCode(),"Sets the conversion factors from pixel number (its order in the array) to wavelength in nanometers (nm). These are 6 coefficients that multiply the pixel number to obtain the nanometers represented by that pixel. The first element is the largest coefficient multiplied by one, the second is the second largest multiplied by the pixel number, the third is the third largest coefficient multiplied by the square of the pixel number and so on until the sixth coefficient, the smallest one. Formula: nm = y (px) = a0 + a1 * px + a2 * px^2 + a3 * px^3 + a4 * px^4 + a5 * px^5.");
        Hints.put(PhotonfyCodes.SET_BACKGROUND_COEFFICIENTS.getHexCode(),"Set the backgrounds coefficients previously mesured. We have been mesured  14 diferents integration time in a range of temperature in order to know the background behaviour. formula:  y=a*x^2+b*x+c , where a,b,c is the matrix.\n (Copy from SET_BACKGROUND_COEFFICIENTS make Changes and paste on the coefficients field.)");
        Hints.put(PhotonfyCodes.SET_DEFAULT_BACKGROUND_COEFFICIENTS.getHexCode(),"Set the backgrounds coefficients previously mesured. We have been mesured  14 diferents integration time in a range of temperature in order to know the background behaviour. formula:  y=a*x^2+b*x+c , where a,b,c is the matrix. \n (Copy from SET_BACKGROUND_COEFFICIENTS make Changes and paste on the coefficients field.)");
        Hints.put(PhotonfyCodes.END_INITIALIZATION.getHexCode(),"This function must be called application have all the calibrations and information about the spectrometer. This function idicates to the spectrometer that is able to take fotos and videos because initialization phase has finalized.");
        Hints.put(PhotonfyCodes.GET_FLICKERING.getHexCode(),"Get the flickering of the light.");
        Hints.put(PhotonfyCodes.SET_BLUETOOTH_NAME.getHexCode(),"Sets the bluetooth name.");

        map.put(PhotonfyCodes.SET_TRANSFER_FUNCTION.getHexCode(), new String[]{"81 Floating point numbers:"});
        map.put(PhotonfyCodes.SET_DEFAULT_TRANSFER_FUNCTION.getHexCode(), new String[]{"81 Floating point numbers:"});

        map.put(PhotonfyCodes.SET_WAVELENGTH_CALIBRATION.getHexCode(), new String[]{"A0:", "A1:", "A2:", "A3:", "A4:", "A5:"});
        Hints.put(PhotonfyCodes.SET_DEFAULT_WAVELENGTH_CALIBRATION.getHexCode(),"Sets defaults conversion factors from pixel number (its order in the array) to wavelength in nanometers (nm). These are 6 coefficients that multiply the pixel number to obtain the nanometers represented by that pixel. The first element is the largest coefficient multiplied by one, the second is the second largest multiplied by the pixel number, the third is the third largest coefficient multiplied by the square of the pixel number and so on until the sixth coefficient, the smallest one. Formula: nm = y (px) = a0 + a1 * px + a2 * px^2 + a3 * px^3 + a4 * px^4 + a5 * px^5.");
        Hints.put(PhotonfyCodes.CALIBRATE_BACKGROUND.getHexCode(),"Spectrometer prepares the calibration, checking the dark position sensor and returning a completed if it is ready. If it is not ready, it will return an error. In order to calibrate the default calibration, function *CALIBRATE_DEFAULT_BACKGROUND* must be used in this place.");
        Hints.put(PhotonfyCodes.CALIBRATE_DEFAULT_BACKGROUND.getHexCode(),"Spectrometer prepares the calibration, checking the dark position sensor and returning a completed if it is ready. If it is not ready, it will return an error. In order to calibrate the default calibration, function *CALIBRATE_DEFAULT_BACKGROUND* must be used in this place.");
        Hints.put(PhotonfyCodes.RELOAD_DEFAULT_BACKGROUND_CALIBRATIONS.getHexCode(),"puts the default calibration background into calibration background.\n");
        Hints.put(PhotonfyCodes.SET_LUX_CALIBRATION.getHexCode(),"Sets the lux calibration\n");
        Hints.put(PhotonfyCodes.SET_DEFAULT_LUX_CALIBRATION.getHexCode(),"Sets the lux calibration\n");
        Hints.put(PhotonfyCodes.RELOAD_DEFAULT_LUX_CALIBRATION.getHexCode(),"Loads the default lux value in the lux calibration\n");
        Hints.put(PhotonfyCodes.SET_VIDEO_SAMPLE_RATE.getHexCode(),"Sets the video frequency between frames in seconds.\n");
        Hints.put(PhotonfyCodes.SET_TIME.getHexCode(),"Updates the current time. IMPORTANT: each field must have 2 digits, except for the year, which must have 4. Time must be in 24-hour format (e.g., 7 PM is 19). The time must be in UTC-0.\n");
        Hints.put(PhotonfyCodes.SET_BACKGROUND.getHexCode(),"Set the background information.\n");
        Hints.put(PhotonfyCodes.SET_DEFAULT_BACKGROUND.getHexCode(),"Set the default background information.\n");
        Hints.put(PhotonfyCodes.RELOAD_DEFAULT_BACKGROUND_COEFFICIENTS.getHexCode(),"Reloads the default values.\n");

        map.put(PhotonfyCodes.SET_DEFAULT_WAVELENGTH_CALIBRATION.getHexCode(), new String[]{"A0:", "A1:", "A2:", "A3:", "A4:", "A5:"});
        map.put(PhotonfyCodes.SET_LUX_CALIBRATION.getHexCode(), new String[]{"Lux:"});
        map.put(PhotonfyCodes.SET_DEFAULT_LUX_CALIBRATION.getHexCode(), new String[]{"Lux:"});
        map.put(PhotonfyCodes.SET_VIDEO_SAMPLE_RATE.getHexCode(), new String[]{"SampleRate (Seconds):"});
        map.put(PhotonfyCodes.SET_TIME.getHexCode(), new String[]{"Hora (Militar):","Minuto:","Segundo:","Dia:","Mes:","Año:"});
        map.put(PhotonfyCodes.SET_BACKGROUND.getHexCode(), new String[]{"Counts:", "Tiempo de integracion(5-7000ms):", "Temperatura (Centigrados)"});
        map.put(PhotonfyCodes.SET_DEFAULT_BACKGROUND.getHexCode(), new String[]{"Counts:", "Tiempo de integracion(5-7000ms):", "Temperatura (Centigrados)"});
        map.put(PhotonfyCodes.SET_BACKGROUND_COEFFICIENTS.getHexCode(), new String[]{"Coeficientes:"});
        map.put(PhotonfyCodes.SET_DEFAULT_BACKGROUND_COEFFICIENTS.getHexCode(), new String[]{"Coeficientes:"});
        map.put(PhotonfyCodes.SET_BLUETOOTH_NAME.getHexCode(), new String[]{"Nombre del dispositivo:"});
        for(PhotonfyCodes code : PhotonfyCodes.values()){
            if(Arrays.stream(Excluded).anyMatch(x -> x.equals(code.name()))){
                continue;
            }
            String upTo3Characters = code.name().substring(0, Math.min(code.name().length(), 3));
            if(upTo3Characters.equals("RSP")){
                continue;
            }
            String upTo2Characters = code.name().substring(0, Math.min(code.name().length(), 2));
            if(upTo2Characters.equals("EV")){
                continue;
            }
            comboBox1.addItem(code);
        }
        fillForms();
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
                    JOptionPane.showMessageDialog(panelFormulario, "Can't Find this device!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                final String DeviceMAC = devices[IDbt].getBluetoothAddress();
                try {
                    StreamConnection conn = (StreamConnection) Connector.open("btspp://"+DeviceMAC+":5");


                    photonfy =  new Photonfy(conn.openInputStream(),conn.openOutputStream());
                    comboBox1.setEnabled(true);
                    panelFormulario.setEnabled(true);
                    Send_Command.setEnabled(true);
                    photonfy.addListener(new Photonfy.DataListener() {
                        @Override
                        public void onPackageReceived(DataPackage dataPackage) throws IOException {

                            // Cuando recibe un mensaje lo imprime
                            String codeName = String.valueOf(PhotonfyCodes.fromInt(dataPackage.token));
                            textPane2.setText(textPane2.getText()+"["+codeName+"]:\n");

                            if(codeName.contains("GET_") && comboBox1.getSelectedItem().toString().contains("SET_")){
                                int index = 0;
                                String[] datos = dataPackage.getResponse(true).replace(" ","").split(",");
                                System.out.println(Arrays.toString(datos));

                                for (Component comp : panelFormulario.getComponents()) {
                                    if (comp instanceof JTextArea) {
                                        if(((JTextArea) comp).isEditable()){
                                            if(index<datos.length){
                                                ((JTextArea) comp).setText(datos[index]);
                                                index++;
                                            }
                                        }

                                    }
                                }


                            }
                            textPane2.setText(textPane2.getText()+dataPackage.getResponse()+"\n\n");
                            // Ensure the text is added before scrolling
                            TextoPane.revalidate();
                            TextoPane.repaint();
                            // Use invokeLater to ensure the scrollbar update happens after the text is added
                            SwingUtilities.invokeLater(() -> {
                                JScrollBar bar = TextoPane.getVerticalScrollBar();
                                bar.setValue(bar.getMaximum());
                            });


                            if(dataPackage.token == PhotonfyCodes.GET_RAW_SPECTRUM.getHexCode()){
                                GenerateHTMLReport(dataPackage);
                            }
                        }
                    });
                    conectarButton.setEnabled(false);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(panelFormulario, "Error Connecting to this device!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

/*
        map.put(PhotonfyCodes.SET_GAIN.getHexCode(), new String[]{"1 para activar, 0 para desactivar:"});

        map.put(PhotonfyCodes.SET_WAVELENGTH_CALIBRATION.getHexCode(), new String[]{"A0:", "A1:", "A2:", "A3:", "A4:", "A5:"});
        map.put(PhotonfyCodes.SET_DEFAULT_WAVELENGTH_CALIBRATION.getHexCode(), new String[]{"A0:", "A1:", "A2:", "A3:", "A4:", "A5:"});
        map.put(PhotonfyCodes.SET_LUX_CALIBRATION.getHexCode(), new String[]{"Lux:"});
        map.put(PhotonfyCodes.SET_DEFAULT_LUX_CALIBRATION.getHexCode(), new String[]{"Lux:"});
        map.put(PhotonfyCodes.SET_VIDEO_SAMPLE_RATE.getHexCode(), new String[]{"SampleRate (Segundos):"});
        map.put(PhotonfyCodes.SET_TIME.getHexCode(), new String[]{"Fecha (DD/MM/AAAA):", "Hora (HH:MM:SS con dos digitos):"});
        map.put(PhotonfyCodes.SET_BACKGROUND.getHexCode(), new String[]{"Counts:", "Tiempo de integracion(5-7000ms):", "Temperatura (Centigrados)"});
        map.put(PhotonfyCodes.SET_DEFAULT_BACKGROUND.getHexCode(), new String[]{"Counts:", "Tiempo de integracion(5-7000ms):", "Temperatura (Centigrados)"});
        map.put(PhotonfyCodes.SET_BACKGROUND_COEFFICIENTS.getHexCode(), new String[]{"Coeficiente 1:","Coeficiente 2:","Coeficiente 3:","Coeficiente 4:","Coeficiente 5:","Coeficiente 6:","Coeficiente 7:","Coeficiente 8:","Coeficiente 9:","Coeficiente 10:","Coeficiente 11:","Coeficiente 12:","Coeficiente 13:","Coeficiente 14:"});
        map.put(PhotonfyCodes.SET_DEFAULT_BACKGROUND_COEFFICIENTS.getHexCode(), new String[]{"Coeficiente 1:","Coeficiente 2:","Coeficiente 3:","Coeficiente 4:","Coeficiente 5:","Coeficiente 6:","Coeficiente 7:","Coeficiente 8:","Coeficiente 9:","Coeficiente 10:","Coeficiente 11:","Coeficiente 12:","Coeficiente 13:","Coeficiente 14:"});
        map.put(PhotonfyCodes.SET_BLUETOOTH_NAME.getHexCode(), new String[]{"Nombre del dispositivo:"});

         */


        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillForms();
            }

        });

        Send_Command.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PhotonfyCodes selected = PhotonfyCodes.valueOf(comboBox1.getSelectedItem().toString());
                SwingUtilities.invokeLater(() -> {
                    switch (selected) {
                        case GET_DEVICE_STATE:
                        case STOP_STREAMING:
                        case RELOAD_DEFAULT_TRANSFER_FUNCTION:
                        case RELOAD_DEFAULT_WAVELENGTH_CALIBRATION:
                        case CALIBRATE_BACKGROUND:
                        case CALIBRATE_DEFAULT_BACKGROUND:
                        case RELOAD_DEFAULT_BACKGROUND_CALIBRATIONS:
                        case RELOAD_DEFAULT_LUX_CALIBRATION:
                        case GET_DEVICE_INFO:
                        case SET_SOFT_FACTORY_RESET:
                        case SET_HARD_FACTORY_RESET:
                        case GET_FLICKERING:
                        case RELOAD_DEFAULT_BACKGROUND_COEFFICIENTS:
                        case GET_RAW_SPECTRUM:
                            try {
                                photonfy.sender.sendCommand(selected.getHexCode());
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            break;
                        case SET_INTEGRATION_TIME:
                            try {
                                String IntegrationTime = Texts.get(0).getText();
                                short IntegrationTimeValue = (short) Integer.parseInt(IntegrationTime);
                                photonfy.sender.SET_INTEGRATION_TIME(IntegrationTimeValue);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, "El valor no corresponde a un numero entero", "Error", JOptionPane.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                        case SET_GAIN:
                            try {
                                String IntegrationTime = Texts.get(0).getText();
                                byte gainValue = (byte) Integer.parseInt(IntegrationTime);
                                photonfy.sender.SET_GAIN(gainValue);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, "El valor no corresponde a un numero entero", "Error", JOptionPane.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                            case SET_DEFAULT_TRANSFER_FUNCTION:
                        case SET_TRANSFER_FUNCTION:
                            Stack<Float> datos_Transfer = new Stack<>();
                                for (Component comp : panelFormulario.getComponents()) {
                                    if (comp instanceof JTextArea) {
                                        if(((JTextArea) comp).isEditable()){
                                            datos_Transfer.add(Float.parseFloat(((JTextArea) comp).getText()));
                                        }

                                    }
                                }
                                try {
                                    photonfy.sender.SET_TRANSFER_FUNCTION(datos_Transfer);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                                break;
                        case SET_WAVELENGTH_CALIBRATION:
                            try {
                            List<Float> Values = new Stack<>();
                            for(JTextArea Campos : Texts){
                                Float Value = Float.valueOf(Campos.getText());
                                Values.add(Value);
                            }
                            photonfy.sender.SET_WAVELENGTH_CALIBRATION(Values);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, "El valor no corresponde a un numero entero", "Error", JOptionPane.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                        case SET_DEFAULT_WAVELENGTH_CALIBRATION:
                            try {
                                List<Float> Values = new Stack<>();
                                for(JTextArea Campos : Texts){
                                    Float Value = Float.valueOf(Campos.getText());
                                    Values.add(Value);
                                }
                                photonfy.sender.SET_DEFAULT_WAVELENGTH_CALIBRATION(Values);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, "El valor no corresponde a un numero entero", "Error", JOptionPane.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                        case SET_LUX_CALIBRATION:
                            try {
                                String IntegrationTime = Texts.get(0).getText();
                                float gainValue = Float.parseFloat(IntegrationTime);
                                photonfy.sender.SET_LUX_CALIBRATION(gainValue);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, "El valor no corresponde a un numero entero", "Error", JOptionPane.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            break;

                        case SET_DEFAULT_LUX_CALIBRATION:
                            try {
                                String IntegrationTime = Texts.get(0).getText();
                                float gainValue = Float.parseFloat(IntegrationTime);
                                photonfy.sender.SET_DEFAULT_LUX_CALIBRATION(gainValue);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, "El valor no corresponde a un numero decimal", "Error", JOptionPane.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                        case SET_VIDEO_SAMPLE_RATE:
                            try {
                                String IntegrationTime = Texts.get(0).getText();
                                float gainValue = Float.parseFloat(IntegrationTime);
                                photonfy.sender.SET_VIDEO_SAMPLE_RATE(gainValue);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, "El valor no corresponde a un numero entero", "Error", JOptionPane.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                        case SET_TIME:
                            try {
                                String Hour = Texts.get(0).getText();
                                String Minute = Texts.get(1).getText();
                                String Second = Texts.get(2).getText();
                                String Day = Texts.get(3).getText();
                                String Month = Texts.get(4).getText();
                                String Year = Texts.get(5).getText();

                                photonfy.sender.SET_TIME(Day+"/"+Month+"/"+Year,Hour+":"+Minute+":"+Second);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, "El valor no corresponde a un numero entero", "Error", JOptionPane.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }

                            break;
                        case SET_BACKGROUND:
                            try {
                                short Counts = Short.parseShort(Texts.get(0).getText());
                                short TiempoIntegracion = Short.parseShort(Texts.get(1).getText());
                                float Temperatura = Float.parseFloat(Texts.get(2).getText());
                                photonfy.sender.SET_BACKGROUND(Counts,TiempoIntegracion,Temperatura);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, "El valor no corresponde a un numero entero", "Error", JOptionPane.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }

                            break;
                        case SET_DEFAULT_BACKGROUND:
                            try {
                                short Counts = Short.parseShort(Texts.get(0).getText());
                                short TiempoIntegracion = Short.parseShort(Texts.get(1).getText());
                                float Temperatura = Float.parseFloat(Texts.get(2).getText());
                                photonfy.sender.SET_DEFAULT_BACKGROUND(Counts,TiempoIntegracion,Temperatura);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, "El valor no corresponde a un numero entero", "Error", JOptionPane.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }

                            break;
                        case SET_DEFAULT_BACKGROUND_COEFFICIENTS:
                        case SET_BACKGROUND_COEFFICIENTS:
                            Stack<Float> DATA_BACKGROUND_COEFFICIENTS = new Stack<>();
                            for (Component comp : panelFormulario.getComponents()) {
                                if (comp instanceof JTextArea) {
                                    if(((JTextArea) comp).isEditable()){
                                        DATA_BACKGROUND_COEFFICIENTS.add(Float.parseFloat(((JTextArea) comp).getText()));
                                    }

                                }
                            }
                            try {
                                photonfy.sender.SET_BACKGROUND_COEFFICIENTS(DATA_BACKGROUND_COEFFICIENTS);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            break;

                        case SET_BLUETOOTH_NAME:
                            try {
                                String nombre = Texts.get(0).getText();
                                photonfy.sender.SET_BLUETOOTH_NAME(nombre);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, "El valor no corresponde a un numero entero", "Error", JOptionPane.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(PanelPrincipal, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                        default:
                            textPane2.setText(textPane2.getText() + "Not Implemented\n");
                            break;
                    }


                });
            }

        });

    }

    private void GenerateHTMLReport(DataPackage dataPackage) {
        String templatePath = "basic_html_template.html";
        // Ruta donde guardaremos el archivo HTML generado
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String outputPath = "Reportes/"+timestamp +".html";
        String Datos = dataPackage.getResponse().split("\n")[1];
        try {
            // Leer la plantilla HTML
            String htmlTemplate = new String(Files.readAllBytes(Paths.get(templatePath)));

            // Reemplazar las variables {{variable}} con valores dinámicos
            String htmlContent = htmlTemplate
                    .replace("{{datos}}",Datos );
            // Guardar el contenido generado en un archivo HTML
            Files.write(Paths.get(outputPath), htmlContent.getBytes(), StandardOpenOption.CREATE);
            Desktop.getDesktop().browse(Paths.get(outputPath).toUri());
            System.out.println("Archivo HTML generado: " + outputPath);

        } catch (IOException e) {
            System.err.println("Ocurrió un error: " + e.getMessage());
        }
    }

    public void fillForms(){
        try {
            Texts.clear();
            String comboBoxSelected = comboBox1.getSelectedItem().toString();
            PhotonfyCodes selected = PhotonfyCodes.valueOf(comboBoxSelected);

            if(comboBoxSelected.contains("SET_")) {
                try {
                    if(comboBoxSelected.equals("SET_BACKGROUND")||comboBoxSelected.equals("SET_DEFAULT_BACKGROUND") ) {
                        comboBoxSelected = comboBoxSelected.replace("_BACKGROUND","_BACKGROUND_CALIBRATIONS");
                    }
                    PhotonfyCodes getVariant = PhotonfyCodes.valueOf(comboBoxSelected.replace("SET_", "GET_"));
                    if (photonfy != null && getVariant.getHexCode() != -1) {
                        photonfy.sender.sendCommand(getVariant.getHexCode());
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            panelFormulario.removeAll();

            // Reset GridBagConstraints
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(2, 2, 15, 2);  // Add some padding
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;

            int selectedIndex = selected.getHexCode();
            if (Hints.containsKey(selectedIndex)) {
                // Use JTextArea for wrapping text
                JTextArea hintArea = new JTextArea(Hints.get(selectedIndex));

                hintArea.setEditable(false); // Make it non-editable
                hintArea.setFocusable(false); // Make it non-focusable
                hintArea.setLineWrap(true); // Enable line wrapping
                hintArea.setWrapStyleWord(true); // Wrap at word boundaries
                hintArea.setBackground(panelFormulario.getBackground()); // Match the panel's background
                hintArea.setBorder(null); // Remove border to make it look like a label

                gbc.gridx = 0;
                gbc.gridwidth = 3; // Span across all columns
                gbc.weightx = 1.0; // Take full width
                gbc.fill = GridBagConstraints.HORIZONTAL; // Ensure it fills the space horizontally
                panelFormulario.add(hintArea, gbc);
                gbc.gridy++;
                gbc.gridwidth = 1; // Reset gridwidth for subsequent components
            }

            if (map.containsKey(selectedIndex)) {
                if(selected.toString().equals("SET_DEFAULT_TRANSFER_FUNCTION")||selected.toString().equals("SET_TRANSFER_FUNCTION")){
                    for (int index=1;index<=81;index++) {
                        // Add Label
                        JLabel label = new JLabel("TF["+index+"]");
                        gbc.gridx = 0;
                        gbc.weightx = 0.2;
                        gbc.fill = GridBagConstraints.NONE; // Do not stretch the label
                        panelFormulario.add(label, gbc);

                        // Add Text Area
                        JTextArea textArea = new JTextArea(1, 20);  // Fixed rows to 1, columns to 20
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        gbc.gridx = 1;
                        gbc.weightx = 0.8;
                        gbc.fill = GridBagConstraints.HORIZONTAL; // Stretch the text area horizontally
                        Texts.add(textArea);
                        panelFormulario.add(textArea, gbc);

                        // Move to next row
                        gbc.gridy++;
                    }

                } else if (selected.toString().equals("SET_BACKGROUND_COEFFICIENTS")||selected.toString().equals("SET_DEFAULT_BACKGROUND_COEFFICIENTS")) {
                    for (int group=1;group<=14;group++) {
                        // Add Label
                        JLabel label = new JLabel("Termino Cuadratico del grupo["+group+"]");
                        gbc.gridx = 0;
                        gbc.weightx = 0.2;
                        gbc.fill = GridBagConstraints.NONE; // Do not stretch the label
                        panelFormulario.add(label, gbc);

                        // Add Text Area
                        JTextArea textArea = new JTextArea(1, 20);  // Fixed rows to 1, columns to 20
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        gbc.gridx = 1;
                        gbc.weightx = 0.8;
                        gbc.fill = GridBagConstraints.HORIZONTAL; // Stretch the text area horizontally
                        Texts.add(textArea);
                        panelFormulario.add(textArea, gbc);
                        // Move to next row
                        gbc.gridy++;
                        // Add Label
                        JLabel label2 = new JLabel("Termino Lineal del grupo["+group+"]");
                        gbc.gridx = 0;
                        gbc.weightx = 0.2;
                        gbc.fill = GridBagConstraints.NONE; // Do not stretch the label
                        panelFormulario.add(label2, gbc);

                        // Add Text Area
                        JTextArea textArea2 = new JTextArea(1, 20);  // Fixed rows to 1, columns to 20
                        textArea2.setLineWrap(true);
                        textArea2.setWrapStyleWord(true);
                        gbc.gridx = 1;
                        gbc.weightx = 0.8;
                        gbc.fill = GridBagConstraints.HORIZONTAL; // Stretch the text area horizontally
                        Texts.add(textArea2);
                        panelFormulario.add(textArea2, gbc);
                        // Move to next row
                        gbc.gridy++;

                        // Add Label
                        JLabel label3 = new JLabel("Termino constante del grupo["+group+"]");
                        gbc.gridx = 0;
                        gbc.weightx = 0.2;
                        gbc.fill = GridBagConstraints.NONE; // Do not stretch the label
                        panelFormulario.add(label3, gbc);

                        // Add Text Area
                        JTextArea textArea3 = new JTextArea(1, 20);  // Fixed rows to 1, columns to 20
                        textArea3.setLineWrap(true);
                        textArea3.setWrapStyleWord(true);
                        gbc.gridx = 1;
                        gbc.weightx = 0.8;
                        gbc.fill = GridBagConstraints.HORIZONTAL; // Stretch the text area horizontally
                        Texts.add(textArea3);
                        panelFormulario.add(textArea3, gbc);
                        // Move to next row
                        gbc.gridy++;
                    }
                    
                    
                } else {
                    for (String data : map.get(selectedIndex)) {
                        // Add Label
                        JLabel label = new JLabel(data);
                        gbc.gridx = 0;
                        gbc.weightx = 0.2;
                        gbc.fill = GridBagConstraints.NONE; // Do not stretch the label
                        panelFormulario.add(label, gbc);

                        // Add Text Area
                        JTextArea textArea = new JTextArea(1, 20);  // Fixed rows to 1, columns to 20
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        gbc.gridx = 1;
                        gbc.weightx = 0.8;
                        gbc.fill = GridBagConstraints.HORIZONTAL; // Stretch the text area horizontally
                        Texts.add(textArea);
                        panelFormulario.add(textArea, gbc);

                        // Move to next row
                        gbc.gridy++;
                    }
                }
            } else {
                // Use JTextArea for wrapping text
                if(!comboBoxSelected.equals("UNKNOWN")) {
                    JTextArea emptyArea = new JTextArea("No parameters required");
                    emptyArea.setEditable(false); // Make it non-editable
                    emptyArea.setFocusable(false); // Make it non-focusable
                    emptyArea.setLineWrap(true); // Enable line wrapping
                    emptyArea.setWrapStyleWord(true); // Wrap at word boundaries
                    emptyArea.setBackground(panelFormulario.getBackground()); // Match the panel's background
                    emptyArea.setBorder(null); // Remove border to make it look like a label

                    gbc.gridx = 0;
                    gbc.gridwidth = 3; // Span across all columns
                    gbc.weightx = 1.0; // Take full width
                    gbc.fill = GridBagConstraints.HORIZONTAL; // Ensure it fills the space horizontally
                    panelFormulario.add(emptyArea, gbc);
                    gbc.gridy++;
                    gbc.gridwidth = 1; // Reset gridwidth for subsequent components
                }
            }

            panelFormulario.revalidate();
            panelFormulario.repaint();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(panelFormulario, "Invalid selection!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



}
