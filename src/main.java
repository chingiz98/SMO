import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;


public class main {
    static final double INF = 999999999;
    static int i1 = 0;
    static int i2 = 0;
    static int i3 = 0;

    static int na = 0;

    static int n = 0;
    static double t1 = INF;
    static double t2 = INF;
    static double t3 = INF;

    static double T = 540;
    static double lam = 2;
    static double t = 0;

    public static double randomInRange(double rangeMin, double rangeMax){
        Random r = new Random();
        double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        return randomValue;
    }

    public static double genTime(double t, double lambda){
        return t - 1 / (lambda * Math.log(randomInRange(0, 1)));
    }

    public static double min(double a, double b, double c, double d) {
        return min(a, min(b, min(c,d)));
    }

    public static double min(double a, double b)
    {
        return a > b ? b : a;
    }

    public static void setBusyness(int w1, int w2, int w3){
        i1 = w1;
        i2 = w2;
        i3 = w3;
    }

    public static void setState(int n1, int w1, int w2, int w3){
        n = n1;
        setBusyness(w1,w2,w3);
    }

    public static int max(Integer... vals) {
        return Collections.max(Arrays.asList(vals));
    }

    public static double interval(){
        double U = randomInRange(0, 1);
        return (-1/lam) * Math.log(U);
    }

    public static void main(String[] args) throws IOException {


        
        int c1 = 0;
        int c2 = 0;
        int c3 = 0;

        double ta = INF;


        ArrayList<Double> times = new ArrayList<>();


        System.out.println("123");

        ta = genTime(t, lam);

        while (ta < T || n > 0) {


            if(ta < t1 && ta < t2 && ta < t3){
                t = ta;
                ta = genTime(t, lam);

                na++;

                if(n == 0 && i1 == 0 && i2 == 0 && i3 ==0){
                    setState(1, na, 0, 0);
                    t1 = t + interval();
                    System.out.println("Next client came at " + t + " at win " + 1 + " n is " + n);
                } else if(n == 1 && i1 == 0 && i2 == 0 && i3 != 0){
                    setState(2, na, 0, i3);
                    t1 = t + interval();
                    System.out.println("Next client came at " + t + " at win " + 1 + " n is " + n);
                }  else if(n == 1 && i1 == 0 && i2 != 0 && i3 == 0){
                    setState(2, na, i2, 0);
                    t1 = t + interval();
                    System.out.println("Next client came at " + t + " at win " + 1 + " n is " + n);
                } else if(n == 2 && i1 == 0 && i2 != 0 && i3 != 0){
                    setState(3, na, i2, i3);
                    t1 = t + interval();
                    System.out.println("Next client came at " + t + " at win " + 1 + " n is " + n);
                } else if(n == 1 && i1 != 0 && i2 == 0 && i3 == 0){
                    setState(2, i1, na, 0);
                    t2 = t + interval();
                    System.out.println("Next client came at " + t + " at win " + 2 + " n is " + n);
                } else if(n == 2 && i1 != 0 && i2 == 0 && i3 != 0){
                    setState(3, i1, na, i3);
                    t2 = t + interval();
                    System.out.println("Next client came at " + t + " at win " + 2 + " n is " + n);
                } else if(n == 2 && i1 != 0 && i2 != 0 && i3 == 0){
                    setState(3, i1, i2, na);
                    t3 = t + interval();
                    System.out.println("Next client came at " + t + " at win " + 3 + " n is " + n);
                } else if (n > 2){
                    setState(n + 1, i1, i2, i3);
                    System.out.println("Next client came at " + t + " and PLACED TO QUEUE n is " + n);
                }
            }


            //уход1
            if(t1 < ta && t1 <= t2 && t1 <= t3){
                t = t1;
                c1++;
                System.out.println("Leaving window1 at " + t);
                if(n == 1){
                    setState(0, 0, 0, 0);
                    t1 = INF;
                } else if(n == 2 && i1 != 0 && i2 != 0 && i3 == 0){
                    setState(1, 0, i2, 0);
                    t1 = INF;
                } else if(n == 2 && i1 != 0 && i2 == 0 && i3 != 0){
                    setState(1, 0, 0, i3);
                    t1 = INF;
                } else if(n == 3){
                    setState(2, 0, i2, i3);
                    t1 = INF;
                } else if (n > 3){
                    setState(n - 1, max(i1, i2, i3) + 1, i2, i3);
                    t1 = t + interval();
                }
            }

            //уход2
            if(t2 < ta && t2 <= t1 && t2 <= t3){
                t = t2;
                c2++;
                System.out.println("Leaving window2 at " + t);

                if(n == 1){
                    setState(0, 0, 0, 0);
                    t2 = INF;
                } else if(n == 2 && i1 != 0 && i2 != 0 && i3 == 0){
                    setState(1, i1, 0, 0);
                    t2 = INF;
                } else if(n == 2 && i1 == 0 && i2 != 0 && i3 != 0){
                    setState(1, 0, 0, i3);
                    t2 = INF;
                } else if(n == 3){
                    setState(2, i1, 0, i3);
                    t2 = INF;
                } else if (n > 3){
                    setState(n - 1, i1, max(i1, i2, i3) + 1, i3);
                    t2 = t + interval();
                }
            }

            //уход3
            if(t3 < ta && t3 <= t2 && t3 <= t1){
                t = t3;
                c3++;
                System.out.println("Leaving window3 at " + t);

                if(n == 1){
                    setState(0, 0, 0, 0);
                    t3 = INF;
                } else if(n == 2 && i1 == 0 && i2 != 0 && i3 != 0){
                    setState(1, 0, i2, 0);
                    t3 = INF;
                } else if(n == 2 && i1 != 0 && i2 == 0 && i3 != 0){
                    setState(1, i1, 0, 0);
                    t3 = INF;
                } else if(n == 3){
                    setState(2, i1, i2, 0);
                    t3 = INF;
                } else if (n > 3){
                    setState(n - 1, i1, i2, max(i1, i2, i3) + 1);
                    t3 = t + interval();
                }
            }


        }



    }
}
