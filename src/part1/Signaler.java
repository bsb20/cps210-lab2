package EventBarrier;

public class Signaler extends Thread{
	private EventBarrier myEB;
	
	
	public Signaler(EventBarrier eb){
		myEB = eb;
	}
	
	public void run() {	
		try {
			System.out.println("signaled!");
			myEB.signal();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
