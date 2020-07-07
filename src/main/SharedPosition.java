package main;

import java.util.ArrayList;

public class SharedPosition extends Thread
{
    float[][] positions;
    ArrayList<MoveRequest> requests;
    Persona[] p;

    SharedPosition(int N)
    {
        positions = new float[N][2];
        requests = new ArrayList<>();
        Persona[] p = new Persona[N];
    }

    public synchronized void sendRequest(MoveRequest mv)
    {
        System.out.println("Ricevuta richiesta da P-"+mv.id+". Dim di queue:"+requests.size());
        requests.add(mv);
        notifyAll();
    }

    public synchronized MoveRequest getNextRequest() throws InterruptedException
    {
        while(requests.size() == 0)
            wait();
        MoveRequest temp = requests.remove(0);
        System.out.println("Prelevo la richiesta di "+temp.p.getName());
        return temp;
    }

    public void addPerson(int id, Persona p)
    {
        // positions.add(id, new float[]{0,0});
        positions[id] = new float[]{0,0};
        this.p[id] = p;
    }


    public synchronized void move(MoveRequest mv) throws InterruptedException
    {
        // System.out.println("Sopra");
        int id = mv.id;
        float[] position = mv.newPos;

        while(isNear(mv.id))
        {
            System.out.println("Dentro");
            wait();
        }

        System.out.println("Sotto");
        // positions.set(id, position);
        positions[id] = position;
        notifyAll();
    }

    public synchronized boolean isNear(int id)
    {
        //for (int i=0;i<positions.size();i++)
        for (int i=0;i<positions.length;i++)
            if(i != id) {
                if (Math.abs(distance(positions[id], positions[i])) <= 1) {
                    System.out.println("IS NEAR:" + Math.abs(distance(positions[id], (positions[i]))));
                    return true;
                }
            }
        return false;
    }

    public synchronized boolean isNear(MoveRequest mv)
    {
        // for (int i=0;i<positions.size();i++)
        for (int i=0;i<positions.length;i++)
            if(i != mv.id) {
                if ((Math.abs(distance(positions[mv.id], (mv.newPos))) <= 1)) {
                    return true;
                }
            }
        return false;
    }

    public static float distance(float[] a, float[] b)
    {
        return (float) Math.sqrt(((a[0]*a[0])-(b[0]*b[0]))+((a[1]*a[1])+(b[1]*b[1])));
    }

    public synchronized float[] getPosition(int id)
    {
        float[] temp = positions[id];
        //System.out.println("Mando la pos di "+id + ":"+ temp[0]+", "+temp[1]);
        return temp;
    }

    public synchronized void solveRequest(MoveRequest mv)
    {
        System.out.println("\n*****\n"+ mv.hasWaited+"\n****\n");
        if (!isNear(mv))
        {
            System.out.println("Sposto P-"+mv.id + " in "+mv.newPos[0]+", "+mv.newPos[1]+"\n");
            positions[mv.id] = mv.newPos;
            mv.isDone = true;
            System.out.println("Posizione di "+mv.id+" :"+positions[mv.id][0]+", "+positions[mv.id][1]);
        }
        else
        {
            mv.hasWaited = true;
        }
        if(!mv.isDone)
        {
            sendRequest(mv);
            System.out.println("Rimetto dentro la queue la richiesta di p-"+mv.id);
            System.out.println("Dimensione queue "+requests.size());
        }
        else {
        //    if (mv.hasWaited)
        //        mv.p.attempts = mv.p.attempts + 1;
        //    mv.p.changePos = mv.p.changePos + 1;
            Persona p = mv.p;
            p.endRequest(mv);
            mv.p.requestDone = true; // risolta, persona puà fare una nuova richiesta
            //System.out.println("Risolta la move request "+mv+ "\nOra P-"+mv.id+" genera nuova richiesta");
        }
        // notify();
    }


    public void run()
    {
        try
        {
            while (true)
            {
                MoveRequest temp = getNextRequest();
                solveRequest(temp);
                sleep(100);
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("\nTerminazione di SharedPosition. Numero di elementi in coda: "+ requests.size() +"\n");
            while(!requests.isEmpty())
            {
                MoveRequest mv = requests.remove(0);
                Persona p = mv.p;
                positions[mv.id] = mv.newPos;
                // risolta, persona puà fare una nuova richiesta
                p.endRequest(mv);
                System.out.println("Risolta la move request "+mv);
            }
            System.out.println("Shared Position Interrupted.");
        }
    }

}
