import java.util.Calendar;
import java.util.TimeZone;

/**
 * 作者: 老廖
 * 时间: 2023/1/25
 **/
public class test {

    public static long getTodayStartTime() {
        //设置时区
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }


    public static void main(String[] args) throws InterruptedException {
        String ac = "Location{world=CraftWorld{name=ad},x=a}";
        String a = ac.substring(ac.indexOf("{"), ac.lastIndexOf("}"));
        String[] text = a.split(",");
        String w = text[0].substring(text[0].indexOf("name=")+5, text[0].lastIndexOf("}"));
        String x = text[1].substring(text[1].indexOf("=")+1);
        System.out.println("world= "+w+" x= "+x);


        System.out.println("---------------");
        System.out.println((int)((Math.random()*9+1)*100000));
        /*
        System.out.println("当前: "+System.currentTimeMillis());
        System.out.println("十二: "+getTodayStartTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy年 MM月 dd日 HH:mm:ss");
        System.out.println("格式化当前: "+format.format(System.currentTimeMillis()));
        System.out.println("格式化十二: "+format.format(getTodayStartTime()));

         */
    }
}
