/*
 * 作者: 老廖
 * 时间: 2022/10/16
 * copy by https://blog.csdn.net/weixin_41755556/article/details/121472206
 */
package me.geek.mail.serializable;
import java.io.*;

public class SerializeUtil {

    public static byte[] serialize(Object object) {
        ObjectOutputStream a;
        ByteArrayOutputStream b;
        byte[] bytes = null;
        try {
            b = new ByteArrayOutputStream();
            a = new ObjectOutputStream(b);
            a.writeObject(object);
            bytes = b.toByteArray();
        } catch (Exception ignored) {}
        return bytes;
    }


    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b;
        ObjectInputStream c = null;
        try {
            b = new ByteArrayInputStream(bytes);
            c = new ObjectInputStream(b);
        } catch (Exception ignored) {}
        return c.readObject();
    }

    public static String toHexString(byte[] byteArray) {

        if(byteArray == null || byteArray.length < 1) {
            throw new IllegalArgumentException("this byteArray must not be null or empty");
        }

        final StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray) {
            if ((b & 0xff) < 0x10) {
                hexString.append("0");
            }
            hexString.append(Integer.toHexString(0xFF & b));
        }
        return hexString.toString().toLowerCase();
    }


    public static byte[] toByteArray(String hexString) {
        if(hexString.isEmpty()) {
            throw new IllegalArgumentException("this hexString must not be empty");
        }
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

}
