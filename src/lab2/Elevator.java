package lab2;

import java.util.List;


public class Elevator extends Thread
{
    private int myId;
	private EventBarrier[] myFloors;
	private int myCurrentFloor;
	private boolean goingUp = true;


	public Elevator(int id, int floors) {
        myId = id;
		myCurrentFloor = 0;
		myFloors = new EventBarrier[floors];
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
		System.out.println(String.format("E%d on F%d closes", myId,
                                         myCurrentFloor));
	}

	public void visitFloor(int floor) {
        goingUp = (floor > myCurrentFloor) ||
            (floor == myCurrentFloor && goingUp);
		myCurrentFloor = floor;
		System.out.println(String.format("E%d moves %s to F%d", myId,
                                         goingUp ? "up" : "down",
                                         myCurrentFloor));
	}

	public void enter() {
		myFloors[myCurrentFloor].complete();
	}

	public void exit() {
		myFloors[myCurrentFloor].complete();
	}

	public void requestFloor(int floor) {
        synchronized (this) {
            notifyAll();
        }
		myFloors[floor].hold();
	}

    private int nextFloor() {
        if (goingUp) {
            int next = nextHigherFloor(false);
            if (next != -1)
                return next;
            goingUp = false;
            return nextLowerFloor(true);
        }
        else {
            int next = nextLowerFloor(false);
            if (next != -1)
                return next;
            goingUp = true;
            return nextHigherFloor(true);
        }
    }

    private int nextHigherFloor(boolean inclusive) {
        int start = myCurrentFloor + (inclusive ? 0 : 1);
        for (int i = start; i < myFloors.length; i++) {
            if (myFloors[i].waiters() > 0) {
                return i;
            }
        }
        return -1;
    }

    private int nextLowerFloor(boolean inclusive) {
        int start = myCurrentFloor - (inclusive ? 0 : 1);
        for (int i = start; i >= 0; i--) {
            if (myFloors[i].waiters() > 0) {
                return i;
            }
        }
        return -1;
    }

	@Override
	public void run() {
        while (true) {
            int next = nextFloor();
            if (next == -1) {
                synchronized (this) {
                    try {
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
