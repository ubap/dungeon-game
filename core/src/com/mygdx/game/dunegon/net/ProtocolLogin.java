package com.mygdx.game.dunegon.net;

import com.mygdx.game.dunegon.Config;
import com.mygdx.game.dunegon.game.login.CharList;
import com.mygdx.game.dunegon.game.login.World;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProtocolLogin extends Protocol {
    private static Logger LOGGER = LoggerFactory.getLogger(ProtocolLogin.class.getSimpleName());

    private String username;
    private String password;

    private CharList charList;

    public ProtocolLogin(String username, String password) {
        charList = new CharList();
        this.username = username;
        this.password = password;
    }

    @Override
    protected void onConnect() throws IOException {
        OutputMessage outputMessage = new OutputMessage();

        outputMessage.addU8(Proto.CLIENT_PENDING_GAME);
        outputMessage.addU16(Config.OS);
        outputMessage.addU16(Config.PROTOCOL_VERSION);
        outputMessage.addU32(Config.CLIENT_VERSION);

        outputMessage.addU32(0);
        outputMessage.addU32(0);
        outputMessage.addU32(0);

        outputMessage.addU8((char)0);

        int offset = outputMessage.getMessageSize();
        outputMessage.addU8((char)0); // RSA, check byte, server checks if this byte is 0 after decryption

        int[] xtea = getXteaKey();
        outputMessage.addU32(xtea[0]);
        outputMessage.addU32(xtea[1]);
        outputMessage.addU32(xtea[2]);
        outputMessage.addU32(xtea[3]);

        outputMessage.addString(username);
        outputMessage.addString(password);

        outputMessage.addPaddingBytes(128 - (outputMessage.getMessageSize() - offset));
        outputMessage.encryptRsa();

        // auth token - not used
        outputMessage.addPaddingBytes(128);
        outputMessage.encryptRsa();

        enableChecksum();
        send(outputMessage);
        enableXtea();

        startReceiving();
    }

    @Override
    protected void onRecvFirstPacket(InputMessage inputMessage) throws IOException {
        onRecv(inputMessage);
    }

    @Override
    protected void onRecv(InputMessage inputMessage) {
        while(inputMessage.hasMore()) {
            short opCode = inputMessage.getU8();
            switch (opCode) {
                case Proto.Login.DISCONNECT:
                    processDisconnect(inputMessage);
                    break;
                case Proto.Login.MOTD:
                    processMotd(inputMessage);
                    break;
                case Proto.Login.SESSION_KEY:
                    processSessionKey(inputMessage);
                    break;
                case Proto.Login.CHAR_LIST:
                    processCharList(inputMessage);
                    break;
                default:
                    LOGGER.warn("Unrecognized opCode: {}", String.format("0x%x", opCode));
                    break;
            }
        }
        // its a login server no connection is maintained. Disconnect after receiving everything.
        disconnect();
    }

    private void processDisconnect(InputMessage inputMessage){
        String message = inputMessage.getString();
        LOGGER.info("Got disconnect with message: {}", message);
        disconnect();
    }

    private void processMotd(InputMessage inputMessage) {
        String motd = inputMessage.getString();
        LOGGER.info("Got motd: {}", motd);
    }

    private void processSessionKey(InputMessage inputMessage) {
        String sessionKey = inputMessage.getString();
        LOGGER.info("Got sessionKey: {}", sessionKey);
    }

    private void processCharList(InputMessage inputMessage) {
        int numberOfWorlds = inputMessage.getU8();
        List<World> worldList = new ArrayList<World>(numberOfWorlds);
        for (int i = 0; i < numberOfWorlds; i++) {
            int worldId = inputMessage.getU8();
            String worldName = inputMessage.getString();
            String worldIp = inputMessage.getString();
            int port = inputMessage.getU16();
            inputMessage.getU8(); // stopByte: 0
            worldList.add(new World(worldId, worldName, worldIp, port));

            LOGGER.info("Got world: {}, {}, {}, {}", worldId, worldName, worldIp, port);
        }

        int numberOfCharacters = inputMessage.getU8();
        List<com.mygdx.game.dunegon.game.login.Character> characterList = new ArrayList<com.mygdx.game.dunegon.game.login.Character>(numberOfCharacters);
        for (int i = 0; i < numberOfCharacters; i++) {
            int worldId  = inputMessage.getU8();
            String characterName = inputMessage.getString();
            characterList.add(new com.mygdx.game.dunegon.game.login.Character(worldId, characterName));

            LOGGER.info("Got chracter: {}", characterName);
        }

        inputMessage.getU8(); // stopByte: 0
        short premium = inputMessage.getU8();
        long premDays = inputMessage.getU32();

        synchronized (charList) {
            charList.setWorlds(worldList);
            charList.setCharacterList(characterList);
            charList.setPremium(premium == 1);
            charList.setPremDays(premDays);

            charList.notify();
        }
    }

    public void waitForCharList() {
        try {
            synchronized (charList) {
                charList.wait(20000);
            }
        } catch (InterruptedException ie) {
            LOGGER.error("interrupted waiting for charList", ie);
        }
    }

    public CharList getCharList() {
        return charList;
    }
}
