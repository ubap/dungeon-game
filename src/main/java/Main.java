import dunegon.game.login.CharList;
import dunegon.net.Protocol;
import dunegon.net.ProtocolGame;
import dunegon.net.ProtocolLogin;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException{
        System.out.println("starting");


        CharList charList = new CharList();

        Protocol protocol = new ProtocolLogin(charList,"1", "1");
        protocol.connect("127.0.0.1", 7171);

        synchronized(charList) {
            charList.wait();
        }

        Protocol protocol2 = new ProtocolGame("1", "1", "Heh");
        protocol2.connect("127.0.0.1", 7172);

        while (true) {

        }

    }
}
