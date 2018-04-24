package com.mygdx.game.dunegon.net;

import com.mygdx.game.dunegon.crypto.XteaEncryptionEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.zip.Adler32;

public abstract class Protocol {
    private static Logger LOGGER = LoggerFactory.getLogger(Protocol.class.getSimpleName());

    private Socket socket;
    private Thread receiveThread;
    private InputMessage inputMessage;
    private boolean firstPacket;

    private boolean checksumEnabled;
    private boolean xteaEnabled;
    private XteaEncryptionEngine xteaEncryptionEngine;
    private int[] xteaKey;

    public Protocol() {
        checksumEnabled = false;
        xteaEnabled = false;
        firstPacket = true;
    }

    public void connect(String host, int port) throws IOException {
        inputMessage = new InputMessage();
        socket = new Socket(host, port);
        onConnect();
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException io) {
            LOGGER.error("error disconnecting", io);
        } finally {
            socket = null;
        }
    }

    public boolean isConnected() {
        if (socket == null || !socket.isConnected()) {
            return false;
        }
        return true;
    }

    public void enableChecksum() {
        checksumEnabled = true;
    }

    public void enableXtea() {
        xteaEnabled = true;
        getXteaKey();

        xteaEncryptionEngine = new XteaEncryptionEngine();
        try {
            xteaEncryptionEngine.init(xteaKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int[] getXteaKey() {
        if (xteaKey == null) {
            xteaKey = new int[4];
            Random random = new Random();
            xteaKey[0] = random.nextInt();
            xteaKey[1] = random.nextInt();
            xteaKey[2] = random.nextInt();
            xteaKey[3] = random.nextInt();
        }
        return xteaKey;
    }

    public void send(OutputMessage outputMessage) {
        try {
            if (xteaEnabled) {
                outputMessage.writeMessageSize();
                int xteaLength = outputMessage.getMessageSize();
                int padding = 8 - (xteaLength % 8);
                outputMessage.addPaddingBytes(padding);

                xteaLength = outputMessage.getMessageSize();
                int cycles = xteaLength / 8;

                xteaEncryptionEngine.encrypt(outputMessage.getBuffer().array(),
                        outputMessage.getHeaderPos() + outputMessage.getHeaderSize() - 2, cycles);
            }


            if (checksumEnabled) {
                outputMessage.writeChecksum();
            }

            outputMessage.writeMessageSize();

            socket.getOutputStream().write(outputMessage.getBuffer().array(), outputMessage.getHeaderPos(),
                    outputMessage.getMessageSize());

        } catch (IOException io) {
            disconnect();
            LOGGER.error("error send", io);
        }

    }

    protected void startReceiving() {
        receiveThread = new RecvThread();
        receiveThread.start();
    }

    private void receive() throws IOException {
        inputMessage.reset();
        int headerSize = 2; // packet size
        if (checksumEnabled) {
            headerSize += 4; // 4 bytes for checksum
        }
        if (xteaEnabled) {
            headerSize += 2;
        }

        int read = socket.getInputStream().read(inputMessage.getBuffer(), 0, 2); // packet size
        if (read != 2) {
            disconnect();
            return;
        }
        int packetSize = inputMessage.getU16();
        inputMessage.setMessageSize(packetSize + 2);

        while (read != packetSize + 2) {
            read += socket.getInputStream().read(inputMessage.getBuffer(), read, packetSize + 2 - read); // the rest of the packet
            if (read <= 0) {
                disconnect();
                return;
            }
        }

        long checksum = inputMessage.getU32();

        Adler32 adler32 = new Adler32();
        adler32.update(inputMessage.getBuffer(), 6, packetSize - 4);
        if (checksum != adler32.getValue()) {
            throw new RuntimeException("checksum mismatch");
        }

        if (xteaEnabled) {
            xteaEncryptionEngine.decrypt(inputMessage.getBuffer(), 6, 1);
            int xteaLength = inputMessage.getU16() + 2;
            inputMessage.setMessageSize(xteaLength + 6); // proper message length is now known, use it
            // xteaLength += 8 - (xteaLength % 8);
            int cycles = xteaLength / 8;

            xteaEncryptionEngine.decrypt(inputMessage.getBuffer(), 14, cycles);
        }

        if (headerSize != inputMessage.getPosition()) {
            throw new RuntimeException("incorrect header size");
        }

        // process the message further
        if (firstPacket) {
            onRecvFirstPacket(inputMessage);
            firstPacket = false;
        }
        else {
            onRecv(inputMessage);
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
                LOGGER.info("Connection closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
