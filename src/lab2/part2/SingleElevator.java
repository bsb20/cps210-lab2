package lab2.part2;

import java.util.Random;

import lab2.Building;
import lab2.part2.Rider;


public class SingleElevator
{
    public static void main(String[] args) throws InterruptedException {
        int n = 20;
        Building building = new Building(1);
        Random rand = new Random();
        Rider[] riders = new Rider[n];

        for (int i = 0; i < n; i++) {
            riders[i] = new Rider(building, rand.nextInt(Building.F),
                                  rand.nextInt(Building.F));
            riders[i].start();
        }
        for (int i = 0; i < n; i++) {
            riders[i].join();
        }
    }
}