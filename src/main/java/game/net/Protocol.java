package game.net;


import game.net.crypto.XTEA;
import game.net.crypto.XteaEncryptionEngine;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.util.Random;
import java.util.zip.Adler32;

public abstract class Protocol {
    private Socket mConnection;
    private Thread mRecvThread;
    private InputMessage mInputMessage;

    private boolean mChecksumEnabled;
    private boolean mXteaEnabled;
    private XteaEncryptionEngine mXteaEncryptionEngine;
    private int[] mXteaKey;

    public Protocol() {
        mChecksumEnabled = false;
        mXteaEnabled = false;
    }

    public void connect(String host, int port) throws IOException {
        mInputMessage = new InputMessage();
        mConnection = new Socket(host, port);
        onConnect();
    }

    public void disconnect() {

    }

    public boolean isConnected() {
        if (mConnection == null || !mConnection.isConnected()) {
            return false;
        }
        return true;
    }

    public void enableChecksum() {
        mChecksumEnabled = true;
    }

    public void enableXtea() {
        mXteaEnabled = true;
        getXteaKey();

        mXteaEncryptionEngine = new XteaEncryptionEngine();
        try {
            mXteaEncryptionEngine.init(mXteaKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int[] getXteaKey() {
        if (mXteaKey == null) {
            mXteaKey = new int[4];
            Random random = new Random();
            mXteaKey[0] = 1;
            mXteaKey[1] = 2;
            mXteaKey[2] = 3;
            mXteaKey[3] = 4;
        }
        return mXteaKey;
    }

    public void send(OutputMessage outputMessage) throws IOException {
        if (mChecksumEnabled) {
            outputMessage.writeChecksum();
        }

        outputMessage.writeMessageSize();

        mConnection.getOutputStream().write(outputMessage.getBuffer().array(), outputMessage.getHeaderPos(),
                outputMessage.getMessageSize());

    }

    protected void startReceiving() {
        mRecvThread = new RecvThread();
        mRecvThread.start();
    }

    private void receive() throws IOException {
        mInputMessage.reset();
        int headerSize = 2; // packet size
        if (mChecksumEnabled) {
            headerSize += 4; // 4 bytes for checksum
        }
        if (mXteaEnabled) {
            headerSize += 2;
        }

        mConnection.getInputStream().read(mInputMessage.getBuffer(), 0, 2); // packet size
        int packetSize = mInputMessage.getU16();

        mConnection.getInputStream().read(mInputMessage.getBuffer(), 2, packetSize); // the rest of the packet
        int checksum = mInputMessage.getU32();

        Adler32 adler32 = new Adler32();
        adler32.update(mInputMessage.getBuffer(), 6, packetSize - 4);
        if (checksum != (int) adler32.getValue()) {
            throw new RuntimeException("checksum mismatch");
        }

        if (mXteaEnabled) {
            mXteaEncryptionEngine.decrypt(mInputMessage.getBuffer(), 6, 1);
            int xteaLength = mInputMessage.getU16();
            xteaLength = xteaLength - 8;
            int cycles = xteaLength / 8;
            if (xteaLength % 8 > 0) {
                cycles++;
            }

            mXteaEncryptionEngine.decrypt(mInputMessage.getBuffer(), 14, cycles);
        }

        if (headerSize != mInputMessage.getPosition()) {
            throw new RuntimeException("incorrect header size");
        }

        // process the message further
        onRecv(mInputMessage);
    }


    abstract protected void onConnect() throws IOException;

    abstract protected void onRecv(InputMessage inputMessage) throws IOException;

    private class RecvThread extends Thread {
        @Override
        public void run() {
            try {
                while (mConnection.isConnected()) {
                    receive();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
