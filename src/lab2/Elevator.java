package lab2;

import java.util.TreeSet;


public class Elevator extends Thread
{
    private int myId;
	private EventBarrier[] myFloors;
	private int myCurrentFloor;
	private boolean goingUp = true;
    private Building myBuilding;
    private TreeSet<Integer> myUpRequests, myDownRequests;


	public Elevator(int id, int floors, Building building) {
        myBuilding = building;
        myId = id;
		myCurrentFloor = -1;
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

    public int getElevatorId() {
        return myId;
    }

	public void openDoors() {
		System.out.println(String.format("E%d on F%d opens", myId,
                                         myCurrentFloor));
		myFloors[myCurrentFloor].signal();
	}

	public void closeDoors() {
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
		myCurrentFloor = floor;
		System.out.println(String.format("E%d moves %s to F%d", myId,
                                         goingUp ? "up" : "down",
                                         myCurrentFloor));
	}

	public void enter() {
		myFloors[myCurrentFloor].complete();
	}

	public void pass() {
		myFloors[myCurrentFloor].complete();
	}

	public void exit() {
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
            System.out.println("elevator notified");
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
                    	System.out.println("elevator waiting for reqs");
                        wait();
                    }
                    catch (InterruptedException e) {
                        return;
                    }
                }
            }
            else {
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
