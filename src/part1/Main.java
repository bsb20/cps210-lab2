package EventBarrier;

import EventBarrier.EventBarrier;
import EventBarrier.WaiterThread;
import EventBarrier.Signaler;

public class Main {
	public static void main (String[] args) throws InterruptedException{
		EventBarrier eb = new EventBarrier();
		WaiterThread[] threads = new WaiterThread[5];
		for (int i=0; i<=4; i++){
			threads[i] = new WaiterThread(eb);
		}
		for (int i=0; i<=4; i++){
			threads[i].start();
		}
		Signaler s = new Signaler(eb);
		s.start();

	}
}
