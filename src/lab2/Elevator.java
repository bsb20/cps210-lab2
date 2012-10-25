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
        goingUp = floor > myCurrentFloor;
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
		myFloors[floor].hold();
        this.notify();
	}

    private int nextFloor() {
        if (goingUp) {
            int next = nextHigherFloor();
            return next == -1 ? nextLowerFloor() : next;
        }
        else {
            int next = nextLowerFloor();
            return next == -1 ? nextHigherFloor() : next;
        }
    }

    private int nextHigherFloor() {
        for (int i = myCurrentFloor + 1; i < myFloors.length; i++) {
            if (myFloors[i].waiters() > 0) {
                return i;
            }
        }
        return -1;
    }

    private int nextLowerFloor() {
        for (int i = myCurrentFloor - 1; i >= 0; i--) {
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
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    return;
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
