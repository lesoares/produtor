package pConsumidor;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Servidor implements Impressao {

//    public int totalClientes;

    private List<File> buffer;
    private int produtoresAtivos;
    private boolean acaba;
    private Semaphore mutex;

    /**
     * Inicializa o buffer vazio; e
     * Inicializa o total de produtores possíveis (5)
     */
    public Servidor(int totalClientes) {
        buffer = new ArrayList<>();
        produtoresAtivos = totalClientes;
        System.out.println("Total ativos "+produtoresAtivos);
    }


    /**
     * Obtém o buffer contendo os arquivos em ordem de impressão
     *
     * @return Buffer de impressão
     */
    public List<File> getBuffer() {
        return buffer;
    }

    /**
     * Obtém o semáforo de controle de acesso ao buffer
     *
     * @return Semáforo de acesso ao buffer
     */
    public Semaphore getMutex() {
        return mutex;
    }

    /**
     * Obtém a flag de finalizar a thread
     *
     * @return Se o servidor já pode acabar
     */
    public boolean isAcaba() {
        return acaba;
    }


    /**
     * Função que verifica se o buffer está cheio, ou seja, as 3 posições foram ocupadam.
     */
    public boolean bufferCheio() {
        return buffer.size() == 3;
    }


    /**
     * Método chamado pelo cliente pra inserir um arquivo na fila de impressão
     *
     * @param arquivo Arquivo a ser impresso
     */
    public boolean solicitarImpressao(File arquivo) throws RemoteException {
        boolean retorno = false;

        //obtém o lock
        if (mutex.tryAcquire()) {
            //Se buffer já está cheio, retorna falha ao cliente.
            if (bufferCheio()) {
                retorno = false;
                System.out.println("Não é possível imprimir esse arquivo (" +arquivo.getName()+") agora, pois o buffer está cheio. Tente novamente mais tarde.");
            } else {
                System.out.println("Adicionando na fila o arquivo " + arquivo.getName());
                buffer.add(arquivo);
                retorno = true;
            }
            //libera o lock
            mutex.release();

        }
        return retorno;
    }


    /**
     * Cliente chama para sinalizar que vai se desconectar do servidor.
     * Quando todos os 5 clientes se desconectam, ou seja, a função é chamada 5 vezes,
     * A flag de terminar é alterada para verdadeiro.
     */
    public void terminarSessao() {
        System.out.println("Total ativos "+produtoresAtivos);
        produtoresAtivos--;
        if (produtoresAtivos <= 0)
            acaba = true;
    }


    /**
     * Inicializa o RMI, semáforo e threads.
     *
     * @param args
     */
    public static void main(String args[]) {
        try {
            //inicializa o RMI
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            Servidor obj = new Servidor(Integer.valueOf(args[0]));

            Impressao stub = (Impressao) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.getRegistry();

            registry.bind("Impressao", stub);


            //cria locks e threads
            obj.mutex = new Semaphore(1, true);

            Impressora impressora1 = new Impressora(new File("imp1.txt"), obj);
            Impressora impressora2 = new Impressora(new File("imp2.txt"), obj);
            Thread i1 = new Thread(impressora1);
            Thread i2 = new Thread(impressora2);

            System.out.println("Servidor pronto!");
            i1.start();
            i2.start();

            //Junta tudo e acaba

            i1.join();
            i2.join();

            System.out.println("Servidor fechado.");
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Capturando exceção no Servidor: " + e.toString());
            e.printStackTrace();
        }
    }
}
