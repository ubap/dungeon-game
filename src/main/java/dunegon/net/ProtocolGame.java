package dunegon.net;

import dunegon.Config;
import dunegon.game.ArrayOfThingTypes;
import dunegon.game.Position;
import dunegon.game.Thing;
import dunegon.io.DatAttrs;
import javafx.geometry.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ProtocolGame extends Protocol {
    private Logger mLogger = LoggerFactory.getLogger(ProtocolGame.class.getSimpleName());

    private String mAccountName;
    private String mPassword;
    private String mCharacterName;

    private int mChallengeTimestamp;
    private byte mChallengeRandom;

    public ProtocolGame(String accountName, String password, String characterName) {
        mAccountName = accountName;
        mPassword = password;
        mCharacterName = characterName;
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
                default:
                    mLogger.warn("Unrecognized opCode: {}", String.format("0x%x", opCode));
                    break;
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

        String sessionArgs = String.format("%s\n%s\n%s\n%s", mAccountName, mPassword, "", "0");
        outputMessage.addString(sessionArgs);
        outputMessage.addString(mCharacterName);
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

        int width = 18;
        int height = 14;

        int skip = 0;
        for (int nz = startz; nz != endz + zstep; nz += zstep) {
            skip = setFloorDescription(inputMessage, x - 8, y - 6, nz, width, height, z - nz, skip);
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

            getThing(inputMessage);
        }

        return 0;
    }

    private Thing getThing(InputMessage inputMessage) {
        Thing thing;
        int id = inputMessage.getU16();

        if (id == 0) {
            throw new RuntimeException("invalid thing id");
        } else if (id == Proto.ItemOpCode.UNKNOWN_CREATURE || id == Proto.ItemOpCode.OUTDATED_CREATUER
                || id == Proto.ItemOpCode.CREATURE) {
            thing = getCreature(inputMessage, id);
        } else {
            thing = getItem(inputMessage, id);
        }

        return thing;
    }

    private Thing getItem(InputMessage inputMessage, int id) {
        if (id == 0) {
            id = inputMessage.getU16();
        }

        Thing thing = ArrayOfThingTypes.getInstance().getThingType(DatAttrs.ThingCategory.ThingCategoryItem).getThing(id);

        int gameThingMark = inputMessage.getU8();

        if (thing.isStackable()) {
            int count = inputMessage.getU8();
        } else if (thing.isFluidContainer() || thing.isSplash()) {
            int countOrSubType = inputMessage.getU8();
        }

        if (thing.getAnimationPhases() > 1) {
            inputMessage.getU8(); // sync I think
        }

        mLogger.info("item id: {}", id);

        return thing;
    }

    private Thing getCreature(InputMessage inputMessage, int type) {
        if (type == 0) {
            type = inputMessage.getU16();
        }

        boolean known = (type != Proto.ItemOpCode.UNKNOWN_CREATURE);
        if (type == Proto.ItemOpCode.OUTDATED_CREATUER || type == Proto.ItemOpCode.UNKNOWN_CREATURE) {
            if (known) {
                long id = inputMessage.getU32();
            } else {
                long removeId = inputMessage.getU32();
                // now remove it from map

                long id = inputMessage.getU32();
                int creatureType = inputMessage.getU8();

                String name = inputMessage.getString();
            }

            int healthPercent = inputMessage.getU8();
            int direction = inputMessage.getU8();
            // get outfit here

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
        } else if (type == Proto.ItemOpCode.CREATURE) {
            long id = inputMessage.getU32();
            int direction = inputMessage.getU8();
            int unpasss = inputMessage.getU8();
        } else {
            throw new RuntimeException("unknown creature opcode");
        }

        return null;
    }

    private void getOutfit(InputMessage inputMessage) {
        int lookType = inputMessage.getU16();
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


    // send

    private void sendPingBack() {
        mLogger.info("sendPingBack");
        OutputMessage outputMessage = new OutputMessage();
        outputMessage.addU8((char) Proto.OpCode.GAMEWORLD_PING_BACK);

        try {
            send(outputMessage);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
