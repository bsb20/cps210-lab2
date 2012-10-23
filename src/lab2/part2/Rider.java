package lab2.part2;

import lab2.Building;
import lab2.Elevator;


public class Rider extends Thread
{
    private Building myBuilding;
    private int myStartFloor, myEndFloor;


    public Rider(Building building, int startFloor, int endFloor) {
        myBuilding = building;
        myStartFloor = startFloor;
        myEndFloor = endFloor;
    }

    @Override
    public void run() {
        System.out.println("Calling: " + myStartFloor);
        Elevator elevator = myStartFloor < myEndFloor ?
            myBuilding.awaitUp(myStartFloor) :
            myBuilding.awaitDown(myStartFloor);
        System.out.println("Entering");
        elevator.enter();
        elevator.requestFloor(myEndFloor);
        elevator.exit();
    }

}
