package main;

import java.util.ArrayList;

public class SharedPosition extends Thread
{
    ArrayList<MoveRequest> requests;
    Persona[] p;
    int size = 0;

    SharedPosition(int N)
    {
        requests = new ArrayList<>();
        p = new Persona[N];
    }

    public synchronized void sendRequest(MoveRequest mv)
    {
        System.out.println("Ricevuta richiesta da P-"+mv.id+". Dim di queue:"+requests.size());
        requests.add(mv);
        ++size;
        notifyAll();
    }

    public synchronized MoveRequest getNextRequest() throws InterruptedException
    {
        while(size == 0)
            wait();
        MoveRequest temp = requests.remove(0);
        System.out.println("Prelevo la richiesta di "+temp.p.getName());
        --size;
        return temp;
    }

    public void addPerson(int id, Persona p)
    {
        // positions.add(id, new float[]{0,0});
        // positions[id] = new float[]{0,0};
        this.p[id] = p;
    }

    public synchronized boolean isNear(MoveRequest mv)
    {
        // for (int i=0;i<positions.size();i++)
        for (int i=0;i<p.length;i++)
            if(i != mv.id) {
                if ((Math.abs(distance(p[mv.id].actualPosition, (mv.newPos))) <= 1)) {
                    return true;
                }
            }
        return false;
    }

    public float distance(float[] a, float[] b)
    {
        return (float) Math.sqrt(((a[0]*a[0])-(b[0]*b[0]))+((a[1]*a[1])+(b[1]*b[1])));
    }

    public synchronized void solveRequest(MoveRequest mv)
    {
        if (!isNear(mv))
        {
            //System.out.println("Sposto P-"+mv.id + " in "+mv.newPos[0]+", "+mv.newPos[1]+"\n");
            p[mv.id].actualPosition = mv.newPos;
            mv.isDone = true;
            System.out.println("Posizione di "+mv.id+" :"+p[mv.id].actualPosition[0]+", "+p[mv.id].actualPosition[1]);
        }
        else
        {
            mv.hasWaited = true;
            p[mv.id].attempts += 1;
        }
        if(!mv.isDone)
        {
            mv.count = mv.count + 1;
            if (mv.count < 3)
                sendRequest(mv);
            else
                mv.isDone = true;
            //System.out.println("Rimetto dentro la queue la richiesta di p-"+mv.id);
            //System.out.println("Dimensione queue "+requests.size());
        }
        else
        {
            p[mv.id].changePos = p[mv.id].changePos + 1;
            // p[mv.id].endRequest(mv);
            p[mv.id].totalDistance += distance(mv.newPos, p[mv.id].actualPosition);
            p[mv.id].requestDone = true; // risolta, persona puà fare una nuova richiesta
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
                if(requests.size()< 1)
                    sleep(3000);
                MoveRequest temp = getNextRequest();
                solveRequest(temp);
                sleep(30);
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("\nTerminazione di SharedPosition. Numero di elementi in coda: "+ requests.size() +"\n");
            while(!requests.isEmpty())
            {
                MoveRequest mv = requests.remove(0);
                p[mv.id].actualPosition = mv.newPos;
                // risolta, persona puà fare una nuova richiesta
                p[mv.id].endRequest(mv);
                System.out.println("Risolta la move request "+mv);
            }
            System.out.println("Shared Position Interrupted.");
        }
    }

}
