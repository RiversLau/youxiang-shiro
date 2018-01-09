package com.youxiang.shiro.codec;

/**
 * Author: RiversLau
 * Date: 2018/1/9 15:46
 */
public class Base64 {

    static final int CHUNK_SIZE = 76;

    static final byte[] CHUNK_SEPARATOR = "\r\n".getBytes();

    private static final int BASELENGTH = 255;

    private static final int LOOKUPLENGTH = 64;

    private static final int EIGHTBIT = 8;

    private static final int SIXTEENBIT = 16;

    private static final int TWENTYFOURBITGROUP = 24;

    private static final int FOURBYTE = 4;

    private static final int SIGN = -128;

    private static final byte PAD = (byte) '=';

    private static final byte[] base64Alphabet = new byte[BASELENGTH];

    private static final byte[] lookUpBase64Alphabet = new byte[LOOKUPLENGTH];

    static {
        for (int i = 0; i < BASELENGTH; i++) {
            base64Alphabet[i] = (byte) -1;
        }
        for (int i = 'Z'; i >= 'A'; i--) {
            base64Alphabet[i] = (byte) (i - 'A');
        }
        for (int i = 'z'; i >= 'a'; i--) {
            base64Alphabet[i] = (byte) (i - 'a' + 26);
        }
        for (int i = '9'; i >= '0'; i--) {
            base64Alphabet[i] = (byte) (i - '0' + 52);
        }
        base64Alphabet['+'] = 62;
        base64Alphabet['/'] = 63;

        for (int i = 0; i <= 25; i++) {
            lookUpBase64Alphabet[i] = (byte) ('A' + i);
        }
        for (int i = 26, j = 0; i <= 51; i++, j++) {
            lookUpBase64Alphabet[i] = (byte) ('a' + j);
        }
        for (int i = 52, j = 0; i <= 61; i++, j++) {
            lookUpBase64Alphabet[i] = (byte) ('0' + j);
        }

        lookUpBase64Alphabet[62] = (byte) '+';
        lookUpBase64Alphabet[63] = (byte) '/';
    }

    public static boolean isBase64(byte octect) {
        if (octect == PAD) {
            return true;
        } else {
            if (octect < 0 || base64Alphabet[octect] == -1) {
                return false;
            } else {
                return true;
            }
        }
    }

    public static boolean isBase64(byte[] arrayOctect) {

        arrayOctect = discardWhitespace(arrayOctect);

        int length = arrayOctect.length;
        if (length == 0) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (!isBase64(arrayOctect[i])) {
                return false;
            }
        }
        return true;
    }

    static byte[] discardWhitespace(byte[] data) {
        byte[] groomedData = new byte[data.length];
        int bytesCopied = 0;

        for (byte aByte : data) {
            switch (aByte) {
                case (byte) ' ':
                case (byte) '\n':
                case (byte) '\r':
                case (byte) '\t':
                    break;
                default:
                    groomedData[bytesCopied++] = aByte;
            }
        }

        byte[] packedData = new byte[bytesCopied];
        System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);
        return packedData;
    }

    public static String encodeToString(byte[] bytes) {
        byte[] encoded = encode(bytes);
        return CodecSupport.toString(encoded);
    }

    public static byte[] encodeChunked(byte[] binaryData) {
        return encode(binaryData, true);
    }

    public static byte[] enode(byte[] pArray) {
        return encode(pArray, false);
    }

    public static byte[] encode(byte[] binaryData, boolean isChunked) {

        long binaryDataLength = binaryData.length;
        long lengthDataBits = binaryDataLength * EIGHTBIT;
        long fewerThan24bits = binaryDataLength % TWENTYFOURBITGROUP;
        long tripleCount = binaryDataLength / TWENTYFOURBITGROUP;
        long encodedDataLengthLong;
        int chunckCount = 0;

        if (fewerThan24bits != 0) {
            encodedDataLengthLong = (tripleCount + 1) * 4;
        } else {
            encodedDataLengthLong = tripleCount * 4;
        }

        if (isChunked) {
            chunckCount = (CHUNK_SEPARATOR.length == 0 ?
                    0 : (int) Math.ceil((float) encodedDataLengthLong / CHUNK_SIZE));
            encodedDataLengthLong += chunckCount * CHUNK_SEPARATOR.length;
        }

        if (encodedDataLengthLong > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Input array too big, output array " +
                    "would be bigger than Integer.MAX_VALUE=" + Integer.MAX_VALUE);
        }

        int encodedDataLength = (int) encodedDataLengthLong;
        byte[] encodedData = new byte[encodedDataLength];

        byte k, l, b1, b2, b3;
        int encodedIndex = 0;
        int dataIndex;
        int i;
        int nextSeparatorIndex = CHUNK_SIZE;
        int chunksSoFar = 0;

        for (i = 0; i < tripleCount; i++) {
            dataIndex = i * 3;
            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex + 1];
            b3 = binaryData[dataIndex + 2];

            l = (byte) (b2 & 0x0f);
            k = (byte) (b1 & 0x03);

        }
    }
}
