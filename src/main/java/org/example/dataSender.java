package org.example;

import com.google.common.primitives.Bytes;

import javax.microedition.io.StreamConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import static org.example.Constants.Commands;
import static org.example.Main.lastMessageID;

public class dataSender {
    StreamConnection streamConnection;
    DataOutputStream dataOutput;
    dataSender(StreamConnection connection) throws IOException {
        this.streamConnection = connection;
        dataOutput = streamConnection.openDataOutputStream();
    }

    public void sendCommand(int command) throws IOException {
        dataPackage frame = new dataPackage();
        frame.function=command;
        lastMessageID = command;
        dataOutput.write(Bytes.toArray(frame.toBytes()));

    }
    public void sendCommand(dataPackage frame) throws IOException {
        dataOutput.write(Bytes.toArray(frame.toBytes()));
    }

    public void SET_INTEGRATION_TIME(short integration_time) throws IOException {
        // Creamos un payload
        List<Byte> payload = new Stack<Byte>();
        // Creamos los bytes para mandarlos en el payload
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(integration_time);
        payload.add(buffer.get(0));
        payload.add(buffer.get(1));
        dataPackage newCommand = new dataPackage();
        newCommand.function = Commands.get("SET_INTEGRATION_TIME");
        newCommand.length = payload.size();
        newCommand.payload = payload;

        // Mandamos el paquete de informacion
        try {
            sendCommand(newCommand);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void SET_GAIN(byte activated) throws IOException {
        // Creamos un payload
        List<Byte> payload = new Stack<Byte>();
        payload.add(activated);

        dataPackage newCommand = new dataPackage();
        newCommand.function = Commands.get("SET_GAIN");
        newCommand.length = payload.size();
        newCommand.payload = payload;

        // Mandamos el paquete de informacion
        try {
            sendCommand(newCommand);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void SET_WAVELENGTH_CALIBRATION(Stack<Float> Coeficientes) throws IOException {
        // Creamos un payload
        List<Byte> payload = new Stack<Byte>();
        for (int i = 0; i < Coeficientes.size(); i++) {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.putFloat(Coeficientes.get(i));
            payload.add(buffer.get(0));
            payload.add(buffer.get(1));
            payload.add(buffer.get(2));
            payload.add(buffer.get(3));
        }
        dataPackage newCommand = new dataPackage();
        newCommand.function = Commands.get("SET_WAVELENGTH_CALIBRATION");
        newCommand.length = payload.size();
        newCommand.payload = payload;

        // Mandamos el paquete de informacion
        try {
            sendCommand(newCommand);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void SET_LUX_CALIBRATION(float coeficientes){
        // Creamos un payload
        List<Byte> payload = new Stack<Byte>();
        ByteBuffer buffer = ByteBuffer.allocate(4).putFloat(coeficientes);

        payload.add(buffer.get(0));
        payload.add(buffer.get(1));
        payload.add(buffer.get(2));
        payload.add(buffer.get(3));

        dataPackage newCommand = new dataPackage();
        newCommand.function = Commands.get("SET_LUX_CALIBRATION");
        newCommand.length = payload.size();
        newCommand.payload = payload;

        // Mandamos el paquete de informacion
        try {
            sendCommand(newCommand);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void SET_DEFAULT_LUX_CALIBRATION(float coeficientes){
        // Creamos un payload
        List<Byte> payload = new Stack<Byte>();
        ByteBuffer buffer = ByteBuffer.allocate(4).putFloat(coeficientes);
        payload.add(buffer.get(0));
        payload.add(buffer.get(1));
        payload.add(buffer.get(2));
        payload.add(buffer.get(3));

        dataPackage newCommand = new dataPackage();
        newCommand.function = Commands.get("SET_DEFAULT_LUX_CALIBRATION");
        newCommand.length = payload.size();
        newCommand.payload = payload;

        // Mandamos el paquete de informacion
        try {
            sendCommand(newCommand);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void SET_VIDEO_SAMPLE_RATE(float coeficientes){
        // Creamos un payload
        List<Byte> payload = new Stack<Byte>();
        ByteBuffer buffer = ByteBuffer.allocate(4).putFloat(coeficientes);
        payload.add(buffer.get(0));
        payload.add(buffer.get(1));
        payload.add(buffer.get(2));
        payload.add(buffer.get(3));

        dataPackage newCommand = new dataPackage();
        newCommand.function = Commands.get("SET_VIDEO_SAMPLE_RATE");
        newCommand.length = payload.size();
        newCommand.payload = payload;

        // Mandamos el paquete de informacion
        try {
            sendCommand(newCommand);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void SET_TIME(String Fecha,String Hora){
        dataPackage newCommand = new dataPackage();
        newCommand.function = Commands.get("SET_TIME");
        Stack<Byte> payload = new Stack<Byte>();
        String[] Fechar =Fecha.split("/");
        Fechar[2] = Fechar[2].substring(Math.max(Fechar[2].length() - 2, 0));
        String[] HorasChar = Hora.split(":");
        byte bufferDia = Byte.parseByte(Fechar[0]);
        byte bufferMes = Byte.parseByte(Fechar[1]);
        byte bufferAnyo = Byte.parseByte(Fechar[2]);
        byte bufferHora = Byte.parseByte(HorasChar[0]);
        byte bufferMinuto = Byte.parseByte(HorasChar[1]);
        byte bufferSegundo = Byte.parseByte(HorasChar[2]);
        Collections.addAll(payload, new  Byte[]{bufferHora, bufferMinuto, bufferSegundo, bufferDia, bufferMes,bufferAnyo});
        newCommand.length = payload.size();
        newCommand.payload = payload;
        // Mandamos el paquete de informacion
        try {
            sendCommand(newCommand);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void SET_BLUETOOTH_NAME(String bluetooth_name){

        dataPackage newCommand = new dataPackage();
        newCommand.function = Commands.get("SET_BLUETOOTH_NAME");
        Stack<Byte> payload = new Stack<Byte>();
        char[] strArray =  bluetooth_name.toCharArray();
        for (int i = 0; i < strArray.length; i++) {
            ByteBuffer buffer = ByteBuffer.allocate(2);
            buffer.putChar(strArray[i]);
            payload.add(buffer.get(1));
        }
        ByteBuffer Endbuffer = ByteBuffer.allocate(2);
        Endbuffer.putChar('\0');
        payload.add(Endbuffer.get(1));

        newCommand.length = payload.size();
        newCommand.payload = payload;

        // Mandamos el paquete de informacion
        try {
            sendCommand(newCommand);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }
    public void SET_BACKGROUND(Short Count, Short Integration, Float Temperature){
        dataPackage newCommand = new dataPackage();
        newCommand.function = Commands.get("SET_BACKGROUND");
        Stack<Byte> payload = new Stack<Byte>();
        byte[] countB = ByteBuffer.allocate(2).putShort(Count).array();
        Collections.addAll(payload, new  Byte[]{countB[0], countB[1]});
        byte[] integrationB = ByteBuffer.allocate(2).putShort(Integration).array();
        Collections.addAll(payload, new  Byte[]{integrationB[0], integrationB[1]});
        byte[] TemperatureB = ByteBuffer.allocate(4).putFloat(Temperature).array();
        Collections.addAll(payload, new  Byte[]{TemperatureB[0], TemperatureB[1], TemperatureB[2], TemperatureB[3]});
        newCommand.length = payload.size();
        newCommand.payload = payload;
        // Mandamos el paquete de informacion
        try {
            sendCommand(newCommand);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void SET_DEFAULT_BACKGROUND(Short Count, Short Integration, Float Temperature){
        dataPackage newCommand = new dataPackage();
        newCommand.function = Commands.get("SET_DEFAULT_BACKGROUND");
        Stack<Byte> payload = new Stack<Byte>();
        byte[] countB = ByteBuffer.allocate(2).putShort(Count).array();
        Collections.addAll(payload, new  Byte[]{countB[0], countB[1]});
        byte[] integrationB = ByteBuffer.allocate(2).putShort(Integration).array();
        Collections.addAll(payload, new  Byte[]{integrationB[0], integrationB[1]});
        byte[] TemperatureB = ByteBuffer.allocate(4).putFloat(Temperature).array();
        Collections.addAll(payload, new  Byte[]{TemperatureB[0], TemperatureB[1], TemperatureB[2], TemperatureB[3]});
        newCommand.length = payload.size();
        newCommand.payload = payload;
        // Mandamos el paquete de informacion
        try {
            sendCommand(newCommand);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
