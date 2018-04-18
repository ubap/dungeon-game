package utils;

import dunegon.game.*;
import dunegon.net.ProtocolGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MapPrinter {
    private static Logger LOGGER = LoggerFactory.getLogger(MapPrinter.class.getSimpleName());

    public static void printThingsUnder(Game game, ProtocolGame protocolGame) {
        Position position = protocolGame.getLocalPlayer().getPosition();
        position = new Position(position.getX(), position.getY() - 1, position.getZ());
        List<Item> items = game.getMap().getTile(position).getItems();
        StringBuilder text = new StringBuilder();
        String separator = "";
        text.append("items: ");
        for (Item item : items) {
            text.append(separator).append(item.getId());
            separator = ", ";
        }

        List<Creature> creatures = game.getMap().getTile(position).getCreatures();
        separator = "";
        text.append(", creatures: ");
        for (Creature creature : creatures) {
            text.append(separator).append(creature.getName());
            separator = ", ";
        }

        text.append(", getTopLookThing: ");
        text.append(game.getMap().getTile(position).getTopLookThing().getId());

        LOGGER.info(text.toString());
    }

    public static void printMap(Game game, ProtocolGame protocolGame) {
        Position playerPosition = protocolGame.getLocalPlayer().getPosition();

        List<List<List<String>>> itemsXY;

        itemsXY = new ArrayList<>();

        for (int x = 0; x < Consts.MAP_WIDTH; x++) {
            itemsXY.add(new ArrayList<>());
            for (int y = 0; y < Consts.MAP_HEIGHT; y++) {
                itemsXY.get(x).add(new ArrayList<>());
            }
        }

        for (int x = 0; x < Consts.MAP_WIDTH; x++) {
            for (int y = 0; y < Consts.MAP_HEIGHT; y++) {
                Position position = new Position(playerPosition.getX() - Consts.MAP_WIDTH/2 + x + 1, playerPosition.getY() - Consts.MAP_HEIGHT/2 + y + 1, playerPosition.getZ());
                Tile tile = game.getMap().getTile(position);
                if (tile != null) {
                    itemsXY.get(x).get(y).add(String.format("%1$5d", tile.getTopThing().getId()));
                } else {
                    itemsXY.get(x).get(y).add("     ");
                }
            }
        }

        for (int y = 0; y < Consts.MAP_HEIGHT; y++) {
            for (int x = 0; x < Consts.MAP_WIDTH; x++) {
                String sqm = String.format("%s|", itemsXY.get(x).get(y).get(0));
                System.out.print(sqm);
            }
            System.out.println();
        }
        System.out.println("");

    }
}
