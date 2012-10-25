



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
            myBuilding.AwaitUp(myStartFloor) :
            myBuilding.AwaitDown(myStartFloor);
        System.out.println("Entering");
        elevator.Enter();
        elevator.RequestFloor(myEndFloor);
        elevator.Exit();
    }

}
