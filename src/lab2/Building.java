package lab2;

import java.util.ArrayList;
import java.util.List;

import lab2.Elevator;


public class Building
{
    public static int F = 10;

    private List<Elevator> myElevators;
    private EventBarrier[] myUpRiders, myDownRiders;


    public Building(int E) {
        myElevators = new ArrayList<Elevator>(E);
        myUpRiders = new long[F];
        myDownRiders = new long[F];
        for (int i = 0; i < E; i++)
            myElevators.add(new Elevator(F));
        for (int i = 0; i < F; i++) {
            myUpRiders[i] = new EventBarrier();
            myDownRiders[i] = new EventBarrier();
        }
    }

    public synchronized EventBarrier getUpRiders(int floor) {
        return myUpRiders[floor];
    }

    public synchronized EventBarrier getDownRiders(int floor) {
        return myDownRiders[floor];
    }

    public synchronized Elevator awaitUp(int floor) {
        myUpRiders[f].wait();
    }

    public synchronized Elevator awaitDown(int floor) {
        myDownRiders[f].wait();
    }
}
