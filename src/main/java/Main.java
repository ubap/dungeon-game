import game.net.Protocol;
import game.net.ProtocolLogin;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("starting");


        Protocol protocol = new ProtocolLogin();
        protocol.connect("127.0.0.1", 7171);


        while (true) {

        }

    }
}
