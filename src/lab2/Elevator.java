package lab2;

import java.util.TreeSet;


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

    public boolean goingUp() {
        return goingUp;
    }

    public boolean isOpen() {
        return myDoorsOpen;
    }

    public int getElevatorId() {
        return myId;
    }

    public boolean isFull() {
        return myRiders == myCapacity;
    }
    public int getFloor(){
    	return myCurrentFloor;
    }
    public int getFloors(){
    	return myFloors.length;
    }
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

    public int requests() {
        return myUpRequests.size() + myDownRequests.size();
    }

	public void closeDoors() {
        myDoorsOpen = false;
        synchronized (myBuilding) {
            myBuilding.notifyAll();
        }
		System.out.println(String.format("E%d on F%d closes", myId,
                                         myCurrentFloor));
	}

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

	public void pass() {
		myFloors[myCurrentFloor].complete();
	}

	public synchronized void exit() {
        myRiders--;
		myFloors[myCurrentFloor].complete();
	}

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
