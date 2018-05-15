package pConsumidor;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Cliente
{
    private Cliente()
	{}

    public static void main(String[] args) 
	{
        String host = (args.length < 1) ? null : args[0];
        try 
		{
			// Passo importante 1: Obter o registro
			// Esse carinha abaixo, representa aquele RmiRegistry que a gente executa por fora
            Registry registry = LocateRegistry.getRegistry(host);
			// Obs.: 
			//  Stub é uma nomenclatura associada ao padrão de projeto Proxy
			//  Para maiores informações procurar o livro Use a Cabeça Padrões de Projetos
			//  também conhecido por Head First Design Patterns
			// Em suma, é o objeto utilizado que efetua a conversa entre a aplicação Cliente e a aplicação Servidora
            // Repare no cast efetuado, sabemos que o tipo do objeto é Impressao
			// Relembre que Impressao é uma interface. Mas é uma interface remota pois estende a interface java.rmi.Remote
			// Passo importante 2: Pesquisar pelo objeto remoto conhecido pelo nome esperado.
			Impressao stub = (Impressao) registry.lookup("Impressao");
			// Aqui a gente executa o método remoto
			// Passo importante 3: Chamar o método remoto.
   //         String resposta = stub.digaAloMundo();
			// Aqui a gente exibe o fruto produzido por esse lindo e maravilho sistema distribuído
			// Passo importante 4: Verificar resposta dada pelo método remoto.

            stub.solicitarImpressao(new File("teste.txt"));
            stub.solicitarImpressao(new File("teste.txt"));
            stub.solicitarImpressao(new File("teste.txt"));
            String respostastub = stub.solicitarImpressao(new File("teste.txt"));
            System.out.println("resposta: " + respostastub);
        } 
		catch (Exception e) // Esse catch trata os RemoteException caso ocorra algum
		{
            System.err.println("Capturando a exceção no Cliente: " + e.toString());
            e.printStackTrace();
        }
    }	
}