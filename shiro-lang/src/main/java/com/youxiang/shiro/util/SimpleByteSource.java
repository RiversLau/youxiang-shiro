package com.youxiang.shiro.util;

import com.youxiang.shiro.codec.Base64;
import com.youxiang.shiro.codec.CodecSupport;
import com.youxiang.shiro.codec.Hex;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Author: RiversLau
 * Date: 2018/1/10 10:58
 */
public class SimpleByteSource implements ByteSource {

    private final byte[] bytes;
    private String cachedHex;
    private String cachedBase64;

    public SimpleByteSource(byte[] bytes) {
        this.bytes = bytes;
    }

    public SimpleByteSource(char[] chars) {
        this.bytes = CodecSupport.toBytes(chars);
    }

    public SimpleByteSource(String str) {
        this.bytes = CodecSupport.toBytes(str);
    }

    public SimpleByteSource(ByteSource byteSource) {
        this.bytes = byteSource.getBytes();
    }

    public SimpleByteSource(File file) {
        this.bytes = new BytesHelper().getBytes(file);
    }

    public SimpleByteSource(InputStream stream) {
        this.bytes = new BytesHelper().getBytes(stream);
    }

    public static boolean isCompatible(Object o) {
        return o instanceof byte[] || o instanceof char[] || o instanceof String ||
                o instanceof ByteSource || o instanceof File || o instanceof InputStream;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public boolean isEmpty() {
        return this.bytes == null || this.bytes.length == 0;
    }

    public String toHex() {
        if (this.cachedHex == null) {
            this.cachedHex = Hex.encodeToString(getBytes());
        }
        return this.cachedHex;
    }

    public String toBase64() {
        if (this.cachedBase64 == null) {
            this.cachedBase64 = Base64.encodeToString(getBytes());
        }
        return cachedBase64;
    }

    public String toString() {
        return toBase64();
    }

    public int hashCode() {
        if (this.bytes == null || this.bytes.length == 0) {
            return 0;
        }
        return Arrays.hashCode(this.bytes);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ByteSource) {
            ByteSource bs = (ByteSource) o;
            return Arrays.equals(getBytes(), bs.getBytes());
        }
        return false;
    }

    private static final class BytesHelper extends CodecSupport {
        public byte[] getBytes(File file) {
            return toBytes(file);
        }

        public byte[] getBytes(InputStream stream) {
            return toBytes(stream);
        }
    }
}
