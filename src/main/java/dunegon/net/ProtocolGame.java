package dunegon.net;

import dunegon.Config;
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
        while(inputMessage.hasMore()) {
            byte opCode = inputMessage.getU8();
            switch (opCode) {

                default:
                    mLogger.warn("Unrecognized opCode: {}", String.format("0x%x", opCode));
                    break;
            }
        }
        return;
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

}
