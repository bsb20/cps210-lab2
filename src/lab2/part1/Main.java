package lab2.part1;

import lab2.EventBarrier;

/*
 * Test written for the EventBarrier Class. 5 Threads wait on the eventbarrier. The main method
 * then calls signal, awakening the threads, which then call complete() until the signal is turned off.
 * NOTE: The commented-out lines of code in EventBarrier must be un-commented for this test to work.
 */

public class Main {
    private static class WaiterThread extends Thread {
        private EventBarrier myEB;

        public WaiterThread(EventBarrier eb){
            myEB = eb;
        }

        public void run() {
            double random = Math.random()*5000;
            myEB.hold();
            try {
                System.out.println("sleeping: " + random + " ms");
                sleep((long) random);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            myEB.complete();
        }
    }

	public static void main(String[] args) {
		EventBarrier eb = new EventBarrier();
		WaiterThread[] threads = new WaiterThread[5];
		for (int i=0; i<=4; i++){
			threads[i] = new WaiterThread(eb);
		}
		for (int i=0; i<=4; i++){
			threads[i].start();
		}
        try {
            Thread.sleep(5);
        }
        catch (InterruptedException e) {}
        eb.signal();
	}
}
