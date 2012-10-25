package lab2;

import java.util.ArrayList;
import java.util.List;


public class Building
{
	private List<Elevator> myElevators;


	public Building(int numFloors, int numElevators) {
		myElevators = new ArrayList<Elevator>();
		for (int i = 0; i < numElevators; i++) {
			myElevators.add(new Elevator(i, numFloors));
		}
	}

    private Elevator findElevator(int floor) {
        return myElevators.get(0);
    }

	public Elevator awaitUp(int floor) {
        Elevator elevator = findElevator(floor);
        do {
            elevator.requestFloor(floor);
		} while (!elevator.goingUp());
		return elevator;
	}

	public Elevator awaitDown(int floor) {
        Elevator elevator = findElevator(floor);
        do {
            elevator.requestFloor(floor);
		} while (elevator.goingUp());
		return elevator;
	}

	public void startElevators(){
		for(Elevator e: myElevators){
			e.start();
		}
	}

	public void stopElevators(){
		for(Elevator e: myElevators){
			e.interrupt();
		}
	}
}
