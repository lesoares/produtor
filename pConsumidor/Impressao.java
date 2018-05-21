package pConsumidor;

import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

// Ponto importante 1: estende a interface java.rmi.Remote
public interface Impressao extends Remote
{
    //Chamada do processo de impressão
    boolean solicitarImpressao(File arquivo) throws RemoteException;

    void terminarSessao() throws RemoteException;
}