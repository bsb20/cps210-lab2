package lab2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public synchronized void removeDestination(int floor, boolean goingUp) {
        (goingUp ? myUpStops : myDownStops)[floor]=null;
    }
	public synchronized Elevator findElevator(int floor, boolean goingUp){
		if(goingUp && myUpStops[floor]!=null){
			return myUpStops[floor];
		}
		if(!goingUp && myDownStops[floor]!=null)
			return myDownStops[floor];
        int randomIndex = (new Random()).nextInt(myElevators.size());
		Elevator bestElevator=myElevators.get(randomIndex);
		int bestDistance=Integer.MAX_VALUE;
        for(Elevator e: myElevators){
            int dist=elevatorDistanceFunction(e,floor,goingUp);
            if(dist > 0 && dist<bestDistance){
                bestDistance=dist;
                bestElevator=e;
            }
        }
        //        bestElevator = myElevators.get((new Random()).nextInt(myElevators.size()));
        addDestination(floor, goingUp, bestElevator);
        return bestElevator;
	}

	private int elevatorDistanceFunction(Elevator e, int floor, boolean goingUp){
		if(e.isFull()){
			return Integer.MAX_VALUE;
		}

        int penalty = e.requests() * e.getFloors() / 2;
        int mask = 0;
        mask = (goingUp ? 1 : 0) |
            (e.goingUp() ? 2 : 0) |
            (e.getFloor() < floor ? 4 : 0);
        switch (mask) {
        case 0:
            return penalty + e.getFloor() - floor;
        case 1:
            return penalty + e.getFloor() + floor;
        case 2:
            return penalty + 2 * e.getFloors() - floor - e.getFloor();
        case 3:
            return penalty + 2 * e.getFloors() + floor - e.getFloor();
        case 4:
            return penalty + 2 * e.getFloors() + floor + e.getFloor();
        case 5:
            return penalty + e.getFloor() + floor;
        case 6:
            return penalty + 2 * e.getFloors() - floor - e.getFloor();
        case 7:
            return penalty + floor - e.getFloor();
        }
		return Integer.MAX_VALUE;
	}
}
