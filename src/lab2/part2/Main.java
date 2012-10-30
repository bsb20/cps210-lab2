package lab2.part2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import lab2.Building;
import lab2.part2.RiderThread;


public class Main
{
    private static Scanner sc;
    private static int F, E, R, T, M;


    public static synchronized int[] nextRider() {
        if (R == 0) {
            return null;
        }

        R--;
        int[] rider = new int[3];
        rider[0] = sc.nextInt();
        rider[1] = sc.nextInt() - 1;
        rider[2] = sc.nextInt() - 1;
        return rider;
    }

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        File log=new File("elevator.log");
        System.setOut(new PrintStream(new FileOutputStream(log)));

        sc = new Scanner(System.in);
        F = sc.nextInt();
        E = sc.nextInt();
        R = sc.nextInt();
        T = sc.nextInt();
        M = sc.nextInt();

        Building building = new Building(F, E, M);

        RiderThread[] riders = new RiderThread[T];
        for (int i = 0; i < T; i++) {
            riders[i] = new RiderThread(building, i);
            riders[i].start();
        }
        building.startElevators();
        for (int i = 0; i < T; i++) {
            riders[i].join();
        }
        building.stopElevators();
        System.out.println("All done");
    }
}
