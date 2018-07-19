import java.util.Calendar;
import java.util.Date;

/**
 * Title: �ļ����֮ʱ��֮ʵ��������
 * Description: TestDemo
 *
 * @author: xg.chen
 * @date:2016��9��7��
 */
public class TimeStep {

    private Calendar calendar = Calendar.getInstance();

    private int feild = Calendar.SECOND;

    private int amount = 3;

    public int getFeild() {
        return feild;
    }

    public void setFeild(int feild) {
        this.feild = feild;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * MethodsTitle: ��ȡʱ��
     *
     * @return
     * @author: xg.chen
     * @date:2016��9��7��
     */
    public Date next() {
        calendar.add(feild, amount);
        return calendar.getTime();
    }

}