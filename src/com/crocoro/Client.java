package com.crocoro;

import java.io.IOException;
import java.net.Socket;

/**
 * 与服务器通信的类
 */
public class Client {
    private Client client = null;
    private Socket sock;

    private Client() throws IOException {
        sock = new Socket(Config.server, Config.serverPort);
    }

    public Client getInstance() throws IOException {
        if (client == null) {
            client = new Client();
        }
        return client;
    }

    public void sendInfo(String info) {

    }
}
