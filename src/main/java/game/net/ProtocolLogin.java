package game.net;

import game.Config;

import java.io.IOException;

public class ProtocolLogin extends Protocol {

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

        outputMessage.addU8((char)0); // RSA
        // xtea key
        int[] xtea = getXteaKey();
        outputMessage.addU32(xtea[0]);
        outputMessage.addU32(xtea[1]);
        outputMessage.addU32(xtea[2]);
        outputMessage.addU32(xtea[3]);

        outputMessage.addString("username hehe xD");
        outputMessage.addString("password");

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
    protected void onRecv(InputMessage inputMessage) throws IOException {
        byte opCode = inputMessage.getU8();
        switch (opCode) {
            case (Proto.OpCode.DISCONNECT):
                processDisconnect(inputMessage);
                break;
            default:

        }
    }

    private void processDisconnect(InputMessage inputMessage) {
        String message = inputMessage.getString();
        System.out.println(message);
    }
}
