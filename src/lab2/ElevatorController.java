package lab2;

import java.util.ArrayList;
import java.util.List;

public class ElevatorController {

	private List<Elevator> myElevators;
	private Elevator[] myUpStops, myDownStops;
	
	public ElevatorController(int floors){
		myElevators=new ArrayList<Elevator>();
		myUpStops= new Elevator[floors];
		myDownStops= new Elevator[floors];
	}
	public synchronized void add(Elevator e){
		myElevators.add(e);
	}
	public synchronized List<Elevator> getElevators(){
		return myElevators;
	}
	public synchronized void addDestination(int floor, boolean goingUp, Elevator e){
		(goingUp ? myUpStops : myDownStops)[floor]=e;
	}
	public synchronized Elevator findElevator(int floor, boolean goingUp){
		if(goingUp && myUpStops[floor]!=null){
			return myUpStops[floor];
		}
		if(!goingUp && myDownStops[floor]!=null)
			return myDownStops[floor];
		Elevator bestElevator=myElevators.get(0);
		int bestDistance=Integer.MAX_VALUE;
	for(Elevator e: myElevators){
		int dist=elevatorDistanceFunction(e,floor,goingUp);
		if(dist<bestDistance){
			bestDistance=dist;
			bestElevator=e;
		}
	}
	addDestination(floor, goingUp, bestElevator);
	return bestElevator;
	}
	
	private int elevatorDistanceFunction(Elevator e, int floor, boolean goingUp){
		if(e.isFull()){
			return Integer.MAX_VALUE;
		}
		if(goingUp==e.goingUp() && goingUp && (e.getFloor()<floor)){
			return floor-e.getFloor();
		}
		if(goingUp==e.goingUp() && !goingUp && (e.getFloor()>floor)){
			return e.getFloor()-floor;
		}
		if(goingUp!=e.goingUp() && !goingUp && (e.getFloor()<floor)){
			return 2*e.getFloor()+(e.getFloor()-floor);
		}
		if(goingUp!=e.goingUp() && goingUp && (e.getFloor()>floor)){
			return 2*e.getFloors()+(e.getFloor()-floor);
		}
		return Integer.MAX_VALUE;
	}
}
