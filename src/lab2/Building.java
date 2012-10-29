package lab2;

import java.util.ArrayList;
import java.util.List;


public class Building
{
	private List<Elevator> myElevators;


	public Building(int numFloors, int numElevators) {
		myElevators = new ArrayList<Elevator>();
		for (int i = 0; i < numElevators; i++) {
			myElevators.add(new Elevator(i, numFloors, this));
		}
	}

    private Elevator findElevator(int floor) {
        return myElevators.get(0);
    }

	public Elevator awaitUp(int floor) {
        Elevator elevator = findElevator(floor);
        elevator.requestFloor(floor, true);
        while (!elevator.goingUp()) {
            elevator.pass();
            synchronized (this) {
                try {
                    wait();
                }
                catch (InterruptedException e) {}
            }
            elevator.requestFloor(floor);
        }
		return elevator;
	}

	public Elevator awaitDown(int floor) {
        Elevator elevator = findElevator(floor);
        elevator.requestFloor(floor, false);
        while (elevator.goingUp()) {
            elevator.pass();
            synchronized (this) {
                try {
                    wait();
                }
                catch (InterruptedException e) {}
            }
            elevator.requestFloor(floor);
        }
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
