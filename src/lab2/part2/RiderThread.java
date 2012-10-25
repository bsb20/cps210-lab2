package lab2.part2;

import lab2.Building;
import lab2.Elevator;


public class RiderThread extends Thread
{
    private Building myBuilding;


    public RiderThread(Building building) {
        myBuilding = building;
    }

    @Override
    public void run() {
        int[] rider = Main.nextRider();
        while (rider != null) {
            int riderId = rider[0];
            int startFloor = rider[1];
            int endFloor = rider[2];

            Elevator elevator = startFloor < endFloor ?
                myBuilding.awaitUp(startFloor) :
                myBuilding.awaitDown(startFloor);
            elevator.enter();
            elevator.requestFloor(endFloor);
            elevator.exit();

            rider = Main.nextRider();
        }
    }

}
