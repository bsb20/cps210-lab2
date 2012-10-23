package lab2;


public class EventBarrier {

    private int myTCount;
    private boolean mySignal;

	public EventBarrier() {
		myTCount = 0;
		mySignal = false;
	}

	/*
	 * First called by threads. If signaled then return immediately. Else wait until the the signal is switched on to return.
	 */
	public synchronized void hold() {
		if (mySignal) {
			return;
        }
		myTCount++;
		//System.out.println("call to wait    count: " + waiters());
		while (!mySignal){
			System.out.println("Thread called hold(), " + waiters() + " threads waiting on Signal");
            try {
                super.wait();
            }
            catch (InterruptedException e) {}
		}
	}

	/*
	 * Signal that event has occurred, and wake up all threads waiting on the event. Wait for each thread to respond completely
	 * before returning the unsignaled state.
	 */
	public synchronized void signal() {
		if (mySignal) {
			return;
		}
		System.out.println("call to signal; Signal now is ON");
		mySignal = true;
		notifyAll();
		while (waiters() != 0) {
            try {
                super.wait();
            }
            catch (InterruptedException e) {}
		}
		mySignal = false;
		System.out.println("Signal switched to OFF");
	}

	/*
	 * Thread calls when it is finished responding to the event. When all threads have responded notify the thread that activated
	 * the signal, so it can then turn off the signal
	 */
	public synchronized void complete() {
		myTCount--;
		System.out.println("call to complete    count: " + waiters());
		if (waiters() == 0)
			notifyAll();
	}

	/*
	 * Returns the number of threads waiting for a signal or that have not finished responding to a signal
	 */
	public synchronized int waiters(){
		return myTCount;
	}
}
