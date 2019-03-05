package ru.bvpotapenko.se.chat2.chatui.network;

import ru.bvpotapenko.se.chat2.chatui.Main;
import ru.bvpotapenko.se.chat2.utils.Properties;

import java.util.concurrent.Callable;

public class ClientConnectionService implements Callable<Client> {
    private Client client;
    private Main mainApp;

    public ClientConnectionService(Main mainApp) {
        this.mainApp = mainApp;
    }

    private void setUpClient(){
        client = new Client(Properties.HOST, Properties.PORT, mainApp);
        client.setOutPrintStream(System.out);
    }

    public Client getClient() {
        if(client == null) setUpClient();
        return client;
    }
    public boolean isClientReady(){
        return client!=null;
    }

    @Override
    public Client call() {
        while (client == null || client.getSocketState()!= ClientState.CONNECTED){
            setUpClient();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return client;
    }
}
