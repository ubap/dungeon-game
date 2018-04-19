package com.mygdx.game.dunegon.crypto;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;

/**
 * Encryption engine that implements the XTEA encryption algorithm.
 * @author jiddo
 *
 */
public class XteaEncryptionEngine {
    public final static int
            rounds     = 32,
            keySize    = 16,
            blockSize  = 8;

    private final static int
            delta      = 0x9e3779b9,
            decryptSum = 0xc6ef3720;

    public int[] key;

    /**
     * Initializes this encryption engine with the given keys.
     * @param key
     * 			An array of keys to be used when encrypting and decrypting data using this
     * 			engine. Must contain exactly four elements.
     * @throws InvalidKeyException
     * 			If the key is null, or if the key does not contain exactly four elements.
     */
    public void init(int[] key) throws InvalidKeyException {
        if (key == null)
            throw new InvalidKeyException("Null key");

        if (key.length != keySize/4)
            throw new InvalidKeyException("Invalid key length (req. " + keySize + " bytes got "+
                    key.length*4+")");

        this.key = key.clone();
    }

    public void encrypt(byte[] buffer, int offset, int cycles) throws IOException
    {
        ByteBuffer in = ByteBuffer.wrap(buffer, offset, cycles * 8);
        ByteBuffer out = ByteBuffer.wrap(buffer, offset, cycles * 8);
        in.order(ByteOrder.LITTLE_ENDIAN);
        out.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < cycles; i++) {
            encrypt(in, out);
        }

    }

    public void encrypt(ByteBuffer in, ByteBuffer out) throws IOException {
        int v0 = in.getInt();
        int v1 = in.getInt();
        int sum = 0;

        for(int i = 0; i < rounds; i++) {
            v0 += ((((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + key[(int) (sum & 3)]));
            sum += delta;
            v1 += ((((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + key[(int) ((sum >>> 11) & 3)]));
        }
        out.putInt(v0);
        out.putInt(v1);
    }

    public void decrypt(byte[] buffer, int offset, int cycles) throws IOException {
        ByteBuffer in = ByteBuffer.wrap(buffer, offset, cycles * 8);
        ByteBuffer out = ByteBuffer.wrap(buffer, offset, cycles * 8);

        in.order(ByteOrder.LITTLE_ENDIAN);
        out.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < cycles; i++) {
            decrypt(in, out);
        }
    }

    public void decrypt(ByteBuffer in, ByteBuffer out) throws IOException {
        int v0 = in.getInt();
        int v1 = in.getInt();
        int sum = decryptSum;

        for(int i = 0; i < rounds; i++) {
            v1 -= ((((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + key[(int) ((sum >>> 11) & 3)]));
            sum -= delta;
            v0 -= ((((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + key[(int) (sum & 3)]));
        }
        out.putInt(v0);
        out.putInt(v1);
    }
}