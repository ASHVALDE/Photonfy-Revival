package org.example;

import javax.microedition.io.StreamConnection;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Stack;

public class dataReceiver extends Thread{
    StreamConnection streamConnection;
    JTextPane textPane2;
    static Stack<Short> dataPhotos = new Stack<Short>();
    static Stack<Float> dataTransferFunction = new Stack<Float>();
    static Stack<Integer> dataFlickering = new Stack<Integer>();

    public dataReceiver(StreamConnection connection, JTextPane texto) throws IOException {
        this.streamConnection = connection;
        this.textPane2 = texto;
    }

    public void run(){
        InputStream inputStream = null;
        try {
            inputStream = streamConnection.openInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Recibimos una lectura
        boolean leyendo = true;
        var i = new Stack<Integer>();
        var bytes = new Stack<Byte>();
        while(true){
            while(leyendo) {

                while (true) {
                    try {
                        if (inputStream.available() == 0) break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    final int data;
                    try {
                        data = inputStream.read();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    bytes.add((byte) data);
                    i.add(data);
                    try {
                        if (inputStream.available() == 0) {

                            leyendo = false;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            System.out.println("finished bytes");

            dataPackage x = null;
            try {
                x = new dataPackage().fromBytes(bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if( x.function== 225){
                if(x.sequence==0){
                    try {
                        dataPhotos = x.unpackPayload();
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(x.sequence!=0){
                    try {
                        dataPhotos.addAll(x.unpackPayload());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
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

                }
            }else{
                try {
                    x.unpackPayload();
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }

            if(x.UnpackedResponse!= null){
                textPane2.setText(x.UnpackedResponse);
            }

            bytes = new Stack<Byte>();
            i = new Stack<Integer>();
            leyendo = true;
    }
}
}
