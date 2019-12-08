import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;


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
    static double lam = 4;
    static double t = 0;

    static ArrayList<Double> times = new ArrayList<Double>();

    static double[] cameTimes = new double[1000000];
    static double[] waitTimes = new double[1000000];

    static double[] tempTimes = {1.0, 1.0, 1.0, 2.0, 3.5, 3.7, 4.0};
    static double[] servTimes = {3.0, 2.0, 4.0, 3.0, 3.0, 2.0, 2.0};
    static int count = 0;

    static double servTime = 0;
    static double sTimes[] = new double[99999];
    static double leaveTimes[] = new double[99999];



    public static double randomInRange(double rangeMin, double rangeMax){
        Random r = new Random();
        double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        return randomValue;
    }

    public static double genTime(double time, double lambda) {

        double U2 = 0;
        double U1 = 0;

        do {
            U1 = randomInRange(0, 1);
            time = time - 1 / (lambda * Math.log(U1));
            U2 = randomInRange(0, 1);
            //System.out.println("U2= " + U2 + " LAMBDA " + (intensity(time) / lambda));

        } while (U2 > (intensity(time) / lambda));

        return time;



        //return time - 1 / (lambda * Math.log(randomInRange(0, 1)));
    }

    public static double intensity(double time) {
        if(time < 120){
            return lam / 2;
        } else if (time >= 120 && time < 240){
            return lam / 3;
        } else if (time >= 240 && time < 360){
            return lam / 4;
        } else {
            return lam;
        }
    }

    public static void printTimes(){
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        int count4 = 0;

        for(int i = 0; i < times.size(); i++){
            if(times.get(i) < 120)
                count1++;
            if(times.get(i) >= 120 && times.get(i) < 240)
                count2++;
            if(times.get(i) >= 240 && times.get(i) < 360)
                count3++;
            if(times.get(i) >= 360)
                count4++;
        }

        System.out.println("Times " + count1 + " " + count2 + " " + count3 + " " + count4);
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

    public static double interval(int index){

        double prob = randomInRange(0, 1);

        double ret = 0;

        if(prob < 0.8){


            ret = randomInRange(2, 4);
        } else if (prob >= 0.8 && prob < 0.9){
            ret = randomInRange(4, 7);
        } else {
            ret = randomInRange(7, 15);
        }

        servTime += ret;

        sTimes[index] = ret;

        return ret;

        /*
        double U = randomInRange(0, 1);
        System.out.println("Random " + U);
        double res = (-1/lam) * Math.log(U);
        System.out.println("INTERVAL " + res);
        return res;
*/
        //return U;
    }

    /*
    public static double interval(int i){
        return servTimes[i - 1];
    }

     */

    static ArrayList<Integer> clientsInQueue = new ArrayList<Integer>();
    static ArrayList<Integer> window1Status = new ArrayList<Integer>();
    static ArrayList<Integer> window2Status = new ArrayList<Integer>();
    static ArrayList<Integer> window3Status = new ArrayList<Integer>();

    static double clientsInQ[] = new double[99999];
    static double eventTimes[] = new double[99999];
    static int evCnt = 0;
    static int maxInQ = 0;

    static double w1Time = 0;
    static double w2Time = 0;
    static double w3Time = 0;

    static double lastCame = 0;
    static double sumLastCame = 0;

    public static void saveValues(){

        eventTimes[evCnt] = t;

        int index = 0;

        if((n - 3) > 0){
            index = n - 3;
            if(index > maxInQ)
                maxInQ = index;
        } else {
            index = 0;
        }
        if(evCnt != 0){
            clientsInQ[index] += t - eventTimes[evCnt - 1];

            if(i1 != 0 && t < T){
                w1Time += t - eventTimes[evCnt - 1];
            }

            if(i2 != 0 && t < T){
                w2Time += t - eventTimes[evCnt - 1];
            }

            if(i3 != 0 && t < T){
                w3Time += t - eventTimes[evCnt - 1];
            }

        }
        else {
            clientsInQ[0] += t;
        }


        evCnt++;


        if(i1 == 0)
            window1Status.add(0);
        else
            window1Status.add(1);

        if(i2 == 0)
            window2Status.add(0);
        else
            window2Status.add(1);

        if(i3 == 0)
            window3Status.add(0);
        else
            window3Status.add(1);


        if(n > 3){
            clientsInQueue.add(n);
        } else {
            clientsInQueue.add(0);
        }
    }



    public static void main(String[] args) throws IOException {

        int c1 = 0;
        int c2 = 0;
        int c3 = 0;

        double ta = INF;

        ta = genTime(t, lam);

        System.out.println();
        while (ta < T || n > 0) {

            //System.out.print(t + " ");

            if(ta >= T)
                ta = INF;


            if(ta < t1 && ta < t2 && ta < t3 && ta < T){
                t = ta;

                if(ta < T){
                    na++;
                    ta = genTime(t, lam);
                    times.add(t);
                    cameTimes[na] = t;
                }

                else
                    ta = INF;


                if(lastCame > 0){
                    sumLastCame += t - lastCame;
                }

                lastCame = t;



                if(n == 0 && i1 == 0 && i2 == 0 && i3 ==0){
                    setState(1, na, 0, 0);
                    t1 = t + interval(na);
                    System.out.println("Next client came at " + t + " at win " + 1 + " n is " + n);
                } else if(n == 1 && i1 == 0 && i2 == 0 && i3 != 0){
                    setState(2, na, 0, i3);
                    t1 = t + interval(na);
                    System.out.println("Next client came at " + t + " at win " + 1 + " n is " + n);
                }  else if(n == 1 && i1 == 0 && i2 != 0 && i3 == 0){
                    setState(2, na, i2, 0);
                    t1 = t + interval(na);
                    System.out.println("Next client came at " + t + " at win " + 1 + " n is " + n);
                } else if(n == 2 && i1 == 0 && i2 != 0 && i3 != 0){
                    setState(3, na, i2, i3);
                    t1 = t + interval(na);
                    System.out.println("Next client came at " + t + " at win " + 1 + " n is " + n);
                } else if(n == 1 && i1 != 0 && i2 == 0 && i3 == 0){
                    setState(2, i1, na, 0);
                    t2 = t + interval(na);
                    System.out.println("Next client came at " + t + " at win " + 2 + " n is " + n);
                } else if(n == 2 && i1 != 0 && i2 == 0 && i3 != 0){
                    setState(3, i1, na, i3);
                    t2 = t + interval(na);
                    System.out.println("Next client came at " + t + " at win " + 2 + " n is " + n);
                } else if(n == 2 && i1 != 0 && i2 != 0 && i3 == 0){
                    setState(3, i1, i2, na);
                    t3 = t + interval(na);
                    System.out.println("Next client came at " + t + " at win " + 3 + " n is " + n);
                } else if (n > 2){
                    setState(n + 1, i1, i2, i3);
                    System.out.println("Next client came at " + t + " and PLACED TO QUEUE n is " + n);
                }

                saveValues();



            }


            //уход1
            if(t1 < ta && t1 <= t2 && t1 <= t3){
                //timeChange.add(t);
                t = t1;
                saveValues();


                c1++;
                System.out.println("Leaving window1 at " + t);
                leaveTimes[i1] = t;

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
                    int m = max(i1, i2, i3) + 1;
                    setState(n - 1, m, i2, i3);
                    waitTimes[m] = t - cameTimes[m];
                    System.out.println("Client ID " + m + " wait time is " + waitTimes[m]);
                    t1 = t + interval(m);
                }
            }

            //уход2
            if(t2 < ta && t2 <= t1 && t2 <= t3){
                //timeChange.add(t);
                t = t2;
                saveValues();
                c2++;
                System.out.println("Leaving window2 at " + t);
                leaveTimes[i2] = t;
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
                    int m = max(i1, i2, i3) + 1;
                    setState(n - 1, i1, m, i3);
                    waitTimes[m] = t - cameTimes[m];
                    System.out.println("Client ID " + (m) + " wait time is " + waitTimes[m]);
                    t2 = t + interval(m);
                }
            }

            //уход3
            if(t3 < ta && t3 <= t2 && t3 <= t1){
                //timeChange.add(t);
                t = t3;
                saveValues();
                c3++;
                System.out.println("Leaving window3 at " + t);
                leaveTimes[i3] = t;
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
                    int m = max(i1, i2, i3) + 1;
                    setState(n - 1, i1, i2, m);
                    waitTimes[m] = t - cameTimes[m];
                    System.out.println("Client ID " + m + " wait time is " + waitTimes[m]);
                    t3 = t + interval(m);
                }
            }


        }

        printTimes();

