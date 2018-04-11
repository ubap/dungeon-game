package game.net;

import game.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ProtocolLogin extends Protocol {
    private Logger mLogger = LoggerFactory.getLogger(ProtocolLogin.class.getSimpleName());

    private String mUsername;
    private String mPassword;

    public ProtocolLogin(String username, String password) {
        mUsername = username;
        mPassword = password;
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

        outputMessage.addString(mUsername);
        outputMessage.addString(mPassword);

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
    protected void onRecv(InputMessage inputMessage) {
        while(inputMessage.hasMore()) {
            byte opCode = inputMessage.getU8();
            switch (opCode) {
                case Proto.OpCode.DISCONNECT:
                    processDisconnect(inputMessage);
                    break;
                case Proto.OpCode.MOTD:
                    processMotd(inputMessage);
                    break;
                case Proto.OpCode.SESSION_KEY:
                    processSessionKey(inputMessage);
                    break;
                case Proto.OpCode.CHAR_LIST:
                    processCharList(inputMessage);
                    break;
                default:
                    mLogger.warn("Unrecognized opCode: {}", String.format("0x%x", opCode));
                    break;
            }
        }
    }

    private void processDisconnect(InputMessage inputMessage){
        String message = inputMessage.getString();
        mLogger.info("Got disconnect with message: {}", message);

        try {
            disconnect();
        } catch (IOException ioe) {
            mLogger.error("Error while processing disconnect", ioe);
        }
    }

    private void processMotd(InputMessage inputMessage) {
        String motd = inputMessage.getString();
        mLogger.info("Got motd: {}", motd);
    }

    private void processSessionKey(InputMessage inputMessage) {
        String sessionKey = inputMessage.getString();
        mLogger.info("Got sessionKey: {}", sessionKey);
    }

    private void processCharList(InputMessage inputMessage) {
        int numberOfWorlds = inputMessage.getU8();
        for (int i = 0; i < numberOfWorlds; i++) {
            int worldId = inputMessage.getU8();
            String worldName = inputMessage.getString();
            String worldIp = inputMessage.getString();
            int port = inputMessage.getU16();
            inputMessage.getU8(); // stopByte: 0
            mLogger.info("Got world: {}, {}, {}, {}", worldId, worldName, worldIp, port);
        }

        int numberOfCharacters = inputMessage.getU8();
        for (int i = 0; i < numberOfCharacters; i++) {
            inputMessage.getU8(); // startByte: 0
            String characterName = inputMessage.getString();
            mLogger.info("Got chracter: {}", characterName);
        }

        inputMessage.getU8(); // stopByte: 0
        byte freePremium = inputMessage.getU8();
        int premDays = inputMessage.getU32();
    }
}
