package EventBarrier;
import java.util.HashSet;
public class EventBarrier {

private HashSet<Runnable> myThreads;
private boolean mySignal;

	public EventBarrier(){
		initialize(); 
	}

	private void initialize() {
		myThreads = new HashSet<Runnable>();
		mySignal = false;
	}
	
	/*
	 * First called by threads. If signaled then return immediately. Else wait until the the signal is switched on to return.
	 */
	public synchronized void wait(Runnable thread) throws InterruptedException{
		myThreads.add(thread);
		while (mySignal == false){
			super.wait();
		}
	}
	
	/*
	 * Signal that event has occurred, and wake up all threads waiting on the event. Wait for each thread to respond completely
	 * before returning the unsignaled state.
	 */
	public synchronized void signal() throws InterruptedException{
		mySignal = true;
		notifyAll();
		while (waiters() != 0)
			super.wait();	
		mySignal = false;
	}
	
	/*
	 * Thread calls when it is finished responding to the event.
	 */
	public void complete(Runnable thread){
		myThreads.remove(thread);
		if (waiters() == 0)
			notify();		
	}
	
	public synchronized int waiters(){
		return myThreads.size();
	}
}
