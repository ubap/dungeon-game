package dunegon.net;

import dunegon.Config;
import dunegon.game.*;
import dunegon.io.DatAttrs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ProtocolGame extends Protocol {
    private Logger mLogger = LoggerFactory.getLogger(ProtocolGame.class.getSimpleName());

    private String accountName;
    private String password;
    private String characterName;

    private int mChallengeTimestamp;
    private byte mChallengeRandom;

    private LocalPlayer localPlayer;

    public ProtocolGame(String accountName, String password, String characterName) {
        this.accountName = accountName;
        this.password = password;
        this.characterName = characterName;
        this.localPlayer = new LocalPlayer();
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
            mLogger.error("Incorrect packet size");
        }
        byte opCode = inputMessage.getU8();
        if (opCode != Proto.OpCode.GAMEWORLD_FIRST_PACKET) {
            mLogger.error("Incorrect first packet opcode");
        }

        mChallengeTimestamp = inputMessage.getU32();
        mChallengeRandom = inputMessage.getU8();

        sendFirstPacket();
    }

    @Override
    protected void onRecv(InputMessage inputMessage) {
        while (inputMessage.hasMore()) {
            byte opCode = inputMessage.getU8();
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
                case Proto.OpCode.ENTER_WORLD:
                    processEnterWorld(inputMessage);
                    break;
                case Proto.OpCode.MAP_DESCRIPTION:
                    processMapDescription(inputMessage);
                    break;
                case Proto.OpCode.CREATURE_SAY:
                    processCreatureSay(inputMessage);
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
                case Proto.OpCode.CREATURE_LIGHT:
                    processCreatureLight(inputMessage);
                    break;
                case Proto.OpCode.PLAYER_BASIC_DATA:
                    processPlayerBasicData(inputMessage);
                    break;
                case Proto.OpCode.PLAYER_STATE:
                    processPlayerState(inputMessage);
                    break;
                default:
                    mLogger.warn("Unrecognized opCode: {}", String.format("0x%x", opCode));
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
        int playerId = inputMessage.getU32();
        int serverBeat = inputMessage.getU16();

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
        int x = inputMessage.getU16();
        int y = inputMessage.getU16();
        int z = inputMessage.getU8();

        int startz, endz, zstep;
        if (z > 7) {
            startz = z - 2;
            endz = Math.min(z + 2, 15);
            zstep = 1;
        } else {
            startz = 7;
            endz = 0;
            zstep = -1;
        }

        int width = Consts.MAP_WIDTH;
        int height = Consts.MAP_HEIGHT;

        int skip = 0;
        for (int nz = startz; nz != endz + zstep; nz += zstep) {
            skip = setFloorDescription(inputMessage, x - 8, y - 6, nz, width, height, z - nz, skip);
            mLogger.info("skip: {}", skip);
        }

        return;
    }

    private int setFloorDescription(InputMessage inputMessage, int x, int y, int z, int width, int height, int offset, int skip) {
        for (int nx = 0; nx < width; nx++) {
            for (int ny = 0; ny < height; ny++) {
                Position tilePos = new Position(x + nx + offset, y + ny + offset, z);
                if (skip == 0) {
                    skip = setTileDescription(inputMessage, tilePos);
                } else {
                    // clean tile
                    skip--;
                }
            }
        }
        return skip;
    }

    private int setTileDescription(InputMessage inputMessage, Position position) {
        // clean tile

        mLogger.info("Getting tile desc from pos {}", position);

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
                mLogger.error("too many things");
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

        //ThingType thingType = ThingTypeManager.getInstance().getThingType(DatAttrs.ThingCategory.ThingCategoryItem).getThing(id);
        Item thing = Item.create(id);

        int gameThingMark = inputMessage.getU8();

        if (thing.isStackable() || thing.isFluidContainer() || thing.isSplash()) {
            thing.setCountOrSubType(inputMessage.getU8());
        }

        if (thing.getAnimationPhases() > 1) {
            inputMessage.getU8(); // sync I think
        }

        mLogger.info("getItem item id: {}", id);

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

                int id = inputMessage.getU32();
                int creatureType = inputMessage.getU8();

                String name = inputMessage.getString();
                mLogger.info("Name: {}", name);

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


    private void processPing(InputMessage inputMessage) {
        sendPingBack();
    }

    private void processCreatureSay(InputMessage inputMessage) {
        inputMessage.getU32(); // statementId
        String creatureName = inputMessage.getString();

        int level = inputMessage.getU16();
        byte type = inputMessage.getU8();

        int x = inputMessage.getU16();
        int y = inputMessage.getU16();
        int z = inputMessage.getU8();

        String text = inputMessage.getString();

        mLogger.info("Creature {} at pos {} {} {} says the following {}", creatureName, x, y, z, text);
    }

    private void processAddInventory(InputMessage inputMessage) {
        int slot = inputMessage.getU8();
        Item item = getItem(inputMessage);
        mLogger.info("processAddInventory, slot: {}", slot);
    }

    private void processDeleteInventory(InputMessage inputMessage) {
        int slot = inputMessage.getU8();
        mLogger.info("processDeleteInventory, slot: {}", slot);
    }

    private void processPlayerStats(InputMessage inputMessage) {
        mLogger.info("processPlayerStats");

        int health = inputMessage.getU16();
        int maxHealth = inputMessage.getU16();
        int freeCapacity = inputMessage.getU32();
        int totalCapacity = inputMessage.getU32();
        int experience = inputMessage.getU32(); inputMessage.getU32(); // TODO: ;
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
        mLogger.info("processPlayerSkills");
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
        mLogger.info("processAmbientLight, intensity:{}, color:{}", intensity, color);
    }

    private void processGraphicalEffect(InputMessage inputMessage) {
        Position position = getPosition(inputMessage);

        int effectId = inputMessage.getU8();

        Thing effect = Effect.create(effectId);


        mLogger.info("processGraphicalEffect, pos: {}, id: {}", position, effectId);
    }

    private void processCreatureLight(InputMessage inputMessage) {
        long id = inputMessage.getU32();
        int intensity = inputMessage.getU8();
        int color = inputMessage.getU8();
        Creature creature = Game.getInstance().getMap().getCreatureById(id);
        if (creature == null) {
            throw new RuntimeException("could not get creature");
        }

        mLogger.info("processCreatureLight, creature:{}, intensity:{}, color:{}", creature.getName(), intensity, color);
    }

    private void processPlayerBasicData(InputMessage inputMessage) {
        boolean premium = inputMessage.getU8() != 0;
        long premiumExpiration = inputMessage.getU32();
        int vocation = inputMessage.getU8();

        int spellCount = inputMessage.getU16();
        for (int i = 0; i < spellCount; i++) {
            inputMessage.getU8(); // get known spell
        }
        mLogger.info("processPlayerBasicData");
    }

    private void processPlayerState(InputMessage inputMessage) {
        int states = inputMessage.getU16();
        mLogger.info("processPlayerState");
    }

    private Position getPosition(InputMessage inputMessage) {
        int x = inputMessage.getU16();
        int y = inputMessage.getU16();
        int z = inputMessage.getU8();

        return new Position(x, y, z);
    }

    // send

    private void sendPingBack() {
        mLogger.info("sendPingBack");
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
}
