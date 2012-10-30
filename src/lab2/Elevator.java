package lab2;

import java.util.TreeSet;

/*
 * The elevator class has several key components. 
 *
 * The elevator runs as a thread that simply looks for a next floor that has a waiting rider,
 * visits the appropriate floor and lets riders off/on. If there are no requests to be handled,
 * the elevator blocks, awakened when a new Rider request arrives
 *
 * When a request is made by a Rider, the ElevatorController contains the logic to help the building
 * direct the appropriate elevator to the requesting Rider. Each Elevator contains an EventBarrier
 * for each floor. When a elevator is assigned to a rider, the rider waits on the EventBarrier on 
 * that elevator's floor. A Rider who enters the elevator and requests a floor does the same. When 
 * the elevator arrives at a floor, the EventBarrier on that floor awakens the waiting threads who 
 * then enter/exit the elevator, calling complete() in the process. After each has tried to exit or 
 * enter, the EventBarrier signal switches off and the doors close.
 */
public class Elevator extends Thread
{
    private int myId;
    private int myRiders = 0;
    private int myCapacity;
	private EventBarrier[] myFloors;
	private int myCurrentFloor;
	private boolean goingUp = true;
    private Building myBuilding;
    private boolean myDoorsOpen = false;
    private TreeSet<Integer> myUpRequests, myDownRequests;
    private ElevatorController myController;


	public Elevator(ElevatorController controller, int id, int floors, int capacity, Building building) {
		myController=controller;
		myBuilding = building;
        myId = id;
		myCurrentFloor = -1;
        myCapacity = capacity;
		myFloors = new EventBarrier[floors];
        myUpRequests = new TreeSet<Integer>();
        myDownRequests = new TreeSet<Integer>();
		for (int i=0; i < floors; i++) {
			myFloors[i] = new EventBarrier();
		}
	}

	/*
	 * Returns true if the elevator is going up
	 */
    public boolean goingUp() {
        return goingUp;
    }

	/*
	 * Returns true if the elevator doors are open
	 */
    public boolean isOpen() {
        return myDoorsOpen;
    }

	
	/*
	 * Returns the elevator's assigned ID (1 - E)
	 */
    public int getElevatorId() {
        return myId;
    }

	/*
	 * Returns true if the elevator is at capacity
	 */
    public boolean isFull() {
        return myRiders == myCapacity;
    }
	
	/*
	 * Returns the floor that the elevator is currently located
	 */
    public int getFloor(){
    	return myCurrentFloor;
    }
	
	/*
	 * Returns the number of floors in the building
	 */
    public int getFloors(){
    	return myFloors.length;
    }
	
	/*
	 * Opens elevator doors, and awakens the riders waiting to enter/exit 
	 */
	public void openDoors() {
        myDoorsOpen = true;
		System.out.println(String.format("E%d on F%d opens", myId,
                                         myCurrentFloor));
		System.out.println(String.format("E%d on F%d has %d waiters", myId,
                                         myCurrentFloor, myFloors[myCurrentFloor].waiters()));
        try {
            sleep(50);
        }
        catch (InterruptedException e) {}
		myFloors[myCurrentFloor].signal();
	}
	
	/*
	 * Returns the total number of Rider threads waiting on the elevator to arrive
	 */
    public int requests() {
        return myUpRequests.size() + myDownRequests.size();
    }

	/*
	 * Close doors, and notify all the Rider threads that could not enter (wrong 
	 * direction, elevator full) so that they can re-request their floor
	 */
	public void closeDoors() {
        myDoorsOpen = false;
        synchronized (myBuilding) {
            myBuilding.notifyAll();
        }
		System.out.println(String.format("E%d on F%d closes", myId,
                                         myCurrentFloor));
	}
	
	/*
	 * Move elevator to floor to visit, and update elevator state accordingly
	 */
	public synchronized void visitFloor(int floor) {
        if (goingUp)
            myUpRequests.remove(floor);
        else
            myDownRequests.remove(floor);
        myController.removeDestination(floor, goingUp);
		myCurrentFloor = floor;
		System.out.println(String.format("E%d moves %s to F%d", myId,
                                         goingUp ? "up" : "down",
                                         myCurrentFloor));
	}

	
	public synchronized boolean enter(int threadId, int riderId) {
        if (isFull()) {
            myFloors[myCurrentFloor].complete();
            return false;
        }
        else {
            myRiders++;
            System.out.println(String.format("T%d: R%d enters E%d on F%d",
                                             threadId, riderId, myId,
                                             myCurrentFloor));
            myFloors[myCurrentFloor].complete();
            return true;
        }
	}

	/*
	 * If a Rider Thread cannot successfully enter the elevator, it must call 
	 * complete so that the doors can close 
	 */
	public void pass() {
		myFloors[myCurrentFloor].complete();
	}
	
	/*
	 * On exit, update the number of riders in the elevator and have the calling Rider call complete
	 * as it leaves the elevator
	 */
	public synchronized void exit() {
        myRiders--;
		myFloors[myCurrentFloor].complete();
	}
	
	/*
	 * For a Rider in the elevator, request the floor it wants to go, and block 
	 * until the elevator arrives at the floor and its doors open
	 */
    public void requestFloor(int floor) {
        requestFloor(floor, floor > myCurrentFloor);
    }

	public void requestFloor(int floor, boolean upwards) {
        synchronized (this) {
            if (upwards)
                myUpRequests.add(floor);
            else
                myDownRequests.add(floor);
            notifyAll();
            myController.addDestination(floor, upwards, this);
        }
        myFloors[floor].hold();
	}
	
	/*
	 * If the elevator is going up, returns the next highest floor as the one to visit
	 * If the elevator is going down, or there are no requests higher to fufill, retunrs
	 * the next lowest floor as the one to visit next.
	 */
    private synchronized int nextFloor() {
        if (goingUp) {
            Integer next = myUpRequests.higher(myCurrentFloor);
            if (next != null)
                return next;
            goingUp = false;
            myCurrentFloor = myFloors.length;
            next = myDownRequests.lower(myCurrentFloor);
            return next != null ? next : -1;
        }
        else {
            Integer next = myDownRequests.lower(myCurrentFloor);
            if (next != null)
                return next;
            goingUp = true;
            myCurrentFloor = -1;
            next = myUpRequests.higher(myCurrentFloor);
            return next != null ? next : -1;
        }
    }
	
	/*
	 * Elevator loops, identifying the next floor to pick up Riders and 
	 * visiting the requested floors. The Elevator blocks if it has no 
	 * requests to fufill
	 */
	@Override
	public void run() {
        while (true) {
            int next = nextFloor();
            if (myUpRequests.isEmpty() && myDownRequests.isEmpty()) {
                synchronized (this) {
                    myCurrentFloor = -1;
                    try {
                        wait();
                    }
                    catch (InterruptedException e) {
                        return;
                    }
                }
            }
            else if (next != -1) {
                visitFloor(next);
                openDoors();
                closeDoors();
            }

            if (interrupted()) {
                return;
            }
        }
	}
}
