import java.io.File;

/**
 * Title: 测试类
 * Description: TestDemo
 *
 * @author: xg.chen
 * @date:2016年9月7日
 */
public class TestMainClass {

    public static boolean judgepath(String path) {
        File dirname = new File(path);
        if (!dirname.exists()) { //目录不存在
            return false; //创建目录
        }
        return true;
    }

    public static void startadobe() {
        Runtime rt = Runtime.getRuntime();
        Process p = null;
        String fileLac = "";
        try {
            fileLac = "C:\\Program Files (x86)\\Adobe\\Acrobat Reader DC\\Reader\\AcroRd32.exe";//要调用的程序路径
            p = rt.exec(fileLac);
        } catch (Exception e) {
            System.out.println("open failure");
        }
    }


    public static void main(String[] args) {


        if (judgepath("D:\\1.jpg"))

        {
            startadobe();
            String pdfsrc = "D:\\pdfsrc";
            try {
                FileSchedulerTask task = new FileSchedulerTask(pdfsrc);
                FileScheduler fileScheduler = new FileScheduler();
                fileScheduler.schedule(task, new TimeStep());
            } catch (Exception e) {
                // TODO: handle exception
                System.out.println(pdfsrc + "不存在");
            }

        } else {

        }
    }
}