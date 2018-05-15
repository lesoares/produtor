package pConsumidor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class Servidor implements Impressao {
    private File[] buffer;
    private int cursor;
    private int ocupados;
    private File impressora1;
    private File impressora2;

    public Servidor() {
        buffer = new File[3];
        cursor = 0;
    }


    /**
     * Método chamado pelo cliente pra inserir um arquivo na fila de impressão
     * @param arquivo
     */
    public String solicitarImpressao(File arquivo) throws IOException{
        if(ocupados >= 2)
            return "Fila de impressão cheia. Tente novamente mais tarde.";
        buffer[cursor] = arquivo;
        ocupados++;
        imprime(impressora1);

        return "Impresso com sucesso";
    }

    public void imprime(File impressora) throws IOException {
        FileWriter saida = new FileWriter(impressora, true);
        Scanner leArquivo = new Scanner(buffer[cursor]);

        while(leArquivo.hasNextLine()) {
            saida.write(leArquivo.nextLine());
            saida.write("\n");
        }
        buffer[cursor] = null;
        ocupados--;
        cursor++;
        if(cursor >= 2)
            cursor = 0;
        saida.close();
    }

    /**
     * Tarefas:
     * Criar buffer
     * Criar método de impressão
     *
     * Criar função de solicitar impressão pelo cliente
     *
     * Criar semáforos
     * Criar threads
     * testar funções
     *
     * Criar classe do cliente
     *
     *
     * @param args
     */
    public static void main(String args[]) {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            Servidor obj = new Servidor();
            Impressao stub = (Impressao) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.getRegistry();

            registry.bind("Impressao", stub);
            obj.impressora1 = new File("imp1.txt");

     //       obj.buffer[obj.cursor] = new File("teste.txt");

     //       obj.imprime(obj.impressora1);

            System.out.println("Servidor pronto!");
        } catch (Exception e) {
            System.err.println("Capturando exceção no Servidor: " + e.toString());
            e.printStackTrace();
        }
    }
}
