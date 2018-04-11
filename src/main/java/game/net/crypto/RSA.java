package game.net.crypto;

import java.math.BigInteger;

public class RSA {


    public static void encrypt(byte[] data, int offset, int len) {
        BigInteger bigInteger = new BigInteger("109120132967399429278860960508995541528237502902798129123468757937266291492576446330739696001110603907230888610072655818825358503429057592827629436413108566029093628212635953836686562675849720620786279431090218017681061521755056710823876476444260558147179707119674283982419152118103759076030616683978566631413",
                10);

        byte[] dataCpy = new byte[len];
        System.arraycopy(data, offset, dataCpy, 0, len);
        BigInteger dataBigInt = new BigInteger(1, dataCpy);



        dataBigInt = dataBigInt.modPow(new BigInteger("65537", 10), bigInteger);


        byte[] result = dataBigInt.toByteArray();
        if (result[0] == 0) {
            byte[] tmp = new byte[result.length - 1];
            System.arraycopy(result, 1, tmp, 0, tmp.length);
            result = tmp;
        }
        int i;
        for (i = 0; i < 128 - result.length; i++) {
            dataCpy[i] = 0;
        }
        System.arraycopy(result, 0, dataCpy, i, result.length);

        System.arraycopy(dataCpy, 0, data, offset, len);
    }
}
