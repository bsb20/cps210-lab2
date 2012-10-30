package lab2.part2;
import lab2.Building;
import lab2.Elevator;

/*
 * Thread that executes requests from the elevator Riders
 * Each loop of thread processes a new Rider request
 */
public class RiderThread extends Thread
{
    private int myId;
    private Building myBuilding;


    public RiderThread(Building building, int id) {
        myBuilding = building;
        myId = id;
    }
	
	/*
	 * Thread loops, each time receiving the riderID, start/end floors of the Rider
	 * Rider awaits the elevator arrival, attempts to enter, and if successful, requests
	 * its floor, and exits the elevator on the appropriate floor
	 *
	 * If the rider encounters a full elevator, it waits again for a elevator to arrive 
	 */
    @Override
    public void run() {
        int[] rider = Main.nextRider();
        while (rider != null) {
            int riderId = rider[0];
            int startFloor = rider[1];
            int endFloor = rider[2];

            Elevator elevator;
            while (true) {    //Call to awaitup/down blocks the thread on an EventBarrier until the elevator opens on its floor
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

                if (elevator.enter(myId, riderId))
                    break;
            }

            System.out.println(String.format("T%d: R%d pushes E%dB%d",
                                             myId, riderId,
                                             elevator.getElevatorId(),
                                             endFloor));
            elevator.requestFloor(endFloor);  //floor request blocks thread until the elevator doors open and the Rider exits

            System.out.println(String.format("T%d: R%d exits E%d on F%d",
                                             myId, riderId,
                                             elevator.getElevatorId(),
                                             endFloor));
            elevator.exit();      

            rider = Main.nextRider();
        }
    }

}
