package main;

public class SharedPosition extends Thread {
    Persona[] people;                   // Persone; l'id della persona corrisponde a indice array
    SharedBuffer buffer;

    SharedPosition(Persona[] persone, SharedBuffer sharedBuffer ) {
        this.buffer = sharedBuffer;
        people = persone;
    }

    public void addPerson(int id, Persona p) {
        this.people[id] = p;
    }

    public float distance(float[] a, float[] b) {
        float f = (float)Math.sqrt((((b[0]-a[0])*(b[0]-a[0]))+((b[1]-a[1])*(b[1]-a[1]))));
        return f;
    }

    public synchronized void solveRequest(MoveRequest mv){
        boolean isNear = false;
        Persona persona;
        int i = people.length - 1;
        while(i >= 0){
            if(i != mv.id){
                persona = people[i];
                float distance = distance(persona.actualPosition, mv.newPos);
                if(distance <= 1)
                    isNear = true;
            }
            i--;
        }
        if(!isNear){
            System.out.println("Aggiornando la posizione di P-"+mv.id);
            people[mv.id].totalDistance += distance(mv.newPos, people[mv.id].actualPosition);
            people[mv.id].countWaitingForChange = people[mv.id].countWaitingForChange + mv.countAttempts;
            people[mv.id].actualPosition = mv.newPos;
            people[mv.id].countChangePos += 1;
            people[mv.id].endRequest();
        }else{
            mv.countAttempts += 1;
            System.out.println("Reinserisco la richiesta di P-"+mv.id+". Attualmente reinvio numero: "+ mv.countAttempts);
            buffer.receiveRequest(mv);
        }
    }

    public synchronized void forceSolveRequest(MoveRequest mv){
        if(mv.countAttempts<100)
            solveRequest(mv);
        else {
            people[mv.id].endRequest();
            System.out.println("Chiusura forzata della richiesta di P-"+mv.id);
        }
    }

    public void run() {
        try {
            while (true) {
                MoveRequest temp = buffer.getRequest();
                solveRequest(temp);
                sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("---\nTerminazione di SharedPosition, consumando le MoveRequest rimaste nel buffer\n---");
            while(!buffer.isEmpty()) {
                try {
                    forceSolveRequest(buffer.getRequest());
                } catch (InterruptedException interruptedException) {}
            }
        }
    }

}
