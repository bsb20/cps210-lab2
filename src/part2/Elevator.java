package part2;

import java.util.List;

import EventBarrier.EventBarrier;

public class Elevator extends Thread{
	private int myFloors;
	private int myCurrentFloor;
	private EventBarrier[] myFloorBarriers;
	private Building myBuilding;
	private List<Integer> myRequestedFloors;
	private boolean up=true;
	public Elevator(int floors, Building b){
		myBuilding=b;
		myFloors=floors;
		myFloorBarriers=new EventBarrier[myFloors];
		for(int i=0; i<myFloors; i++){
			myFloorBarriers[i]=new EventBarrier();
		}
	}
	
	@Override
	public void run(){
		
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
	
	
	
	
	
}
