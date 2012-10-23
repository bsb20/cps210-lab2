package EventBarrier;

public class WaiterThread extends Thread{
	private EventBarrier myEB;
	
	
	public WaiterThread(EventBarrier eb){
		myEB = eb;
	}
	
	public void run() {
		double random = Math.random()*5000;
		try {
			myEB.hold();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			System.out.println("sleeping: " + random + " ms");
			sleep((long) random);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		myEB.complete();
	}

}
