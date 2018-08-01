public class Tester {
    public static void main(String[] args){

        long start = System.currentTimeMillis();
        try {
            Thread.sleep(2000);

            // Get elapsed time in milliseconds
            long elapsedTimeMillis = System.currentTimeMillis() - start;

            // Get elapsed time in minutes
            float elapsedTimeMin = elapsedTimeMillis / (60 * 1000F);
            System.out.println(elapsedTimeMin);
        }
        catch(InterruptedException e){

        }
    }
}
