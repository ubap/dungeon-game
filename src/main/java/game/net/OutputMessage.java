package game.net;

import game.net.crypto.RSA;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.Adler32;

public class OutputMessage {
    private ByteBuffer mByteBuffer;
    private short mHeaderPos;
    private short mMessageSize;
    private short mWritePos;

    public OutputMessage() {
        mByteBuffer = ByteBuffer.allocate(1024);
        mByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        mHeaderPos = 8;
        mMessageSize = 0;
        mWritePos = mHeaderPos;
    }

    public void addU8(char value) {
        mByteBuffer.putChar(mWritePos, value);
        mWritePos += 1;
        mMessageSize += 1;
    }

    public void addU16(short value) {
        mByteBuffer.putShort(mWritePos, value);
        mWritePos += 2;
        mMessageSize += 2;
    }

    public void addU32(int value) {
        mByteBuffer.putInt(mWritePos, value);
        mWritePos += 4;
        mMessageSize += 4;
    }

    public void addString(String value) {
        int length = value.length();
        addU16((short) length);

        mByteBuffer.position(mWritePos);
        mByteBuffer.put(value.getBytes(), 0, length);
        mWritePos += length;
        mMessageSize += length;
    }

    public void addPaddingBytes(int bytes) {
        for (int i = 0; i < bytes; i++) {
            addU8((char)0);
        }
    }

    public void encryptRsa() {
        if (mMessageSize < 128)
            throw new RuntimeException("not enough bytes to encrypt");

        RSA.encrypt(mByteBuffer.array(), mWritePos - 128, 128);
    }

    public void writeChecksum() {
        Adler32 adler32 = new Adler32();
        adler32.update(mByteBuffer.array(), mHeaderPos, mMessageSize);
        int checksum = (int) adler32.getValue();

        mHeaderPos -= 4;
        mByteBuffer.putInt(mHeaderPos, checksum);
        mMessageSize += 4;
    }

    public void writeMessageSize() {
        mHeaderPos -= 2;
        mByteBuffer.putShort(mHeaderPos, mMessageSize);
        mMessageSize += 2;
    }

    public ByteBuffer getBuffer() {
        return mByteBuffer;
    }

    public short getHeaderPos() {
        return mHeaderPos;
    }

    public short getMessageSize() {
        return mMessageSize;
    }
}
