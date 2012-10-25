package lab2.part2;

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
        rider[1] = sc.nextInt();
        rider[2] = sc.nextInt();
        return rider;
    }

    public static void main(String[] args) throws InterruptedException {
        sc = new Scanner(System.in);
        F = sc.nextInt();
        E = sc.nextInt();
        R = sc.nextInt();
        T = sc.nextInt();
        M = sc.nextInt();

        Building building = new Building(F, E);

        System.out.println(F + " " + E + " " + R + " " + T + " " + M);

        RiderThread[] riders = new RiderThread[T];
        for (int i = 0; i < T; i++) {
            riders[i] = new RiderThread(building);
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
