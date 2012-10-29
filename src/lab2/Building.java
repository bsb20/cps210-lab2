package lab2;

import java.util.ArrayList;
import java.util.List;


public class Building
{
	private List<Elevator> myElevators;


	public Building(int numFloors, int numElevators, int capacity) {
		myElevators = new ArrayList<Elevator>();
		for (int i = 0; i < numElevators; i++) {
			myElevators.add(new Elevator(i, numFloors, capacity, this));
		}
	}

    private Elevator findElevator(int floor) {
        return myElevators.get(0);
    }

    private Elevator await(int floor, boolean upwards) {
        Elevator elevator = findElevator(floor);
        synchronized (this) {
            while (elevator.isFull()) {
                try {
                    wait();
                }
                catch (InterruptedException e) {}
            }
        }
        elevator.requestFloor(floor, upwards);
        while (elevator.goingUp() != upwards) {
            elevator.pass();
            synchronized (this) {
                while (elevator.isOpen()) {
                    try {
                        wait();
                    }
                    catch (InterruptedException e) {}
                }
            }
            elevator.requestFloor(floor, upwards);
        }
		return elevator;
    }

	public Elevator awaitUp(int floor) {
        return await(floor, true);
	}

	public Elevator awaitDown(int floor) {
        return await(floor, false);
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
