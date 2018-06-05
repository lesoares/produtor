package pConsumidor;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class Impressora implements Runnable{
    File saida;
    Servidor servidor;

    public static int tempoEspera = 1;

    public Impressora(File saida, Servidor servidor) {
        this.saida = saida;
        this.servidor = servidor;
    }


    public void run() {
       do {
           try {
               //Tenta obter o lock do buffer
               if (servidor.getMutex().tryAcquire()) {
                   //Se existe algo no buffer a ser impresso, imprime
                   if (servidor.getBuffer().size() > 0) {
                       FileWriter saida = new FileWriter(this.saida, true);

                       //obtém o arquivo do buffer
                       File proximo = servidor.getBuffer().get(0);
                       servidor.getBuffer().remove(0);

                       //Libera o buffer
                       servidor.getMutex().release();

                       Scanner leArquivo = new Scanner(proximo);
                       System.out.println("Imprimido arquivo " +proximo.getName()+ " em impressora "+this.saida.getName());

                       //lê o arquivo e escreve na impressora, linha a linha
                       while (leArquivo.hasNextLine()) {
                           saida.write(leArquivo.nextLine());
                           saida.write("\n");
                       }
                       //Para separar da próxima impressão
                       saida.write("\n");
                       TimeUnit.SECONDS.sleep(tempoEspera);
                       System.out.println("Arquivo "+proximo.getName()+ " impresso com sucesso.");

                       //Fecha os arquivos
                       leArquivo.close();
                       saida.close();

                   } else {
                       //Se não tem nada no buffer, libera o lock
                       servidor.getMutex().release();
                   }
               }
           }
           catch (Exception e){
               System.out.println("Impressão falhou, um erro inesperado ocorreu.");
           }

       //Enquanto ainda existe cliente
       } while(!servidor.isAcaba());
    }
}