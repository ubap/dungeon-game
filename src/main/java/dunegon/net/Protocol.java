package dunegon.net;


import dunegon.crypto.XteaEncryptionEngine;
import jdk.internal.util.xml.impl.Input;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.zip.Adler32;

public abstract class Protocol {
    private Socket mConnection;
    private Thread mRecvThread;
    private InputMessage mInputMessage;
    private boolean mFirstPacket;

    private boolean mChecksumEnabled;
    private boolean mXteaEnabled;
    private XteaEncryptionEngine mXteaEncryptionEngine;
    private int[] mXteaKey;

    public Protocol() {
        mChecksumEnabled = false;
        mXteaEnabled = false;
        mFirstPacket = true;
    }

    public void connect(String host, int port) throws IOException {
        mInputMessage = new InputMessage();
        mConnection = new Socket(host, port);
        onConnect();
    }

    public void disconnect() throws IOException {
        mConnection.close();
        mConnection = null;
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

        if (mXteaEnabled) {
            outputMessage.writeMessageSize();
            int xteaLength = outputMessage.getMessageSize();
            int padding = 8 - (xteaLength % 8);
            outputMessage.addPaddingBytes(padding);

            xteaLength = outputMessage.getMessageSize();
            int cycles = xteaLength / 8;

            mXteaEncryptionEngine.encrypt(outputMessage.getBuffer().array(),
                    outputMessage.getHeaderPos() + outputMessage.getHeaderSize() - 2, cycles);
        }


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
        mInputMessage.setMessageSize(packetSize + 2);

        mConnection.getInputStream().read(mInputMessage.getBuffer(), 2, packetSize); // the rest of the packet
        int checksum = mInputMessage.getU32();

        Adler32 adler32 = new Adler32();
        adler32.update(mInputMessage.getBuffer(), 6, packetSize - 4);
        if (checksum != (int) adler32.getValue()) {
            throw new RuntimeException("checksum mismatch");
        }

        if (mXteaEnabled) {
            mXteaEncryptionEngine.decrypt(mInputMessage.getBuffer(), 6, 1);
            int xteaLength = mInputMessage.getU16() + 2;
            mInputMessage.setMessageSize(xteaLength + 6); // proper message length is now known, use it
            // xteaLength += 8 - (xteaLength % 8);
            int cycles = xteaLength / 8;

            mXteaEncryptionEngine.decrypt(mInputMessage.getBuffer(), 14, cycles);
        }

        if (headerSize != mInputMessage.getPosition()) {
            throw new RuntimeException("incorrect header size");
        }

        // process the message further
        if (mFirstPacket) {
            onRecvFirstPacket(mInputMessage);
            mFirstPacket = false;
        }
        else {
            onRecv(mInputMessage);
        }
    }


    abstract protected void onConnect() throws IOException;
    abstract protected void onRecvFirstPacket(InputMessage inputMessage) throws IOException ;
    abstract protected void onRecv(InputMessage inputMessage);

    private class RecvThread extends Thread {
        @Override
        public void run() {
            try {
                while (Protocol.this.isConnected()) {
                    receive();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
