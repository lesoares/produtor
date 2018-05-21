package pConsumidor;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Cliente {
    private Cliente() {
    }

    public static void main(String[] args) {
        String host = (args.length < 2) ? null : args[1];
        String fileName = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            Impressao stub = (Impressao) registry.lookup("Impressao");

            boolean respostastub;

            for (int i = 0; i < 5; i++) {
                do {
                    TimeUnit.MILLISECONDS.sleep(537);
                    respostastub = stub.solicitarImpressao(new File(fileName));
                } while (!respostastub);
                System.out.println("Imprimiu");
                respostastub = false;
            }

            System.out.println("Cliente finalizado");
            stub.terminarSessao();
        } catch (Exception e) // Esse catch trata os RemoteException caso ocorra algum
        {
            System.err.println("Capturando a exceção no Cliente: " + e.toString());
            e.printStackTrace();
        }
    }
}