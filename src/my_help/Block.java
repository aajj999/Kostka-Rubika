package my_help;

public class Block {
    boolean x;
    boolean y;
    boolean z;

    public Block(){
        x = true;
        y = true;
        z = true;
    }

    private synchronized void plock(String message){
        //System.out.println(message);
        //System.out.println("   " + x + " " + y + " " + z);
        //System.out.println("");
    }

    public synchronized void lock(boolean x, boolean y, boolean z) throws InterruptedException {
        plock("lock");
        if(x){
            while(!this.x){
                wait();
            }
            this.x = false;
        }

        if(y){
            while (!this.y) {
                wait();
            }
            this.y = false;
        }

        if(z){
            while (!this.z) {
                wait();
            }
            this.z = false;
        }
        plock(" po lock");
    }

    public synchronized void unlock(boolean x, boolean y, boolean z){
        plock("unlock");
        if(x){
            this.x = true;
        }
        if(y){
            this.y = true;
        }
        if(z){
            this.z = true;
        }

        notifyAll();
        plock("po unlock");
    }
}
