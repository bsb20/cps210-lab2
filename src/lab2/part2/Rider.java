package lab2.part2;

import lab2.Building;
import lab2.Elevator;


public class Rider extends Thread
{
    private Building myBuilding;
    private int myStartFloor, myEndFloor;


    public Rider(Building building, int startFloor, int endFloor) {
        myStartFloor = startFloor;
        myEndFloor = endFloor;
    }

    @Override
    public void run() {
        Elevator elevator = myStartFloor < myEndFloor ?
            myBuilding.awaitUp(myStartFloor) :
            myBuilding.awaitDown(myStartFloor);
        elevator.enter();
        elevator.requestFloor(myEndFloor);
        elevator.exit();
    }

}
