package lab2;

import java.util.List;

import lab2.EventBarrier;


public class Elevator extends Thread{
	private int myFloors;
	private int myCurrentFloor;
	private EventBarrier[] myFloorBarriers;
	private Building myBuilding;
	private boolean up=true;

	public Elevator(int floors, Building b){
		myBuilding=b;
		myFloors=floors;
		myFloorBarriers=new EventBarrier[myFloors];
		for(int i=0; i<myFloors; i++){
			myFloorBarriers[i]=new EventBarrier();
		}
	}

    private synchronized void visitFloor(int floor){
		myCurrentFloor=floor;
		try{
		if(up){
			myBuilding.getUpRiders(floor).signal();
		}
		else{
			myBuilding.getDownRiders(floor).signal();
		}
		myFloorBarriers[floor].signal();}
		catch(InterruptedException e){
			System.err.println("An interruption occurred");
		}
	}
	public boolean isGoingUp(){
		return up;
	}
	public void enter(int floor) throws InterruptedException{
		if(up){
			myBuilding.getUpRiders(floor).complete();
		}
		else{
			myBuilding.getDownRiders(floor).complete();
		}
	}

	public void exit(int floor) throws InterruptedException{
		myFloorBarriers[floor].complete();
	}

    private int nextFloor() {
        if (up) {
            int next = nextHigherFloor();
            if (next != -1)
                return next;
            up = false;
            return nextLowerFloor();
        }
        else {
            int next = nextLowerFloor();
            if (next != -1)
                return next;
            up = true;
            return nextHigherFloor();
        }
    }

    private int nextHigherFloor() {
        for (int i = myCurrentFloor + 1; i < myBuilding.getFloors(); i++) {
            if (myFloorBarriers[i].waiters() > 0 ||
                myBuilding.getUpRiders(i).waiters() > 0 ||
                myBuilding.getDownRiders(i).waiters() > 0) {
                return i;
            }
        }
        return -1;
    }

    private int nextLowerFloor() {
        for (int i = myCurrentFloor - 1; i >= 0; i--) {
            if (myFloorBarriers[i].waiters() > 0 ||
                myBuilding.getUpRiders(i).waiters() > 0 ||
                myBuilding.getDownRiders(i).waiters() > 0) {
                return i;
            }
        }
        return -1;
    }

	@Override
	public void run() {
        while (true) {
            visitFloor(nextFloor());
        }
	}
}
