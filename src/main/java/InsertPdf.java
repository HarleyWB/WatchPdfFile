

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class InsertPdf {


    private static final int wdDoNotSaveChanges = 0;//
    private static final int wdFormatPDF = 17;// wordתPDF ��ʽ

    //word转pdf
    private final static boolean word2pdf(String source, String target) {
        ActiveXComponent app = null;
        try {
            app = new ActiveXComponent("Word.Application");
            app.setProperty("Visible", false);
            Dispatch docs = app.getProperty("Documents").toDispatch();
            Dispatch doc = Dispatch.call(docs, "Open", source, false, true).toDispatch();
            File tofile = new File(target);
            if (tofile.exists()) {
                tofile.delete();
            }
            Dispatch.call(doc, "SaveAs", target, wdFormatPDF);
            Dispatch.call(doc, "Close", false);
            return true;
        } catch (Exception e) {

            return false;
        } finally {
            if (app != null) {
                app.invoke("Quit", wdDoNotSaveChanges);
            }
        }
    }

    //将图片插入pdf
    private final static void insertToPdf(String FILE_DIR, String FILE_out, String imgdir, float x,
                                          float y,
                                          float X, float Y)
            throws
            DocumentException, IOException {
        Image img = Image.getInstance(imgdir);
        PdfReader reader = new PdfReader(FILE_DIR);
        int pagecount = reader.getNumberOfPages();//获取pdf页数
        Rectangle a = reader.getPageSize(1);
        //@SuppressWarnings("deprecation")
        float sizex = a.getWidth();
        float sizey = a.getHeight();

        PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(FILE_out
        ));
        //定义插入位置 、大小（上层方法已经定义）
        // float x = (float) 940.2, y = (float) 64, X = (float) 61, Y = (float) 61;

        img.setAbsolutePosition(x, y);
        //img.setRotationDegrees(45);// 旋转角度  没用
        img.scaleAbsolute(X, Y);//设置图片大小

        //img2.scalePercent(20);
        try {
            for (int i = 1; i <= pagecount; i++) {//循环插入pdf   i代表页数（从1开始，否则空指针异常服了）
                PdfContentByte pdfContentByte = stamp.getUnderContent(i);//插入pdf背景
                //PdfContentByte pdfContentByte = stamp.getOverContent(i);//插入pdf前景
                pdfContentByte.addImage(img);
            }
        } finally {
            stamp.close();
            reader.close();
        }
    }

    //删除临时文件
    private static final boolean deleteFile(String fileName) {
        File file = new File(fileName);

        if (file.exists() && file.isFile()) {
            if (file.delete()) {

                return true;
            } else {

                return false;
            }
        } else {

            return false;
        }
    }

    //判断文件是doc、docx、pdf哪一种。若是doc或docx 先转成pdf再插入背景

    public final static void insert(String FILE_DIR, String FILE_out, String imgpath, float x,
                                    float y,
                                    float X, float Y) throws
            DocumentException, IOException {
        File f = new File(FILE_DIR);
        if (!f.exists()) {
            //  System.out.println("文件不存在");
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "文件不存在", "文件不存在", JOptionPane.ERROR_MESSAGE);
            return ;

        }

        int len = FILE_DIR.length();

        String suffix = FILE_DIR.substring(FILE_DIR.lastIndexOf(".") + 1);

        System.out.println(suffix);
        if ("doc".equals(suffix)) {


            String tmp = FILE_DIR.substring(0, len - 4);
            String target = tmp + ".pdf";

            word2pdf(FILE_DIR, target);
            insertToPdf(target, FILE_out, imgpath, x, y, X, Y);
            File file = new File(target);
            if (file.exists()) {

                deleteFile(target);
            }
            System.out.println("成功");
            Toolkit.getDefaultToolkit().beep();

        }
        if ("docx".equals(suffix)) {

            String tmp = FILE_DIR.substring(0, len - 5);
            String target = tmp + ".pdf";

            word2pdf(FILE_DIR, target);
            insertToPdf(target, FILE_out, imgpath, x, y, X, Y);
            File file = new File(target);
            if (file.exists()) {

                deleteFile(target);
            }
            System.out.println("成功");
            Toolkit.getDefaultToolkit().beep();


        }

        if ("pdf".equals(suffix)) {

            insertToPdf(FILE_DIR, FILE_out, imgpath, x, y, X, Y);
            System.out.println("成功");
            Toolkit.getDefaultToolkit().beep();

        }

    }


}
