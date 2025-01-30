package org.example;

import com.google.common.primitives.Bytes;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Stack;

public class dataUnpacker {
    public static String getDeviceState(List<Byte> payload) {
        // 1 byte de info de la carga de bateria
        int Bateria = payload.get(0)& 0xFF;
        byte[] x = {payload.get(1),payload.get(2),payload.get(3),payload.get(4)};
        // 1 Float con la temperatura del dispositivo
        float temperature =  ByteBuffer.wrap(x).getFloat();
        return "Bateria: "+Bateria+" || Temperatura: "+ temperature;
    }

    public static String GET_GAIN(List<Byte> payload){
        int GAIN = payload.get(0)& 0xFF;
        if(GAIN==1){
            return "Gain Activado";
        }else{
            return "Gain Desactivado";
        }
    }

    public static String getDeviceInfo(List<Byte> payload) {
        String serial = "";
        System.out.println(payload.size());
        for (int i = 0; i < 24; i++) {
            serial += (payload.get(i)& 0xFF)+"\n";
        }

        return "Serial: "+serial +"\n";
    }

    public static Stack<Float> unpackTransferFunction(List<Byte> payload,int sequence){
        Stack<Float> UnpackedData = new Stack<Float>();
        for (int i = 0; i < payload.size(); i=i+4) {
            byte[] x = {payload.get(i),payload.get(i+1),payload.get(i+2),payload.get(i+3)};
            UnpackedData.add(ByteBuffer.wrap(x).getFloat());

        }
        return UnpackedData;
    }

    public static Stack<Short> unpackPhoto(List<Byte> payload,int sequence){
        if(sequence!=0){
            Stack<Short> UnpackedData = new Stack<Short>();

            for (int i = 0; i < payload.size()/2; i++) {
                int num1 = i*2;
                int num2 = (i*2)+1;
                short reading = ByteBuffer.wrap(new byte[]{payload.get(num2), payload.get(num1)}).getShort();
                UnpackedData.add(reading);
            }

            return UnpackedData;

        }
        if(sequence==0){
            // Ponemos al final 0xFF porque por algun motivo java se fuma y deja los bytes sin signo con signo XD
            int startCapture = payload.get(0)& 0xFF;
            int hour = payload.get(1)& 0xFF;
            int minute = payload.get(2)& 0xFF;
            int seconds = payload.get(3)& 0xFF;
            int day = payload.get(4)& 0xFF;
            int month = payload.get(5)& 0xFF;
            int year = payload.get(6)& 0xFF;

            // Para poder crear los short tenemos que combinar dos bytes (Los shorts ocupan 16 bits o 2 bytes)
            short integration_time = (short) ((payload.get(7) << 8) | (payload.get(8) & 0xFF));

            // Para crear un float es peor porque tenemos 4 bits y toca meterlos a un bytebuffer para convertir la logica
            byte[] x = {payload.get(9),payload.get(10),payload.get(11),payload.get(12)};
            float temperature =  ByteBuffer.wrap(x).getFloat();

            List<Byte> preUnpackedData =  payload.subList(13,payload.size());

            Stack<Short> UnpackedData = new Stack<Short>();
            // Formamos pares de bytes para formar los shorts que componen las lecturas
            for (int i = 0; i < preUnpackedData.size()/2; i++) {
                int num1 = i*2;
                int num2 = (i*2)+1;
                short reading = (short) ((preUnpackedData.get(num1) << 8) | (preUnpackedData.get(num2) & 0xFF));
                UnpackedData.add(reading);
            }
            return UnpackedData;
        }
        return null;
    }

    public static void unpackDefault(List<Byte> payload,int function){
        byte[] damn = Bytes.toArray(payload);
        System.out.println("Event #"+function+" not implemented... anyways, here is the response");
        for (byte b : damn) {
            System.out.print(b & 0xFF);
            System.out.print("-");
        }
        System.out.println();
    }

    public static String getIntegrationTime(List<Byte> payload){
        byte[] x = {payload.get(0),payload.get(1)};
        float Integration =  ByteBuffer.wrap(x).getShort();
        return "Integration time: "+Integration;
    }

    public static String getLuxCalibration(List<Byte> payload){
        byte[] x = {payload.get(0),payload.get(1),payload.get(2),payload.get(3)};
        float Lux =  ByteBuffer.wrap(x).getFloat();
        return "Lux Coeficiente: "+Lux;

    }

    public static Stack<Float> GET_WAVELENGTH_CALIBRATION(List<Byte> payload){
        Stack<Float> UnpackedData = new Stack<Float>();
        for (int i = 0; i < payload.size(); i=i+4) {
            byte[] x = {payload.get(i),payload.get(i+1),payload.get(i+2),payload.get(i+3)};
            UnpackedData.add(ByteBuffer.wrap(x).getFloat());
        }
        return UnpackedData;
    }

    public static String GET_BACKGROUND_CALIBRATIONS(List<Byte> payload){
        String response = "";
        System.out.println(payload.size());
        short data =  ByteBuffer.wrap(new byte[]{payload.get(0), payload.get(1)}).getShort();
        response = response+"data: "+data+"\n";
        short integrationTime =  ByteBuffer.wrap(new byte[]{payload.get(2), payload.get(3)}).getShort();
        response = response+"integrationTime: "+integrationTime+"\n";
        float Temperature =  ByteBuffer.wrap(new byte[]{payload.get(4), payload.get(5), payload.get(6), payload.get(7)}).getFloat();
        response = response+"Temperature: "+Temperature+"\n";
        return response;
    }

    public static Stack<Integer> GET_FLICKERING(List<Byte> payload){
        Stack<Integer> UnpackedData = new Stack<Integer>();
        for (int i = 0; i < payload.size(); i=i+4) {
            byte[] x = {payload.get(i),payload.get(i+1),payload.get(i+2),payload.get(i+3)};
            UnpackedData.add(ByteBuffer.wrap(x).getInt());
        }
        return UnpackedData;
    }

    public static String GET_TIME(List<Byte> payload){
        int Hora = payload.get(0)& 0xFF;
        int Minuto = payload.get(1)& 0xFF;
        int Segundo = payload.get(2)& 0xFF;
        int Dia = payload.get(3)& 0xFF;
        int Mes = payload.get(4)& 0xFF;
        int Anyo = payload.get(5)& 0xFF;
        return Hora+":"+Minuto+":"+Segundo+"|| Fecha -> Dia:"+Dia+" Mes:"+Mes+" Año:"+Anyo;
    }
    public static String GET_VIDEO_SAMPLE_RATE(List<Byte> payload){
        float SampleRate= ByteBuffer.wrap(new byte[]{payload.get(0), payload.get(1), payload.get(2), payload.get(3)}).getFloat();
        return "SampleRate: "+SampleRate;
    }

    public static String GET_BACKGROUND_COEFFICIENTS(List<Byte> payload) {
        String response = "";
        Stack<Float> UnpackedData = new Stack<Float>();
        for (int i = 0; i < payload.size(); i=i+4) {
            byte[] x = {payload.get(i),payload.get(i+1),payload.get(i+2),payload.get(i+3)};
            UnpackedData.add(ByteBuffer.wrap(x).getFloat());
        }
        return UnpackedData.toString();
    }
}
