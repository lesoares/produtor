package pConsumidor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import static javafx.application.Platform.exit;

public class Servidor implements Impressao {
    private List<File> buffer;
    private int cursor;
    private int produtoresAtivos;
    private Semaphore mutex;

    public Servidor() {
        buffer = new ArrayList<>();
        cursor = 0;
        produtoresAtivos = 5;
    }


    public List<File> getBuffer() {
        return buffer;
    }

    public Semaphore getMutex() {
        return mutex;
    }


    public boolean bufferCheio() {
        return buffer.size() == 3;
    }


    /**
     * Método chamado pelo cliente pra inserir um arquivo na fila de impressão
     *
     * @param arquivo
     */
    public boolean solicitarImpressao(File arquivo) throws IOException, InterruptedException {
        boolean retorno = false;
        if (mutex.tryAcquire()) {

            if (bufferCheio())
                retorno = false;

            else {
                buffer.add(arquivo);
                cursor++;
                if (cursor > 2)
                    cursor = 0;
                retorno = true;
            }

            mutex.release();
        }
        return retorno;
    }


    public void terminarSessao(){
        produtoresAtivos--;
        if(produtoresAtivos == 0)
            System.exit(0);
    }


    /**
     * Tarefas:
     * Criar buffer
     * Criar método de impressão
     * <p>
     * Criar função de solicitar impressão pelo cliente
     * <p>
     * Criar semáforos
     * Criar threads
     * testar funções
     * <p>
     * Criar classe do cliente
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

            obj.mutex = new Semaphore(1, true);

            Impressora impressora1 = new Impressora(new File("imp1.txt"),obj);
            new Thread(impressora1).start();

            System.out.println("Servidor pronto!");
        } catch (Exception e) {
            System.err.println("Capturando exceção no Servidor: " + e.toString());
            e.printStackTrace();
        }
    }
}
