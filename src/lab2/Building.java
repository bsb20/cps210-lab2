package lab2;

import java.util.ArrayList;
import java.util.List;


public class Building
{
	
	private ElevatorController myController;

	public Building(int numFloors, int numElevators, int capacity) {
		myController=new ElevatorController(numFloors);
		for (int i = 0; i < numElevators; i++) {
			myController.add(new Elevator(myController, i, numFloors, capacity, this));
		}

	}

    private Elevator findElevator(int floor, boolean upwards) {
        return myController.findElevator(floor, upwards);
    }

    private Elevator await(int floor, boolean upwards) {
        Elevator elevator = findElevator(floor,upwards);
        System.out.println("E" + elevator.getElevatorId() + " called");
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
		for(Elevator e: myController.getElevators()){
			e.start();
		}
	}

	public void stopElevators(){
		for(Elevator e: myController.getElevators()){
			e.interrupt();
		}
	}
}
