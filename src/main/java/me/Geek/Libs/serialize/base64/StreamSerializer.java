package me.Geek.Libs.serialize.base64;

import me.Geek.GeekMail;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * 作者: 老廖
 * 时间: 2022/7/28
 **/
public class StreamSerializer {
    //序列化
    /*
    public static String serializeItemStack(ItemStack item) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        try (BukkitObjectOutputStream out = new BukkitObjectOutputStream(b)) {
            out.writeObject(serialize(item));
            return Base64Coder.encodeLines(b.toByteArray());
        } catch (IOException e) {
            throw new IllegalArgumentException("无法序列化物品堆栈数据");
        }
    }
     */

    public static String serializeItemStacks(ItemStack[] item) {
        if (item.length == 0) {
            return "";
        }

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        try (BukkitObjectOutputStream bukkitOutputStream = new BukkitObjectOutputStream(byteOutputStream)) {

            bukkitOutputStream.writeInt(item.length);
            for (ItemStack items : item) {
                bukkitOutputStream.writeObject(serialize(items));
            }
            return Base64Coder.encodeLines(byteOutputStream.toByteArray());
        } catch (IOException e) {
            throw new IllegalArgumentException("无法序列化物品堆栈数据");
        }
    }

    //反序列化 获取单个物品stack
    public static ItemStack deserializeItemStack(String item) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(Base64Coder.decodeLines(item))) {
            try (BukkitObjectInputStream bukkitInputStream = new BukkitObjectInputStream(byteInputStream)) {
                return deserialize(bukkitInputStream.readObject());
            }
        }
    }
    //反序列化 获取stack数组
    public static ItemStack[] deserializeItemStacks(String item) throws IOException, ClassNotFoundException {
        if (item.equals("null") || item.isEmpty()) {
            return new ItemStack[0];
        }

    //    GeekMail.say(item);
        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(Base64Coder.decodeLines(item))) {

            try (BukkitObjectInputStream bukkitInputStream = new BukkitObjectInputStream(byteInputStream)) {

                ItemStack[] Contents = new ItemStack[bukkitInputStream.readInt()];
                int index = 0;
                for (ItemStack ignored : Contents) {
                    Contents[index] = deserialize(bukkitInputStream.readObject());
                    index++;
                }
                return Contents;
            }
        }
    }


    @SuppressWarnings("unchecked")
    private static ItemStack deserialize(Object item) {
        return item != null ? ItemStack.deserialize((Map<String, Object>) item) : null;
    }

    private static Map<String, Object> serialize(ItemStack item) {
        return item != null ? item.serialize() : null;
    }
}
