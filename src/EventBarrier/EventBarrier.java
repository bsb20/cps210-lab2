package EventBarrier;
public class EventBarrier {

private int myTCount;
private boolean mySignal;

	public EventBarrier(){
		initialize(); 
	}

	private void initialize() {
		myTCount = 0;
		mySignal = false;
	}
	
	/*
	 * First called by threads. If signaled then return immediately. Else wait until the the signal is switched on to return.
	 */
	public synchronized void hold() throws InterruptedException{
		while (mySignal == false){
			super.wait();
		}
	}
	
	/*
	 * Signal that event has occurred, and wake up all threads waiting on the event. Wait for each thread to respond completely
	 * before returning the unsignaled state.
	 */
	public synchronized void signal() throws InterruptedException{
		if (mySignal = true)
			return;
		
		mySignal = true;
		notifyAll();
		while (waiters() != 0)
			super.wait();	
		mySignal = false;
	}
	
	/*
	 * Thread calls when it is finished responding to the event. When all threads have responded notify the thread that activated 
	 * the signal, so it can then turn off the signal
	 */
	public void complete(){
		myTCount--;
		if (waiters() == 0)
			notify();		
	}
	
	/*
	 * Returns the number of threads waiting for a signal or that have not finished responding to a signal
	 */
	public synchronized int waiters(){
		return myTCount;
	}
}
