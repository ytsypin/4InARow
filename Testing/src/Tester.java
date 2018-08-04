import sun.java2d.pipe.SpanShapeRenderer;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class Tester {
    public static void main(String[] args){
        long start = System.currentTimeMillis();

        try{
            Thread.sleep(400);

        } catch (Exception e){};

        long currDate = System.currentTimeMillis();

        Date elapsed = new Date(currDate-start);

        SimpleDateFormat sdf = new SimpleDateFormat("MM:SS");

        System.out.println(sdf.format(elapsed));

    }

}
