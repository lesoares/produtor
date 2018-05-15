@ECHO OFF
ECHO LEMBRE-SE de alterar o caminho de diretorio para o JAVA HOME na sua maquina!
"C:\Program Files\Java\jdk1.8.0_131\bin\rmiregistry.exe" -J-Djava.rmi.server.useCodebaseOnly=file:bin/
PAUSE