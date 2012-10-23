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
        Elevator elevator = startFloor < endFloor ?
            myBuilding.awaitUp(myStartFloor) :
            myBuilding.awaitDown(myStartFloor);
        elevator.enter();
        elevator.requestFloor(myEndFloor);
        elevator.exit();
    }

}
