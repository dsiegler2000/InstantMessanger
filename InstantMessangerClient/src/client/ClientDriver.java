package client;

import javax.swing.JFrame;

public class ClientDriver {

    public static void main(String[] args) {

        Client client;
        client = new Client(Client.HOST);
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.startRunning();

    }

}
