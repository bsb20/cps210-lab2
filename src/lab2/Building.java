import java.util.ArrayList;
import java.util.List;


public class Building{
	private List<Elevator> myElevators;
	public Building(int numFloors, int numElevators) {
		myElevators=new ArrayList<Elevator>();
		for(int i=0; i<numElevators; i++){
			myElevators.add(new Elevator(numFloors, i, 100));
		}
		
	}

	
	public Elevator AwaitUp(int fromFloor) {
		myElevators.get(0).RequestFloor(fromFloor);
		if(myElevators.get(0).goingUp()){
			return myElevators.get(0);
		}
		while(!myElevators.get(0).goingUp()){
			myElevators.get(0).RequestFloor(fromFloor);
		}
		return myElevators.get(0);
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
	public Elevator AwaitDown(int fromFloor) {
		myElevators.get(0).RequestFloor(fromFloor);
		if(!myElevators.get(0).goingUp()){
			System.out.println("yelp2");
			return myElevators.get(0);
		}
		while(myElevators.get(0).goingUp()){
			myElevators.get(0).RequestFloor(fromFloor);
		}
		return myElevators.get(0);
	}

}
