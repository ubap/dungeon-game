import dunegon.io.ThingTypeManager;
import dunegon.game.Game;
import dunegon.game.login.CharList;
import dunegon.io.SpriteManager;
import dunegon.net.Protocol;
import dunegon.net.ProtocolGame;
import dunegon.net.ProtocolLogin;
import utils.MapPrinter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        System.out.println("starting");

        Game.init();
        ThingTypeManager.init();
        SpriteManager.init();



        final ClassLoader loader = Main.class.getClassLoader();
        URL datUrl = loader.getResource("Tibia1098.dat");
        URL sprUrl = loader.getResource("Tibia.spr");
        ThingTypeManager.getInstance().loadDat(datUrl.toURI());
        SpriteManager.getInstance().loadSpr(sprUrl.toURI());
        CharList charList = new CharList();

        Protocol protocol = new ProtocolLogin(charList,"1", "1");
        protocol.connect("127.0.0.1", 7171);

        synchronized(charList) {
            charList.wait();
        }

        ProtocolGame protocolGame = new ProtocolGame("1", "1", "Heh");
        protocolGame.connect("127.0.0.1", 7172);

        Thread.sleep(2000);

        while (true) {
            // MapPrinter.printThingsUnder(Game.getInstance(), protocolGame);
            Thread.sleep(500);
        }

    }
}
