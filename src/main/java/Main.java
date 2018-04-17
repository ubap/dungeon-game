import dunegon.game.ThingTypeManager;
import dunegon.game.Game;
import dunegon.game.login.CharList;
import dunegon.net.Protocol;
import dunegon.net.ProtocolGame;
import dunegon.net.ProtocolLogin;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        System.out.println("starting");

        Game.init();
        ThingTypeManager.init();

        final ClassLoader loader = Main.class.getClassLoader();
        URL resourceUrl = loader.getResource("Tibia1098.dat");
        ThingTypeManager.getInstance().loadDat(resourceUrl.toURI());

        CharList charList = new CharList();

        Protocol protocol = new ProtocolLogin(charList,"1", "1");
        protocol.connect("127.0.0.1", 7171);

        synchronized(charList) {
            charList.wait();
        }

        Protocol protocol2 = new ProtocolGame("1", "1", "Heh");
        protocol2.connect("127.0.0.1", 7172);

        while (true) {
            Thread.sleep(1000);
        }

    }
}
