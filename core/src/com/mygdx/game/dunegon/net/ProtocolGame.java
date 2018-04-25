package com.mygdx.game.dunegon.net;

import com.mygdx.game.dunegon.Config;
import com.mygdx.game.dunegon.game.AwareRange;
import com.mygdx.game.dunegon.game.Consts;
import com.mygdx.game.dunegon.game.Creature;
import com.mygdx.game.dunegon.game.Effect;
import com.mygdx.game.dunegon.game.Game;
import com.mygdx.game.dunegon.game.Item;
import com.mygdx.game.dunegon.game.LocalPlayer;
import com.mygdx.game.dunegon.game.Monster;
import com.mygdx.game.dunegon.game.Npc;
import com.mygdx.game.dunegon.game.Outfit;
import com.mygdx.game.dunegon.game.Player;
import com.mygdx.game.dunegon.game.Position;
import com.mygdx.game.dunegon.game.Thing;
import com.mygdx.game.dunegon.io.DatAttrs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ProtocolGame extends Protocol {
    private static Logger LOGGER = LoggerFactory.getLogger(ProtocolGame.class.getSimpleName());

    private String accountName;
    private String password;
    private String characterName;

    private long mChallengeTimestamp;
    private short mChallengeRandom;

    private LocalPlayer localPlayer;
    private boolean mapKnown;


    public LocalPlayer getLocalPlayer() {
        return this.localPlayer;
    }

    public ProtocolGame(String accountName, String password, String characterName) {
        this.accountName = accountName;
        this.password = password;
        this.password = password;
        this.characterName = characterName;
        this.localPlayer = new LocalPlayer();
        this.mapKnown = false;
    }

    @Override
    protected void onConnect() throws IOException {
        enableChecksum();
        startReceiving();
    }

    @Override
    protected void onRecvFirstPacket(InputMessage inputMessage) throws IOException {
        int packetSize = inputMessage.getU16();
        if (packetSize != 6) {
            LOGGER.error("Incorrect packet size");
        }
        short opCode = inputMessage.getU8();
        if (opCode != Proto.OpCode.GAMEWORLD_FIRST_PACKET) {
            LOGGER.error("Incorrect first packet opcode");
        }

        mChallengeTimestamp = inputMessage.getU32();
        mChallengeRandom = inputMessage.getU8();

        sendFirstPacket();
    }

    @Override
    protected void onRecv(InputMessage inputMessage) {
        while (inputMessage.hasMore()) {
            short opCode = inputMessage.getU8();
            switch (opCode) {
                case Proto.OpCode.GAMESERVER_LOGIN_SUCCESS:
                    processLoginSuccess(inputMessage);
                    break;
                case Proto.OpCode.GAMEWORLD_PING:
                    processPing(inputMessage);
                    break;
                case Proto.OpCode.PENDING_STATE:
                    processPendingState(inputMessage);
                    break;
                case Proto.OpCode.DEATH:
                    processDeath(inputMessage);
                    break;
                case Proto.OpCode.ENTER_WORLD:
                    processEnterWorld(inputMessage);
                    break;
                case Proto.OpCode.MAP_DESCRIPTION:
                    processMapDescription(inputMessage);
                    break;
                case Proto.OpCode.MAP_TOP_ROW:
                    processMapTopRow(inputMessage);
                    break;
                case Proto.OpCode.MAP_RIGHT_ROW:
                    processMapRightRow(inputMessage);
                    break;
                case Proto.OpCode.MAP_BOTTOM_ROW:
                    processMapBottomRow(inputMessage);
                    break;
                case Proto.OpCode.MAP_LEFT_ROW:
                    processMapLeftRow(inputMessage);
                    break;
                case Proto.OpCode.CREATURE_SAY:
                    processTalk(inputMessage);
                    break;
                case Proto.OpCode.SET_INVENTORY:
                    processAddInventory(inputMessage);
                    break;
                case Proto.OpCode.DELETE_INVENTORY:
                    processDeleteInventory(inputMessage);
                    break;
                case Proto.OpCode.PLAYER_STATS:
                    processPlayerStats(inputMessage);
                    break;
                case Proto.OpCode.PLAYER_SKILLS:
                    processPlayerSkills(inputMessage);
                    break;
                case Proto.OpCode.AMBIENT_LIGHT:
                    processAmbientLight(inputMessage);
                    break;
                case Proto.OpCode.GRAPHICAL_EFFECT:
                    processGraphicalEffect(inputMessage);
                    break;
                case Proto.OpCode.MISSILE_EFFECT:
                    processMissileEffect(inputMessage);
                    break;
                case Proto.OpCode.TEXT_MESSAGE:
                    processTextMessage(inputMessage);
                    break;
                case Proto.OpCode.CREATURE_LIGHT:
                    processCreatureLight(inputMessage);
                    break;
                case Proto.OpCode.CREATURE_HEALTH:
                    processCreatureHealth(inputMessage);
                    break;
                case Proto.OpCode.CREATURE_SPEED:
                    processCreatureSpeed(inputMessage);
                    break;
                case Proto.OpCode.CREATURE_SKULL:
                    processCreatureSkull(inputMessage);
                    break;
                case Proto.OpCode.CREATURE_MARKS:
                    processCreatureMarks(inputMessage);
                    break;
                case Proto.OpCode.PLAYER_BASIC_DATA:
                    processPlayerBasicData(inputMessage);
                    break;
                case Proto.OpCode.PLAYER_STATE:
                    processPlayerState(inputMessage);
                    break;
                case Proto.OpCode.CREATE_ON_MAP:
                    processTileAddThing(inputMessage);
                    break;
                case Proto.OpCode.CHANGE_ON_MAP:
                    processTileTransformThing(inputMessage);
                    break;
                case Proto.OpCode.MOVE_CREATURE:
                    processMoveCreature(inputMessage);
                    break;
                case Proto.OpCode.DELETE_ON_MAP:
                    processTileRemoveThing(inputMessage);
                    break;
                case Proto.OpCode.CREATURE_TYPE:
                    processCreatureType(inputMessage);
                    break;
                case Proto.OpCode.PLAYER_INVENTORY:
                    processPlayerInventory(inputMessage);
                    break;
                default:
                    LOGGER.warn("Unrecognized opCode: {}", String.format("0x%x", opCode));
                    return;
            }
        }
    }

    private void sendFirstPacket() throws IOException {
        OutputMessage outputMessage = new OutputMessage();
        outputMessage.addU8(Proto.CLIENT_PENDING_GAME);

        outputMessage.addU16(Config.OS);
        outputMessage.addU16(Config.PROTOCOL_VERSION); // version

        outputMessage.addU32(Config.CLIENT_VERSION);
        outputMessage.addU8(Config.CLIENT_TYPE);
        outputMessage.addU16(Config.DAT_REVISION);

        int offset = outputMessage.getMessageSize();
        outputMessage.addU8((char)0); // RSA, check byte, server checks if this byte is 0 after decryption

        int[] xtea = getXteaKey();
        outputMessage.addU32(xtea[0]);
        outputMessage.addU32(xtea[1]);
        outputMessage.addU32(xtea[2]);
        outputMessage.addU32(xtea[3]);

        outputMessage.addU8((char) 0); // gamemasterflag

        String sessionArgs = String.format("%s\n%s\n%s\n%s", accountName, password, "", "0");
        outputMessage.addString(sessionArgs);
        outputMessage.addString(characterName);
        outputMessage.addU32(mChallengeTimestamp);
        outputMessage.addU8((char) mChallengeRandom);


        outputMessage.addPaddingBytes(128 - (outputMessage.getMessageSize() - offset));
        outputMessage.encryptRsa();

        enableChecksum();
        send(outputMessage);

        enableXtea();
    }

    private void processLoginSuccess(InputMessage inputMessage) {
        long playerId = inputMessage.getU32();
        int serverBeat = inputMessage.getU16();
        Game.getInstance().setServerBeat(serverBeat);

        double speedA = inputMessage.getDouble();
        double speedB = inputMessage.getDouble();
        double speedC = inputMessage.getDouble();

        boolean canReportBugs = inputMessage.getU8() == 1;
        boolean canChangePvpFrameOption = inputMessage.getU8() == 1;
        boolean experModeEnabled = inputMessage.getU8() == 1;

        String urlToIngameStoreImages = inputMessage.getString();
        int premiumCoinPackageSize = inputMessage.getU16();

        localPlayer.setId(playerId);
        localPlayer.setSpeedFormula(speedA, speedB, speedC);
    }

    private void processPendingState(InputMessage inputMessage) {
    }
    private void processEnterWorld(InputMessage inputMessage) {
    }

    private void processMapDescription(InputMessage inputMessage) {
        Position position = getPosition(inputMessage);

        if (!mapKnown) {
            localPlayer.setPosition(position);
        }

        AwareRange awareRange = Game.getInstance().getMap().getAwareRange();
        setMapDescription(inputMessage, position.getX() - awareRange.getLeft(),
                position.getY() - awareRange.getTop(), position.getZ(),
                awareRange.horizontal(), awareRange.vertical());

        mapKnown = true;
        Game.getInstance().getMap().setCentralPosition(position);
        LOGGER.info("processMapDescription");

    }

    private void processMapTopRow(InputMessage inputMessage) {
        Position position = Game.getInstance().getMap().getCentralPosition();
        position = new Position(position.getX(), position.getY() - 1, position.getZ());

        AwareRange awareRange = Game.getInstance().getMap().getAwareRange();
        setMapDescription(inputMessage, position.getX() - awareRange.getLeft(),
                position.getY() - awareRange.getTop(), position.getZ(),
                awareRange.horizontal(), 1);

        Game.getInstance().getMap().setCentralPosition(position);
        LOGGER.info("processMapTopRow");
    }

    private void processMapRightRow(InputMessage inputMessage) {
        Position position = Game.getInstance().getMap().getCentralPosition();
        position = new Position(position.getX() + 1, position.getY(), position.getZ());

        AwareRange awareRange = Game.getInstance().getMap().getAwareRange();
        setMapDescription(inputMessage, position.getX() + awareRange.getRight(),
                position.getY() - awareRange.getTop(), position.getZ(),
                1, awareRange.vertical());

        Game.getInstance().getMap().setCentralPosition(position);
        LOGGER.info("processMapRightRow");
    }

    private void processMapBottomRow(InputMessage inputMessage) {
        Position position = Game.getInstance().getMap().getCentralPosition();
        position = new Position(position.getX(), position.getY() + 1, position.getZ());

        AwareRange awareRange = Game.getInstance().getMap().getAwareRange();
        setMapDescription(inputMessage, position.getX() - awareRange.getLeft(),
                position.getY() + awareRange.getBottom(), position.getZ(),
                awareRange.horizontal(), 1);

        Game.getInstance().getMap().setCentralPosition(position);
        LOGGER.info("processMapBottomRow");
    }

    private void processMapLeftRow(InputMessage inputMessage) {
        Position position = Game.getInstance().getMap().getCentralPosition();
        position = new Position(position.getX() - 1, position.getY(), position.getZ());

        AwareRange awareRange = Game.getInstance().getMap().getAwareRange();
        setMapDescription(inputMessage, position.getX() - awareRange.getLeft(),
                position.getY() - awareRange.getTop(), position.getZ(),
                1, awareRange.vertical());

        Game.getInstance().getMap().setCentralPosition(position);
        LOGGER.info("processMapLeftRow");
    }

    private void processPing(InputMessage inputMessage) {
        sendPingBack();
    }

    private void processTalk(InputMessage inputMessage) {
        inputMessage.getU32(); // statementId
        String creatureName = inputMessage.getString();

        int level = inputMessage.getU16();
        short mode = inputMessage.getU8();

        Position position;
        int channelId;
        switch (mode) {
            case Consts.Message.SAY:
            case Consts.Message.WHISPER:
            case Consts.Message.YELL:
            case Consts.Message.NPC_TO:
            case Consts.Message.BARK_LOW:
            case Consts.Message.BARK_LOUD:
            case Consts.Message.SPELL:
            case Consts.Message.NPC_FROM_START_BLOCK:
                position = getPosition(inputMessage);
                break;
            case Consts.Message.CHANNEL:
            case Consts.Message.CHANNEL_MANAGEMENT:
            case Consts.Message.CHANNEL_HIGHLIGHT:
            case Consts.Message.GAMEMASTER_CHANNEL:
                channelId = inputMessage.getU16();
                break;
            case Consts.Message.NPC_FROM:
            case Consts.Message.PRIVATE_FROM:
            case Consts.Message.GAMEMASTER_BROADCAST:
            case Consts.Message.GAMEMASTER_PRIVATE_FROM:
                break;
            default:
                throw new RuntimeException("unknown message mode " + mode);
        }

        String text = inputMessage.getString();
        LOGGER.info("Creature {} says the following {}", creatureName, text);
    }

    private void processAddInventory(InputMessage inputMessage) {
        int slot = inputMessage.getU8();
        Item item = getItem(inputMessage);
        LOGGER.info("processAddInventory, slot: {}", slot);
    }

    private void processDeleteInventory(InputMessage inputMessage) {
        int slot = inputMessage.getU8();
        LOGGER.info("processDeleteInventory, slot: {}", slot);
    }

    private void processPlayerStats(InputMessage inputMessage) {
        LOGGER.info("processPlayerStats");

        int health = inputMessage.getU16();
        int maxHealth = inputMessage.getU16();
        long freeCapacity = inputMessage.getU32();
        long totalCapacity = inputMessage.getU32();
        long experience = inputMessage.get64();
        int level = inputMessage.getU16();
        int levelPercent = inputMessage.getU8();

        int baseXpGain = inputMessage.getU16();
        int voucherAddend = inputMessage.getU16();
        int grindingAddend = inputMessage.getU16();
        int storeBoostAddend = inputMessage.getU16();
        int huntingBoostFactor = inputMessage.getU16();

        int mana = inputMessage.getU16();
        int maxMana = inputMessage.getU16();

        int magicLevel = inputMessage.getU8();
        int baseMagicLevel = inputMessage.getU8();
        int magicLevelPercent = inputMessage.getU8();
        int soul = inputMessage.getU8();
        int stamina = inputMessage.getU16();

        int baseSkillSpeed = inputMessage.getU16();
        int regenerationTime = inputMessage.getU16(); // food

        int training = inputMessage.getU16();
        int remainingStoreXpBoostSeconds = inputMessage.getU16();
        boolean canBuyMoreStoreXpBoosts = inputMessage.getU8() != 0;

        localPlayer.setHealth(health);
        localPlayer.setMaxHealth(maxHealth);
        localPlayer.setFreeCapacity(freeCapacity);
        localPlayer.setTotalCapacity(totalCapacity);
        localPlayer.setTotalExperience(0);
        localPlayer.setLevel(level);
        localPlayer.setLevelPercent(levelPercent);
        localPlayer.setMana(mana);
        localPlayer.setMaxMana(maxMana);
        localPlayer.setMagicLevel(magicLevel);
        localPlayer.setMagicLevelPercent(magicLevelPercent);
        localPlayer.setBaseMagicLevel(baseMagicLevel);
        localPlayer.setSoul(soul);
        localPlayer.setStamina(stamina);
    }

    private void processPlayerSkills(InputMessage inputMessage) {
        LOGGER.info("processPlayerSkills");
        for (int skill = 0; skill < Consts.Skill.LASTSKILL; skill++) {
            int level = inputMessage.getU16();
            int baseLevel = inputMessage.getU16();

            if (skill <= Consts.Skill.FISHING) {
                int levelPercent = inputMessage.getU8();
            }
        }
    }

    private void processAmbientLight(InputMessage inputMessage) {
        int intensity = inputMessage.getU8();
        int color = inputMessage.getU8();
        LOGGER.info("processAmbientLight, intensity:{}, color:{}", intensity, color);
    }

    private void processGraphicalEffect(InputMessage inputMessage) {
        Position position = getPosition(inputMessage);
        int effectId = inputMessage.getU8();
        Thing effect = Effect.create(effectId);
        LOGGER.info("processGraphicalEffect, pos: {}, id: {}", position, effectId);
    }
    private void processMissileEffect(InputMessage inputMessage) {
        Position fromPosition = getPosition(inputMessage);
        Position toPosition = getPosition(inputMessage);
        int shotId = inputMessage.getU8();
        LOGGER.info("processMissileEffect, fromPosition: {}, toPosition: {}", fromPosition, toPosition);
    }

    private void processTextMessage(InputMessage inputMessage) {
        int mode = inputMessage.getU8();
        String text;
        switch (mode) {
            case Consts.Message.CHANNEL_MANAGEMENT:
                inputMessage.getU16(); // channel
                text = inputMessage.getString();
                break;
            case Consts.Message.GUILD:
            case Consts.Message.PARTY_MANAGEMENT:
            case Consts.Message.PARTY:
                inputMessage.getU16(); // channel
                text = inputMessage.getString();
                break;
            case Consts.Message.DAMAGE_DEALED:
            case Consts.Message.DAMAGE_RECEIVED:
            case Consts.Message.DAMAGE_OTHERS: {
                Position position = getPosition(inputMessage);
                long valuePhysical, valueMagic;
                int colorPhysical, colorMagic;
                valuePhysical = inputMessage.getU32();
                colorPhysical = inputMessage.getU8();
                valueMagic = inputMessage.getU32();
                colorMagic = inputMessage.getU8();
                text = inputMessage.getString();
                break;
            }
            case Consts.Message.HEAL:
            case Consts.Message.MANA:
            case Consts.Message.EXP:
            case Consts.Message.HEAL_OTHERS:
            case Consts.Message.EXP_OTHERS: {
                Position position = getPosition(inputMessage);
                long value = inputMessage.getU32();
                int color = inputMessage.getU8();
                text = inputMessage.getString();
                break;
            }
            case Consts.Message.INVALID:
                throw new RuntimeException("invalid message");
            default:
                text = inputMessage.getString();
                break;
        }
        LOGGER.info("processTextMessage, text:{}", text);
    }

    private void processCreatureLight(InputMessage inputMessage) {
        long id = inputMessage.getU32();
        int intensity = inputMessage.getU8();
        int color = inputMessage.getU8();
        Creature creature = Game.getInstance().getMap().getCreatureById(id);
        if (creature == null) {
            throw new RuntimeException("could not get creature");
        }
        LOGGER.info("processCreatureLight, creature:{}, intensity:{}, color:{}", creature.getName(), intensity, color);
    }

    private void processCreatureHealth(InputMessage inputMessage) {
        long id = inputMessage.getU32();
        int healthPercent = inputMessage.getU8();
        Creature creature = Game.getInstance().getMap().getCreatureById(id);
        if (creature != null) {
            creature.setHealthPercent(healthPercent);
        }
        LOGGER.info("processCreatureHealth hppc: {}", healthPercent);
    }

    private void processCreatureSpeed(InputMessage inputMessage) {
        long id = inputMessage.getU32();
        int baseSpeed = inputMessage.getU16();
        int speed = inputMessage.getU16();

        Creature creature = Game.getInstance().getMap().getCreatureById(id);
        if (creature != null) {
            creature.setSpeed(speed);
            creature.setBaseSpeed(baseSpeed);
        }

        LOGGER.info("processCreatureSpeed");
    }
    private void processCreatureSkull(InputMessage inputMessage) {
        long cid = inputMessage.getU32();
        int skull = inputMessage.getU8();
        LOGGER.info("processCreatureSkull");
    }
    private void processCreatureMarks(InputMessage inputMessage) {
        long cid = inputMessage.getU32();
        boolean isPermanent = inputMessage.getU8() != 1;
        int markType = inputMessage.getU8();
        LOGGER.info("processCreatureMarks");
    }

    private void processPlayerBasicData(InputMessage inputMessage) {
        boolean premium = inputMessage.getU8() != 0;
        long premiumExpiration = inputMessage.getU32();
        int vocation = inputMessage.getU8();

        int spellCount = inputMessage.getU16();
        for (int i = 0; i < spellCount; i++) {
            inputMessage.getU8(); // get known spell
        }
        LOGGER.info("processPlayerBasicData");
    }

    private void processPlayerState(InputMessage inputMessage) {
        int states = inputMessage.getU16();
        LOGGER.info("processPlayerState");
    }

    private void processTileTransformThing(InputMessage inputMessage) {
        Thing thing = getMappedThing(inputMessage);
        Thing newThing = getThing(inputMessage);

        if (thing == null) {
            throw new RuntimeException("no thing");
        }

        Position position = thing.getPosition();
        int stackPos = thing.getStackPos();

        if (!Game.getInstance().getMap().removeThing(thing)) {
            throw new RuntimeException("unable to remove thing");
        }

        Game.getInstance().getMap().addThing(newThing, position, stackPos);
        LOGGER.info("processTileTransformThing");
    }

    private void processTileAddThing(InputMessage inputMessage) {
        Position position = getPosition(inputMessage);
        int stackPos = inputMessage.getU8();
        Thing thing = getThing(inputMessage);
        Game.getInstance().getMap().addThing(thing, position, stackPos);

        LOGGER.info("processTileAddThing position: {}", position);
    }

    private void processTileRemoveThing(InputMessage inputMessage) {
        Thing thing = getMappedThing(inputMessage);
        if (thing == null) {
            throw new RuntimeException("no thing");
        }

        if (!Game.getInstance().getMap().removeThing(thing)) {
            throw new RuntimeException("unable to remove thing");
        }
        LOGGER.info("processTileRemoveThing, position {}", thing.getPosition());
    }

    private void processMoveCreature(InputMessage inputMessage) {
        Thing thing = getMappedThing(inputMessage);
        Position newPos = getPosition(inputMessage);

        if (thing == null || !thing.isCreature()) {
            throw new RuntimeException("no creature found to move");
        }

        if (!Game.getInstance().getMap().removeThing(thing)) {
            throw new RuntimeException("unable to remove creature");
        }

        LOGGER.info("processMoveCreature, oldPos: {}, newPos: {}", thing.getPosition(), newPos);

        ((Creature) thing).allowaAppearWalk();

        Game.getInstance().getMap().addThing(thing, newPos, -1);
    }

    private void processCreatureType(InputMessage inputMessage) {
        long id = inputMessage.getU32();
        int type = inputMessage.getU8();
    }

    private void processDeath(InputMessage inputMessage) {
        int deathType = inputMessage.getU8();
        int penalty = inputMessage.getU8();
        LOGGER.info("processDeath");
    }

    private void processPlayerInventory(InputMessage inputMessage) {
        int size = inputMessage.getU16();
        for (int i = 0; i < size; i++) {
            inputMessage.getU16(); // id
            inputMessage.getU8(); // subtype
            inputMessage.getU16(); // count
        }
    }

    // HELPERS -->

    private Position getPosition(InputMessage inputMessage) {
        int x = inputMessage.getU16();
        int y = inputMessage.getU16();
        int z = inputMessage.getU8();

        return new Position(x, y, z);
    }

    private void setMapDescription(InputMessage inputMessage, int x, int y, int z, int width, int height) {
        int startZ, endZ, zStep;
        if (z > 7) {
            startZ = z - 2;
            endZ = Math.min(z + 2, 15);
            zStep = 1;
        } else {
            startZ = 7;
            endZ = 0;
            zStep = -1;
        }

        int skip = 0;
        for (int nz = startZ; nz != endZ + zStep; nz += zStep) {
            skip = setFloorDescription(inputMessage, x, y, nz, width, height, z - nz, skip);
            // LOGGER.info("skip: {}", skip);
        }
    }

    private int setFloorDescription(InputMessage inputMessage, int x, int y, int z,
                                    int width, int height, int offset, int skip) {
        for (int nx = 0; nx < width; nx++) {
            for (int ny = 0; ny < height; ny++) {
                Position tilePos = new Position(x + nx + offset, y + ny + offset, z);
                if (skip == 0) {
                    skip = setTileDescription(inputMessage, tilePos);
                } else {
                    // clean tile
                    Game.getInstance().getMap().cleanTile(tilePos);
                    skip--;
                }
            }
        }
        return skip;
    }

    private int setTileDescription(InputMessage inputMessage, Position position) {
        // LOGGER.info("Getting tile desc from pos {}", position);
        Game.getInstance().getMap().cleanTile(position);

        boolean gotEffect = false;
        for (int stackPos = 0; stackPos < 255; stackPos++) {
            if (inputMessage.peekU16() >= 0xFF00) {
                return inputMessage.getU16() & 0xFF;
            }

            if (!gotEffect) {
                inputMessage.getU16(); // env effect
                gotEffect = true;
                continue;
            }

            if (stackPos > 10) {
                LOGGER.error("too many things");
            }

            Thing thing = getThing(inputMessage);
            Game.getInstance().getMap().addThing(thing, position, stackPos);
        }

        return 0;
    }

    private Thing getThing(InputMessage inputMessage) {
        Thing thing;
        int id = inputMessage.getU16();

        if (id == 0) {
            throw new RuntimeException("invalid thingType id");
        } else if (id == Proto.ItemOpCode.UNKNOWN_CREATURE || id == Proto.ItemOpCode.OUTDATED_CREATUER
                || id == Proto.ItemOpCode.CREATURE) {
            thing = getCreature(inputMessage, id);
        } else {
            thing = getItem(inputMessage, id);
        }

        return thing;
    }

    private Item getItem(InputMessage inputMessage) {
        return getItem(inputMessage, 0);
    }

    private Item getItem(InputMessage inputMessage, int id) {
        if (id == 0) {
            id = inputMessage.getU16();
        }
        Item thing = Item.create(id);
        int gameThingMark = inputMessage.getU8();

        if (thing.isStackable() || thing.isFluidContainer() || thing.isSplash()) {
            thing.setCountOrSubType(inputMessage.getU8());
        }
        if (thing.getAnimationPhases() > 1) {
            inputMessage.getU8(); // sync I think
        }

        //LOGGER.info("getItem item id: {}", id);

        return thing;
    }

    private Thing getCreature(InputMessage inputMessage, int type) {
        if (type == 0) {
            type = inputMessage.getU16();
        }

        Creature creature;

        boolean known = (type != Proto.ItemOpCode.UNKNOWN_CREATURE);
        if (type == Proto.ItemOpCode.OUTDATED_CREATUER || type == Proto.ItemOpCode.UNKNOWN_CREATURE) {
            if (known) {
                long id = inputMessage.getU32();
                creature = Game.getInstance().getMap().getCreatureById(id);
                if (creature == null) {
                    throw new RuntimeException("server said that a creature is known, but it's not");
                }
            } else {
                long removeId = inputMessage.getU32();
                Game.getInstance().getMap().removeCreatureById(removeId);

                long id = inputMessage.getU32();
                int creatureType = inputMessage.getU8();

                String name = inputMessage.getString();
                LOGGER.info("Name: {}", name);

                if (id == localPlayer.getId()) {
                    creature = localPlayer;
                } else if (creatureType == Proto.CreatureType.PLAYER) {
                    creature = new Player();
                } else if (creatureType == Proto.CreatureType.MONSTER) {
                    creature = new Monster();
                } else if (creatureType == Proto.CreatureType.NPC) {
                    creature = new Npc();
                } else {
                    throw new RuntimeException("invalid creature type: " + creatureType);
                }

                creature.setId(id);
                creature.setName(name);

                Game.getInstance().getMap().addCreature(creature);
            }

            int healthPercent = inputMessage.getU8();
            Consts.Direction direction = Consts.Direction.fromInt(inputMessage.getU8());
            // get outfit here
            Outfit outfit = getOutfit(inputMessage);

            int lightIntensity = inputMessage.getU8();
            int lightColor = inputMessage.getU8();

            int speed = inputMessage.getU16();
            int skull = inputMessage.getU8();
            int shield = inputMessage.getU8();

            if (!known) {
                int emblem = inputMessage.getU8();
            }

            int creatureType = inputMessage.getU8();
            int icon = inputMessage.getU8();
            int mark = inputMessage.getU8();
            inputMessage.getU16(); // helpers
            int unpass = inputMessage.getU8();

            if (creature != null) {
                // todo other attribs set
                creature.setSpeed(speed);
                creature.setHealthPercent(healthPercent);
                creature.setDirection(direction);
                creature.setOutfit(outfit);
            }

        } else if (type == Proto.ItemOpCode.CREATURE) {
            long id = inputMessage.getU32();
            creature = Game.getInstance().getMap().getCreatureById(id);
            Consts.Direction direction = Consts.Direction.fromInt(inputMessage.getU8());
            int unpasss = inputMessage.getU8();

            // todo: turn creature
            creature.setDirection(direction);
        } else {
            throw new RuntimeException("unknown creature opcode");
        }

        return creature;
    }

    private Outfit getOutfit(InputMessage inputMessage) {
        Outfit outfit = new Outfit();

        int lookType = inputMessage.getU16();
        if (lookType != 0) {
            outfit.setThingCategory(DatAttrs.ThingCategory.ThingCategoryCreature);
            int head = inputMessage.getU8();
            int body = inputMessage.getU8();
            int legs = inputMessage.getU8();
            int feet = inputMessage.getU8();
            int addons = inputMessage.getU8();

            outfit.setId(lookType);
            outfit.setHead(head);
            outfit.setBody(body);
            outfit.setLegs(legs);
            outfit.setFeet(feet);
            outfit.setAddons(addons);
        } else {
            int lookTypeEx = inputMessage.getU16();
            if (lookTypeEx == 0) {
                // effect
                outfit.setThingCategory(DatAttrs.ThingCategory.ThingCategoryEffect);
                outfit.setAuxId(13); // invisible effect id
            } else {
                // outfit type = item
                lookTypeEx = 0;
                outfit.setThingCategory(DatAttrs.ThingCategory.ThingCategoryItem);
                outfit.setAuxId(lookTypeEx);
            }
        }
        int mount = inputMessage.getU16();
        outfit.setMount(mount);

        return outfit;
    }

    private Thing getMappedThing(InputMessage inputMessage) {
        Thing thing;
        int x = inputMessage.getU16();
        if (x != 0xFFFF) {
            int y = inputMessage.getU16();
            int z = inputMessage.getU8();
            int stackPos = inputMessage.getU8();
            Position position = new Position(x, y, z);
            thing = Game.getInstance().getMap().getThing(position, stackPos);
            if (thing == null) {
                throw new RuntimeException("no thing at pos:" + position + ", stackpos:" + stackPos);
            }

        } else {
            long id = inputMessage.getU32();
            thing = Game.getInstance().getMap().getCreatureById(id);
            if (thing == null) {
                throw new RuntimeException("no creature with id: " + id);
            }
        }
        return thing;
    }

    // SEND -->

    private void sendPingBack() {
        // LOGGER.info("sendPingBack");
        OutputMessage outputMessage = new OutputMessage();
        outputMessage.addU8((char) Proto.OpCode.GAMEWORLD_PING_BACK);
        send(outputMessage);
    }

    public void sendTurnNorth() {
        OutputMessage outputMessage = new OutputMessage();
        outputMessage.addU8((char) Proto.OpCode.Send.TURN_NORTH);
        send(outputMessage);
    }

    public void sendTurnEast() {
        OutputMessage outputMessage = new OutputMessage();
        outputMessage.addU8((char) Proto.OpCode.Send.TURN_EAST);
        send(outputMessage);
    }

    public void sendWalkWest() {
        OutputMessage outputMessage = new OutputMessage();
        outputMessage.addU8((char) Proto.OpCode.Send.WALK_WEST);
        send(outputMessage);
    }

    public void sendWalkNorth() {
        OutputMessage outputMessage = new OutputMessage();
        outputMessage.addU8((char) Proto.OpCode.Send.WALK_NORTH);
        send(outputMessage);
    }

    public void sendWalkEast() {
        OutputMessage outputMessage = new OutputMessage();
        outputMessage.addU8((char) Proto.OpCode.Send.WALK_EAST);
        send(outputMessage);
    }

    public void sendWalkSouth() {
        OutputMessage outputMessage = new OutputMessage();
        outputMessage.addU8((char) Proto.OpCode.Send.WALK_SOUTH);
        send(outputMessage);
    }
}
