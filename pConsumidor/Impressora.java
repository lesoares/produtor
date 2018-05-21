package pConsumidor;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Impressora implements Runnable{
    File saida;
    Servidor servidor;

    public Impressora(File saida, Servidor servidor) {
        this.saida = saida;
        this.servidor = servidor;
    }


    public void run() {
       do {
           try {
               if (servidor.getMutex().tryAcquire()) {
                   if (servidor.getBuffer().size() > 0) {
                       FileWriter saida = new FileWriter(this.saida, true);
                       Scanner leArquivo = new Scanner(servidor.getBuffer().get(0));
                       servidor.getBuffer().remove(0);

                       servidor.getMutex().release();

                       while (leArquivo.hasNextLine()) {
                           saida.write(leArquivo.nextLine());
                           saida.write("\n");
                       }
                       System.out.println("Imprimiu");
                       leArquivo.close();
                       saida.close();
                   } else {
                       servidor.getMutex().release();
                   }
               }
           }
           catch (Exception e){
               System.out.println("Impressão falhou, um erro inesperado ocorreu.");
           }
       } while(!servidor.isAcaba());
    }
}