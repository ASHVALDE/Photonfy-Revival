package org.example;
import java.io.IOException;

public class Main {
    public static int lastMessageID = 0;
    public static void main(String[] args) throws IOException, InterruptedException {
        Constants constantes = new Constants();

        constantes.initConst();

        new PantallaPrincipal(constantes);

    }
}
