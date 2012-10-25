import java.util.List;


public class Elevator extends Thread{
	private int myFloors;
	private int myID;
	private EventBarrier[] myFloorBarriers;
	private int myCurrentFloor;
	private boolean up=true;
	private boolean doorsOpen=true;
	public Elevator(int numFloors, int elevatorId, int maxOccupancyThreshold) {
		myFloors=numFloors;
		myCurrentFloor=0;
		myID=elevatorId;
		myFloorBarriers= new EventBarrier[numFloors];
		for(int i=0;i<myFloors; i++){
			myFloorBarriers[i]=new EventBarrier();
		}
	}

	
	public void OpenDoors() {
		myFloorBarriers[myCurrentFloor].signal();
		
	}

	
	public void CloseDoors() {
		// TODO Auto-generated method stub
		
	}

	
	public synchronized void VisitFloor(int floor) {
		System.out.println("visiting "+floor);
		myCurrentFloor=floor;
		System.out.println("floor has "+myFloorBarriers[myCurrentFloor].waiters()+" waiters");
		OpenDoors();
	}

	public boolean goingUp(){
		return up;
	}
	public void Enter() {
		System.out.println("ENTERED");
		myFloorBarriers[myCurrentFloor].complete();
	}

	
	public void Exit() {
		System.out.println("EXITED");
		myFloorBarriers[myCurrentFloor].complete();
	}

	
	public void RequestFloor(int floor) {
		myFloorBarriers[floor].hold();
		System.out.println("completed");
		
	}
	public synchronized void moveFloor(int i){
		myCurrentFloor+=i;
	}
	@Override
	public void run(){
		VisitFloor(myCurrentFloor);
		while(true){
			if(up){
				if(myCurrentFloor<myFloors-1){
					if(myFloorBarriers[myCurrentFloor+1].waiters()>0)
						VisitFloor(myCurrentFloor+1);
					else{
						moveFloor(1);
					}
				}
				else{
					up=false;
				}
			}
			if(!up){
				if(myCurrentFloor>0){
					if(myFloorBarriers[myCurrentFloor-1].waiters()>0)
						VisitFloor(myCurrentFloor-1);
					else{
						moveFloor(-1);
					}
				}
			}
		if(interrupted()){
			return;
		}
		}
	}
}
