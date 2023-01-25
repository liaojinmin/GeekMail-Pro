import java.util.Random;

/**
 * 作者: 老廖
 * 时间: 2023/1/25
 **/
public class test {
    /**
     * 打印进度条
     *
     * @param progress 当前进度
     */
    public static void printProgressBar(int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("invalid progress = " + progress);
        }
        // 要想获得更好的通用性，在控制台打印的内容最好不要使用ASCII以外的字符，这些字符在不同环境下的长度不一样，导致效果不尽如人意，下同。
        String prefix = String.format("Current Progress%3d%%:", progress);
        StringBuilder refreshBar = new StringBuilder();
        int cnt = 100 + prefix.length();
        while (cnt-- > 0) {
            refreshBar.append("\b");
        }
        refreshBar.append(prefix);
        int remaining = 100 - progress;
        while (progress-- > 0) {
            refreshBar.append("_");
        }
        while (remaining-- > 0) {
            refreshBar.append("*");
        }
        System.out.print(refreshBar);
    }

    public static void main(String[] args) throws InterruptedException {
        // 测试进度条
        Random random = new Random();
        int remaining = 0;
        while (remaining < 100) {
            Thread.sleep(500);
            printProgressBar(remaining);
            remaining += random.nextInt(5);
            if (remaining > 100) {
                remaining = 100;
            }
        }
        Thread.sleep(500);
        printProgressBar(remaining);
    }
}
