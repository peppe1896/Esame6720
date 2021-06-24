package main;

public class Persona extends Thread {
    int id;
    private SharedBuffer buffer;
    private SharedPosition sharedPosition;
    int countWaitingForChange = 0;
    int countChangePos = 0;
    float totalDistance = 0.f;
    boolean requestDone = true;             // Se attualmente la persona sta aspettando di essere spostata
    float[] actualPosition;

    Persona(int id, SharedBuffer buffer, SharedPosition sharedPosition) {
        this.id = id;
        this.buffer = buffer;
        this.setName("P-"+id);
        this.actualPosition = new float[]{0.f,0.f};
        this.sharedPosition = sharedPosition;
    }

    private synchronized float[] generateNewPos(){
        float[] ret = new float[2];
        ret[0] = (float) ((Math.random()*20.f)-10.f);
        ret[1] = (float) ((Math.random()*20.f)-10.f);
        return ret;
    }

    public synchronized void endRequest() {
        requestDone = true;
        notify();
    }

    synchronized void performNewRequest() throws InterruptedException{
        while(!requestDone)
            wait();
        requestDone = false;
        float[] nextPos = generateNewPos();
        buffer.receiveRequest(new MoveRequest(id, nextPos, this));
    }

    public void run() {
        try {
            while (true) {
                performNewRequest();
                sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("Nome: "+getName()+"\nNumero di cambi di posizione: "+ countChangePos +"\n" +
                    "Numero di attese per cambio di posizione: "+countWaitingForChange+"\n" +
                    "Distanza percorsa: "+totalDistance +
                    "\nPosizione finale: ("+actualPosition[0]+", "+actualPosition[1]+")\n");
        }
    }

}
