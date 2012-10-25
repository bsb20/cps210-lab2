package lab2.part2;

import java.util.Random;

import lab2.Building;
import lab2.part2.Rider;


public class SingleElevator
{
    private static int N = 1;
    private static int F = 10;


    public static void main(String[] args) throws InterruptedException {
        Building building = new Building(F, N);
        Random rand = new Random();
        Rider[] riders = new Rider[N];

        for (int i = 0; i < N; i++) {
            riders[i] = new Rider(building, i, rand.nextInt(F), rand.nextInt(F));
            riders[i].start();
        }
        building.startElevators();
        for (int i = 0; i < N; i++) {
            riders[i].join();
        }
        building.stopElevators();
        System.out.println("All done");
    }
}