//непустые в wait_time / (na-1) - первая статистика

        System.out.println(" ");
        double sum = 0;
        for(int i = 0; i < na - 1; i++){
            sum += waitTimes[i];
        }
        System.out.println(" ");


        System.out.println("SIZE " + clientsInQueue.size() + "\n");

        System.out.printf("Средняя задержка в очереди %.2f\n", sum / (na - 1));
        //System.out.println("Средняя задержка в очереди " + sum / (na - 1));

        double _Q = 0;

        for(int i = 0; i <= maxInQ; i++){
            _Q += clientsInQ[i] * i;
        }

        _Q /= T;

        //System.out.println("Оценка ожидаемого среднего числа клиентов " + _Q);
        System.out.printf("Среднее число клиентов в очереди %.2f\n", _Q);

        //System.out.println("Коэффициент занятости первого окна " + w1Time / T);
        System.out.printf("Коэффициент занятости первого окна %.3f\n", w1Time / T);
        //System.out.println("Коэффициент занятости второго окна " + w2Time/T);
        System.out.printf("Коэффициент занятости второго окна %.3f\n", w2Time/T);
        //System.out.println("Коэффициент занятости третьего окна " + w3Time/T);
        System.out.printf("Коэффициент занятости третьего окна %.3f\n", w3Time/T);
        //System.out.println();
        //System.out.println("Среднее время обслуживания " + servTime / na);
        System.out.printf("Среднее время обслуживания %.2f\n", servTime / na);
        //System.out.println("Среднее время между поступлениями " + sumLastCame / (na - 1));
        System.out.printf("Среднее время между поступлениями %.2f\n", sumLastCame / (na - 1));


        System.out.printf("№     Ai       Di      Vi      Wi\n");
        for(int i = 0; i <= na; i++){
            //System.out.println(i + " " + cameTimes[i] + " " + leaveTimes[i] + " " + sTimes[i] + " " + waitTimes[i] + " " + convertToTime(cameTimes[i]) + " " + convertToTime(leaveTimes[i]));
            System.out.printf("%d     %.2f    %.2f    %.2f    %.2f    %s    %s\n", i, cameTimes[i], leaveTimes[i], sTimes[i], waitTimes[i], convertToTime(cameTimes[i]), convertToTime(leaveTimes[i]));
        }

    }

    public static String convertToTime(double inputTime)
    {
        double timeline = inputTime;

        int hour = (int) timeline / 60 + 9;
        int min = (int) timeline - (hour - 9) * 60;
        double sec = timeline - (int) timeline;

        String result = hour + ":" + min + ":" + (int)(sec * 60);

        return result;
    }






}
