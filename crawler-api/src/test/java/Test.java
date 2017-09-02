/**
 * Created by slj on 17/1/22.
 */
public class Test {


    public static void main(String[] args) {
        Integer i = new Integer(1);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Integer temp = i;
        i=10;
        temp=6;
    }
}
