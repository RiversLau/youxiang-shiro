package com.youxiang.shiro.util;

import java.io.File;
import java.io.InputStream;

/**
 * Author: RiversLau
 * Date: 2018/1/9 16:35
 */
public interface ByteSource {

    byte[] getBytes();

    String toHex();

    String toBase64();

    boolean isEmpty();

    final class Util {
        public static ByteSource bytes(byte[] bytes) {
            return new SimpleByteSource(bytes);
        }

        public static ByteSource bytes(char[] chars) {
            return new SimpleByteSource(chars);
        }

        public static ByteSource bytes(String string) {
            return new SimpleByteSource(string);
        }

        public static ByteSource bytes(ByteSource byteSource) {
            return new SimpleByteSource(byteSource);
        }

        public static ByteSource bytes(File file) {
            return new SimpleByteSource(file);
        }

        public static ByteSource bytes(InputStream in) {
            return new SimpleByteSource(in);
        }

        public static boolean isCompatible(Object source) {
            return SimpleByteSource.isCompatible(source);
        }

        public static ByteSource bytes(Object source) throws IllegalArgumentException {
            if (source == null) {
                return null;
            }
            if (!isCompatible(source)) {
                String msg = "Unable to heuristically acquire bytes for object of type [" +
                        source.getClass().getName() + "].  If this type is indeed a byte-backed data type, you might " +
                        "want to write your own ByteSource implementation to extract its bytes explicitly.";
                throw new IllegalArgumentException(msg);
            }
            if (source instanceof byte[]) {
                return bytes((byte[]) source);
            } else if (source instanceof char[]) {
                return bytes((char[]) source);
            } else if (source instanceof String) {
                return bytes((String) source);
            } else if (source instanceof File) {
                return bytes((File) source);
            } else if (source instanceof InputStream) {
                return bytes((InputStream) source);
            } else {
                throw new IllegalStateException("Encountered unexpected byte source.  This is a bug - please notify " +
                        "the Shiro developer list asap (the isCompatible implementation does not reflect this " +
                        "method's implementation).");
            }
        }
    }
}
