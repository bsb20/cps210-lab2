package lab2.part2;

import lab2.Building;
import lab2.Elevator;


public class RiderThread extends Thread
{
    private int myId;
    private Building myBuilding;


    public RiderThread(Building building, int id) {
        myBuilding = building;
        myId = id;
    }

    @Override
    public void run() {
        int[] rider = Main.nextRider();
        while (rider != null) {
            int riderId = rider[0];
            int startFloor = rider[1];
            int endFloor = rider[2];

            Elevator elevator;
            if (startFloor < endFloor) {
                System.out.println(String.format("T%d: R%d pushes U%d",
                                                 myId, riderId, startFloor));
                elevator = myBuilding.awaitUp(startFloor);
            }
            else {
                System.out.println(String.format("T%d: R%d pushes D%d",
                                                 myId, riderId, startFloor));
                elevator = myBuilding.awaitDown(startFloor);
            }
            elevator.enter();
            elevator.requestFloor(endFloor);
            elevator.exit();

            rider = Main.nextRider();
        }
    }

}
