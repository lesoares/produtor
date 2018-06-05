package pConsumidor;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class Cliente {
    private Cliente() {
    }
    public static int numeroArquivos = 5;
    public static int tempoEspera = 1;

    public static void main(String[] args) {
        String host = (args.length < 2) ? null : args[1];
        String fileName = (args.length < 1) ? null : args[0];

        try {
            Registry registry = LocateRegistry.getRegistry(host);
            Impressao stub = (Impressao) registry.lookup("Impressao");
            boolean respostastub;

            for (int i = 0; i < numeroArquivos; i++) {
                    do {
                    TimeUnit.SECONDS.sleep(tempoEspera);
                    respostastub = stub.solicitarImpressao(new File(fileName));
                } while (!respostastub);
                System.out.println("Arquivo "+fileName+ " impresso com sucesso.");
                respostastub = false;
            }

            System.out.println("Cliente finalizado");
            stub.terminarSessao();
        } catch (Exception e) // Esse catch trata os RemoteException caso ocorra algum
        {
            System.out.println("Não foi possível imprimir pois houve uma exceção. Tente novamente mais tarde.");
            System.err.println("Capturando a exceção no Cliente: " + e.toString());
            e.printStackTrace();
        }
    }
}