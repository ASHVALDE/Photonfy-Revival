package org.example;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.example.Main.lastMessageID;
import static org.example.dataReceiver.*;

class dataPackage {

    // Propiedades de un datapackage
    int[] syncWord = new int[2]; // No fockin idea
    int flags; // No fockin idea
    int token; // No fockin idea
    int function; // Cada  funcion (obtener info,pedir una calibracion ETC) tiene un ID y esta ID dicta como se trata un datapackage
    short sequence; // Como no se pueden mandar datos muy grandes, se parten los packages y esa variable indica que parte del datapackage es este
    List<Byte> payload; // Los bytes como tal del datapackage
    int length; // El tamaño del payload
    String UnpackedResponse;




    // Consctructor en el caso de que si se tengan los datos
    dataPackage(int[] syncword,int flags,int token,int function,short sequence,List<Byte> payload,int length)
    {
        this.syncWord = syncword;
        this.flags = flags;
        this.token = token;
        this.function = function;
        this.sequence = sequence;
        this.payload = payload;
        this.length = length;

    }
    dataPackage()
    {
        this.syncWord = new int[]{85, 2};
        this.flags = 0;
        this.token = 0;
        this.function = 0;
        this.sequence = 0;
        this.payload = null;
        this.length = 0;

    }
    public dataPackage fromBytes(Stack<Byte> payload) throws IOException {
        // Cuando recibimos un dataPackage del espectrometro primero obtenemos el header que contiene los datos menos el payload que vamos a mantener en binario

        // Los dos primeros bits se asignan al syncword
        List<Byte> syncword = payload.subList(0, 2);
        // Ponemos al final 0xFF porque por algun motivo java se fuma y deja los bytes sin signo con signo XD
        int syncwordA= (syncword.get(0) & 0xFF);
        int syncwordB= (syncword.get(1) & 0xFF);
        this.syncWord = new int[]{syncwordA,syncwordB};

        // Seguimos asignando valores a nuestras variables con los bytes que recibimos
        this.flags = (payload.get(2)& 0xFF);
        this.token = (payload.get(3)& 0xFF);
        this.function = (payload.get(4) & 0xFF);

        // Este caso es especial porque tenemos que combinar dos bytes (8+8) para formar un short(16)
        this.sequence = (short) ((payload.get(5) << 8) | (payload.get(6) & 0xFF));

        // Seguimos asignando valores a nuestras variables con los bytes que recibimos
        this.length = (payload.get(7) & 0xFF);

        this.payload =  payload.subList(8,payload.size());
        return this;
    }
    public List<Byte> toBytes(){
        Stack<Byte> dataPack = new Stack<>();
        dataPack.add((byte) (this.syncWord[0]));
        dataPack.add((byte) (this.syncWord[1]));
        dataPack.add((byte) (this.flags));
        dataPack.add((byte) (this.token));
        dataPack.add((byte) (this.function));
        // Cambia pq es short
        byte[] sequence_byte=new byte[]{(byte)((this.sequence>>8)&0xFF),(byte)(this.sequence&0xFF)};
        dataPack.add(sequence_byte[0]);
        dataPack.add(sequence_byte[1]);
        dataPack.add((byte) (this.length));
        if(this.length  > 0){
            for (int i = 0; i < this.payload.size(); i++) {
                dataPack.add(this.payload.get(i));
            }
        }

        return dataPack;
    }



    public Stack<Short> unpackPayload() throws UnsupportedEncodingException {
        // Dependiendo de lo que envie el espectrometro tenemos que hacer diferentes acciones entonces:
        switch (this.function){
            case 161:
                this.UnpackedResponse ="La peticion se ha realizado correctamente";
                break;
            // 162 es un callback de una peticion que hallamos realizado
            case 162:
                // Dependiendo de lo ultimo que hallamos pedido, vamos a decodificar la informacion
                switch(lastMessageID){
                    case 2:
                        this.UnpackedResponse = dataUnpacker.getIntegrationTime(this.payload);
                        break;
                    case 4:
                        this.UnpackedResponse = dataUnpacker.GET_GAIN(this.payload);
                        break;
                    case 5:
                        if(this.sequence==0){
                            dataPhotos = dataUnpacker.unpackPhoto(this.payload,this.sequence);

                        }else{
                            dataPhotos.addAll(dataUnpacker.unpackPhoto(this.payload,this.sequence));
                        }
                        if(dataPhotos.size()==255){
                            try {
                                Process p = new ProcessBuilder("python", "FrameBuilder.py", dataPhotos.toString())
                                        .redirectErrorStream(true)
                                        .start();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return null;

                    case 9:
                    case 11:
                        if(this.sequence == 0) {
                            dataTransferFunction = dataUnpacker.unpackTransferFunction(this.payload, this.sequence);
                        }else{
                            dataTransferFunction.addAll(dataUnpacker.unpackTransferFunction(this.payload,this.sequence));
                        }
                        this.UnpackedResponse = "";
                        for (int i = 0; i < dataTransferFunction.size(); i++) {
                            this.UnpackedResponse = this.UnpackedResponse + i+" :"+dataTransferFunction.get(i).toString()+"\n";
                        }
                        break;

                    case 0x10:
                    case 0xE:
                        Stack<Float> datos = dataUnpacker.GET_WAVELENGTH_CALIBRATION(this.payload);
                        this.UnpackedResponse = "";
                        for (int i = 0; i < datos.size(); i++) {
                            this.UnpackedResponse = this.UnpackedResponse + i+" :"+datos.get(i).toString()+"\n";
                        }

                        break;
                    case 0x13:
                        this.UnpackedResponse = dataUnpacker.GET_BACKGROUND_CALIBRATIONS(this.payload);
                        break;
                    case 0x19:
                        case 0x27:
                        this.UnpackedResponse = dataUnpacker.getLuxCalibration(this.payload);
                        break;
                    case 0x1E:
                        this.UnpackedResponse = dataUnpacker.GET_VIDEO_SAMPLE_RATE(this.payload);

                        break;
                        case 33:
                        this.UnpackedResponse = dataUnpacker.getDeviceState(this.payload);
                        break;
                    case 35:
                        this.UnpackedResponse = dataUnpacker.getDeviceInfo(this.payload);
                        break;
                    case 0x20:
                        this.UnpackedResponse = dataUnpacker.GET_TIME(this.payload);
                        break;
                    case 0x2E:
                    case 0x2C:
                        this.UnpackedResponse = dataUnpacker.GET_BACKGROUND_COEFFICIENTS(this.payload);
                        break;
                    case 0x32:
                        System.out.println(this.sequence);
                        if(this.sequence==0){
                            dataFlickering = dataUnpacker.GET_FLICKERING(this.payload);
                        }else{
                            dataFlickering.addAll(dataUnpacker.GET_FLICKERING(this.payload));
                        }
                        this.UnpackedResponse = "";
                        for (int i = 0; i < dataFlickering.size(); i++) {
                            this.UnpackedResponse = this.UnpackedResponse + i+" :"+dataFlickering.get(i).toString()+"\n";
                        }
                        break;
                    default:

                        dataUnpacker.unpackDefault(this.payload,this.function);
                        break;
                }
                break;
            // Si manda una foto (o varias en secuencia las procesamos y mostramos en python)
            case 225:
                return dataUnpacker.unpackPhoto(this.payload,this.sequence);
            default: // Si no hay una respuesta programada solo botamos los bits
                dataUnpacker.unpackDefault(this.payload,this.function);
                break;
        }

        return null;


    }



}
