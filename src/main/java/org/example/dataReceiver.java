package org.example;

import javax.microedition.io.StreamConnection;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        Stack<Integer> i = new Stack<Integer>();
        Stack<Byte> bytes = new Stack<Byte>();
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
                if(x.sequence!=0) {
                    try {
                        dataPhotos.addAll(x.unpackPayload());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(dataPhotos.size()>=255){

                    // Crear Carpeta
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
                    String timestamp = LocalDateTime.now().format(formatter);
                    File folder = new File(timestamp);
                    if (folder.mkdir()) {
                        System.out.println("Carpeta creada: " + folder.getAbsolutePath());
                    } else {
                        System.err.println("Error al crear la carpeta.");
                        return;
                    }
                    String csvFilePath = timestamp + File.separator + "data.csv";

                    // Crear y escribir en el archivo CSV
                    StringBuilder csvBuilder = new StringBuilder();

                    try (FileWriter writer = new FileWriter(csvFilePath)) {
                        for(short data : dataPhotos){
                            csvBuilder.append(Short.toString(data) + ",");
                        }
                        if (!dataPhotos.isEmpty()) {
                            csvBuilder.setLength(csvBuilder.length() - 1);
                        }
                        writer.append(csvBuilder.toString());


                        System.out.println("Archivo CSV creado en: " + csvFilePath);
                    } catch (IOException e) {
                        System.err.println("Error al escribir en el archivo CSV: " + e.getMessage());
                    }



                    String templatePath = "basic_html_template.html";

                    // Ruta donde guardaremos el archivo HTML generado
                    String outputPath = timestamp + File.separator + "output.html";


                    // Variables para reemplazar en la plantilla
                    String title = "Mi Página Web";
                    String header = "¡Bienvenido a mi página!";
                    String paragraph = "Este es un ejemplo de cómo usar plantillas HTML con Java.";

                    try {
                        // Leer la plantilla HTML
                        String htmlTemplate = new String(Files.readAllBytes(Paths.get(templatePath)));

                        // Reemplazar las variables {{variable}} con valores dinámicos
                        String htmlContent = htmlTemplate
                                .replace("{{datos}}", csvBuilder.toString());
                        // Guardar el contenido generado en un archivo HTML
                        Files.write(Paths.get(outputPath), htmlContent.getBytes(), StandardOpenOption.CREATE);

                        System.out.println("Archivo HTML generado: " + outputPath);
                    } catch (IOException e) {
                        System.err.println("Ocurrió un error: " + e.getMessage());
                    }




                    try {
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(folder);
                        System.out.println("Carpeta abierta en el explorador de archivos.");
                    } catch (IOException e) {
                        System.err.println("Error al intentar abrir la carpeta: " + e.getMessage());
                    } catch (UnsupportedOperationException e) {
                        System.err.println("La acción de abrir carpetas no está soportada en este sistema.");
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
