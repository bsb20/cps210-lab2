package lab2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * While the building is called by the thread when it awaits an elevator and the building returns a reference
 * of the elevator to the thread, it is the ElevatorController that maintains a list of all the elevators 
 * and what floors are currently registered to be visited by an elevator. It holds a list of elevators, and an 
 * array of floors that contains a reference to each elevator visiting each floor. It also contains the logic 
 * to select a elevator for a Rider when no elevator is scheduled to visit the floor it is waiting on. 
 */
public class ElevatorController {

	private List<Elevator> myElevators;
	private Elevator[] myUpStops, myDownStops;

	public ElevatorController(int floors){
		myElevators=new ArrayList<Elevator>();
		myUpStops= new Elevator[floors];
		myDownStops= new Elevator[floors];
	}
	
	/*
	 * Add elevator to controller
	 */ 
	public synchronized void add(Elevator e){
		myElevators.add(e);
	}
	
	/*
	 * Returns a list of the elevators being controlled
	 */
	public synchronized List<Elevator> getElevators(){
		return myElevators;
	}
	
	/*
	 * For a Rider with a requested floor and its according Elevator, register the elevator with the floor requested
	 */
	public synchronized void addDestination(int floor, boolean goingUp, Elevator e){
		(goingUp ? myUpStops : myDownStops)[floor]=e;
	}
	
	/*
	 * Update the controller that a floor is no longer registered to be visited 
	 */
    public synchronized void removeDestination(int floor, boolean goingUp) {
        (goingUp ? myUpStops : myDownStops)[floor]=null;
    }
	
	/*
	 * Returns a reference to the Elevator that will visit the requesting Rider thread's floor.
	 * If a elevator is already visiting the floor requested and is heading the in the correct direction,
	 * then return that elevator. If no elevator is headed to the floor, use our algorithm to select the floor
	 * (find the closest elevator that is headed in the correct direction).
	 */
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
	
	/*
	 * Logic for selecting the optimal elevator to visit a floor with a Rider waiting to enter
	 */
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
