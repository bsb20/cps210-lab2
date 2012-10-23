package lab2.part1;

import lab2.EventBarrier;


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
