package my_help;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Axis {
    private int size;
    private boolean finished;

    private AtomicBoolean can_start; //true when waiting threads can start their job (maybe they will need to wait for layer)
    private AtomicInteger just_arrived; //threads waiting before the whole system
    private AtomicInteger waiting; //threads waiting to begin their job
    private AtomicInteger working; //threads waiting for their layer or working
    private AtomicBoolean[] layers;

    public Axis(int size){
        this.size = size;
        finished = false;

        layers = new AtomicBoolean[size];
        for(AtomicBoolean bool : layers){
            bool.set(true);
        }
        can_start = new AtomicBoolean(false);
        just_arrived = new AtomicInteger(0);
        waiting = new AtomicInteger(0);
        working = new AtomicInteger(0);

    }

    public synchronized void before(int layer, boolean only_me) throws InterruptedException {
        just_arrived.getAndIncrement();
        try {
            while(can_start.get()){
                wait();
            }
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            just_arrived.getAndDecrement();
        }

        waiting.getAndIncrement();
        try {
            while(!can_start.get()){
                wait();
            }
        } catch (InterruptedException e) {
            if(waiting.get() + working.get() == 1){  //between decreasing waiting and increasing working nothing can happen
                we_finished();
            }
            throw new InterruptedException();
        } finally {
            waiting.getAndDecrement();
            if(waiting.get() == 0){
                can_start.set(false);
                notifyAll();
            }
        }

        working.getAndIncrement();
        try {
            while(!layers[layer].get()){
                wait();
            }
        } catch (InterruptedException e) {
            if((can_start.get() && waiting.get() + working.get() == 1) || (!can_start.get() && working.get() == 1)){  //between decreasing waiting and increasing working nothing can happen
                we_finished();
            }
            working.getAndDecrement();
            throw new InterruptedException();
        }

        layers[layer].set(false);
    }

    private synchronized void we_finished(){
        finished = true;
    }

    public synchronized boolean if_finished(){
        return finished;
    }

    public synchronized void after(int layer){
        working.getAndDecrement();
        layers[layer].set(true);
        notifyAll();

        if((can_start.get() && waiting.get() + working.get() == 0) || (!can_start.get() && working.get() == 0)){  //between decreasing waiting and increasing working nothing can happen
            we_finished();
        }
    }

    public synchronized void start_axis(){
        if(waiting.get() != 0){
            finished = false;
            can_start.set(true);
            notifyAll();
        }
    }
}
