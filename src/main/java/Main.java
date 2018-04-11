import dunegon.game.ArrayOfThingTypes;
import dunegon.game.ThingType;
import dunegon.game.login.CharList;
import dunegon.io.ItemLoader;
import dunegon.net.Protocol;
import dunegon.net.ProtocolGame;
import dunegon.net.ProtocolLogin;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        System.out.println("starting");

//        ArrayOfThingTypes arrayOfThingTypes = new ArrayOfThingTypes();
//
//        final ClassLoader loader = Main.class.getClassLoader();
//        URL resourceUrl = loader.getResource("Tibia.dat");
//
//        ItemLoader.loadDat(arrayOfThingTypes.getThingTypesArray(), resourceUrl.toURI());

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
