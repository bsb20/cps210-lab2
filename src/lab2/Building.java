package lab2;

import java.util.ArrayList;
import java.util.List;

/*
 * Each rider requests a floor by calling await up/down on the Building
 * The building starts/stops elevators and helps coordinate the elevators
 * with the Riders
 */
public class Building
{
	
	private ElevatorController myController;

	public Building(int numFloors, int numElevators, int capacity) {
		myController=new ElevatorController(numFloors);
		for (int i = 0; i < numElevators; i++) {
			myController.add(new Elevator(myController, i, numFloors, capacity, this));
		}

	}
	
	/*
	 * Returns the 
	 */
    private Elevator findElevator(int floor, boolean upwards) {
        return myController.findElevator(floor, upwards);
    }
	
	/*
	 * A call to await finds the elevator that will arrive for the Rider, and 
	 * returns an instance of the elevator to the calling Rider. If the elevator
	 * is full or headed in the wrong direction, the Building's monitor uses a 
	 * wait() CV to block the calling thread until the doors close and the floor 
	 * request can be made again
	 */
    private Elevator await(int floor, boolean upwards) {
        Elevator elevator = findElevator(floor,upwards);
        System.out.println("E" + elevator.getElevatorId() + " called");
        synchronized (this) {
            while (elevator.isFull()) {
                try {
                    wait(); //if elevator full, block rider thread until doors close and new request can be made
                }
                catch (InterruptedException e) {}
            }
        }
        elevator.requestFloor(floor, upwards);
        while (elevator.goingUp() != upwards) {
            elevator.pass();
            synchronized (this) {
                while (elevator.isOpen()) {
                    try {
                        wait(); //if elevator in wrong direction, block rider thread until doors close and new request can be made
                    }
                    catch (InterruptedException e) {}
                }
            }
            elevator.requestFloor(floor, upwards);
        }
		return elevator; //elevator is in correct direction and has capacity for another rider
    }
	
	/*
	 * Returns a reference to the arriving elevator when a Rider requests a ride down
	 */ 
	public Elevator awaitUp(int floor) {
        return await(floor, true);
	}

	/*
	 * Returns a reference to the arriving elevator when a Rider requests a ride up
	 */ 
	public Elevator awaitDown(int floor) {
        return await(floor, false);
	}

	/*
	 * Starts all Elevator threads in the Building
	 */
	public void startElevators(){
		for(Elevator e: myController.getElevators()){
			e.start();
		}
	}

	/*
	 * Stops all Elevator threads in the Building
	 */
	public void stopElevators(){
		for(Elevator e: myController.getElevators()){
			e.interrupt();
		}
	}
}
