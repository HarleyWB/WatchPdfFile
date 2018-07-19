import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;

import java.io.*;
import java.net.URI;
import java.util.*;


/**
 * Title: 文件监控之定时任务
 * Description: TestDemo
 *
 * @author: xg.chen
 * @date:2016年9月7日
 */
public class FileSchedulerTask implements Runnable {
    private boolean firstRun = true;
    //文件起始目录
    private String directory = "";
    // 初始文件信息
    private Map<String, Long> currentFiles = new HashMap<String, Long>();
    // 当前文件信息
    private Set<String> newFiles = new HashSet<String>();

    /**
     * 构造函数
     */
    public FileSchedulerTask() {

    }

    public FileSchedulerTask(String directory) {
        this.directory = directory;
    }

    public static int getpagesCount(String stringpath) {
        PdfReader pdfReader = null;
        try {
            pdfReader = new PdfReader(stringpath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int pages = pdfReader.getNumberOfPages();
        return pages;
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    //文件起始目录之get/set方法
    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    /**
     * 在 run() 中执行具体任务
     */
    public void run() {
        File file = new File(directory);
        if (firstRun) {
            firstRun = false;
            // 初次运行
            loadFileInfo(file);
            System.out.println("-----开始监控-----");
        } else {
            // 检查文件更新状态[add,update]
            try {
                try {
                    checkFileUpdate(file);
                } catch (NumberFormatException | ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (DocumentException e) {

                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            // 检查被移除的文件[remove]
            checkRemovedFiles();
            // 清空临时文件集合
            newFiles.clear();
        }

    }

    /**
     * MethodsTitle: 加载文件
     *
     * @param file
     * @author: xg.chen
     * @date:2016年9月7日
     */
    private void loadFileInfo(File file) {
        if (file.isFile()) {
            currentFiles.put(file.getAbsolutePath(), file.lastModified());
            return;
        }
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            loadFileInfo(files[i]);
        }
    }

    /**
     * MethodsTitle: 检查文件更新状态
     *
     * @param file
     * @throws IOException
     * @throws DocumentException
     * @throws ClassNotFoundException
     * @throws NumberFormatException
     * @author: xg.chen
     * @date:2016年9月7日
     */

    private void checkFileUpdate(File file) throws DocumentException, IOException, NumberFormatException, ClassNotFoundException {
        if (file.isFile()) {
            // 将当前文件加入到 newFiles 集合中
            newFiles.add(file.getAbsolutePath());
            //

            Long lastModified = currentFiles.get(file.getAbsolutePath());

            if (lastModified == null) {
                // 新加入文件
                currentFiles.put(file.getAbsolutePath(), file.lastModified());

                File fil = new File(file.getAbsolutePath());
                URI path = fil.toURI();
                File file2 = new File(path);
                String string = file2.toString();
                boolean b = judge(string);
                while (!b) {
                    try {
                        System.out.println("休眠开始");
                        Thread.sleep(3000);
                        b = judge(string);
                    } catch (InterruptedException e) {
                        System.out.println("不休眠");
                    }
                }
                System.out.println("添加文件:" + file.getAbsolutePath());
                //	System.out.println(file2.exists());
                // System.out.println(string);

                if (file2.exists()) ;
                {
                    int pages = getpagesCount(string);
                    System.out.println("共有" + pages + "页");


                    java.util.Random random = new java.util.Random();

                    String outpath = "D:\\pdfout\\" + getRandomString(random.nextInt(20)) + ".pdf";

                   InsertPdf.insert(string, outpath, "D:\\watermark.jpg",0,0,
                            595,841);
                    deleteFile(string);


                    String objpath = "D:\\1.obj";
                    int numb = getnum(objpath, pages);


                    try {
                        Runtime.getRuntime().exec("cmd.exe /C start acrord32 /P /h " + outpath);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }


                    try {  // System.out.println("删除休眠开始");
                        Thread.sleep(6000);
                        File file3 = new File(outpath);
                        // boolean ex=judge(outpath);
                        boolean ex = file3.exists();
                        while (ex) {
                            Thread.sleep(3000);
                            deleteFile(outpath);
                            ex = file3.exists();

                        }//while
                    } catch (InterruptedException e1) {
                        System.out.println("不休眠");
                    }


                }


                return;
            }
            if (lastModified.doubleValue() != file.lastModified()) {
                // 更新文件
                currentFiles.put(file.getAbsolutePath(), file.lastModified());
                System.out.println("更新文件:" + file.getAbsolutePath());
                return;
            }
            return;
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                // 没有子文件或子目录时返回
                return;
            }
            for (int i = 0; i < files.length; i++) {
                checkFileUpdate(files[i]);
            }
        }
    }

    /**
     * MethodsTitle: 检查被移除的文件
     *
     * @author: xg.chen
     * @date:2016年9月7日
     */
    public boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;

        }
        return flag;
    }

    public boolean judge(String s) {
        File file = new File(s);
        if (file.renameTo(file)) {
            return true;//正在使用

        }
        return false;


    }

    public int getnum(String path, int pages) throws FileNotFoundException, IOException, NumberFormatException, ClassNotFoundException {

        File file = new File(path);
        if (file.exists()) {  //反序列化对象
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            //读取字面值常量
            int a = Integer.parseInt((String) in.readObject());
            in.close();
            a -= pages;
            System.out.println("剩余次数： " + a);

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
            out.writeObject(String.valueOf(a));    //写入字面值常量
            out.close();
            return a;
        } else {

            //序列化对象
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
            out.writeObject("10000");    //写入字面值常量
            out.close();
            return 10000;
        }


    }


    private void checkRemovedFiles() {
        if (newFiles.size() == currentFiles.size()) {
            // 增加或更新时没有被移除的文件,直接返回
            return;
        }
        Iterator<String> it = currentFiles.keySet().iterator();
        while (it.hasNext()) {
            String filename = it.next();
            if (!newFiles.contains(filename)) {
                // 此处不能使用 currentFiles.remove(filename);从 map 中移除元素,
                // 否则会引发同步问题.
                // 正确的做法是使用 it.remove();来安全地移除元素.
                it.remove();
                System.out.println("删除文件:" + filename);
            }
        }
    }

}