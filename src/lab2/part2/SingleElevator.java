import java.util.Random;


public class SingleElevator
{
    public static void main(String[] args) throws InterruptedException {
        System.out.println("good");
    	int n = 1;
        Building building = new Building(10,1);
        Random rand = new Random();
        Rider[] riders = new Rider[n];

        for (int i = 0; i < n; i++) {
            riders[i] = new Rider(building, rand.nextInt(10),
                                  rand.nextInt(10));
            riders[i].start();
        }
        building.startElevators();
        for (int i = 0; i < n; i++) {
            riders[i].join();
        }
        building.stopElevators();
        System.out.println("All done");
    }
}
