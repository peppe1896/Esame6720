package main;

import com.sun.jdi.InternalException;

import java.util.ArrayList;

public class SharedPosition extends Thread
{
    ArrayList<float[]> positions;
    ArrayList<MoveRequest> requests;
    boolean isWorking = false;

    SharedPosition(int N)
    {
        positions = new ArrayList<>(N);
        requests = new ArrayList<>();
    }

    public synchronized void sendRequest(MoveRequest mv)
    {
        System.out.println("Ricevuta una Move request da "+mv.p.getName());
        requests.add(mv);
        notifyAll();
    }

    public synchronized MoveRequest getNextRequest() throws InterruptedException
    {
        while(isWorking || requests.size() == 0)
        {
            System.out.println("In attesa per mandare nuova request");
            wait();
        }
        MoveRequest temp = requests.remove(0);
        System.out.println("Ok->mando la richiesta di "+temp.p.getName());
        return temp;
    }

    public void addPerson(int id)
    {
        positions.add(id, new float[]{0,0});
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
        positions.set(id, position);
        notifyAll();
    }

    public boolean isNear(int id)
    {
        for (int i=0;i<positions.size();i++)
            if(i != id) {
                if ((Math.abs(distance(positions.get(id), (positions.get(i)))) <= 1)) {
                    System.out.println("IS NEAR:" + Math.abs(distance(positions.get(id), (positions.get(i)))));
                    return true;
                }
            }
        return false;
    }

    public boolean isNear(MoveRequest mv)
    {
        for (int i=0;i<positions.size();i++)
            if(i != mv.id) {
                if ((Math.abs(distance(positions.get(mv.id), (mv.newPos))) <= 1)) {
                    return true;
                }
            }
        return false;
    }

    public float distance(float[] a, float[] b)
    {
        return (float) Math.sqrt((a[0]*a[0]-b[0]*b[0])+(a[1]*a[1]+b[1]*b[1]));
    }

    public synchronized float[] getPosition(int id)
    {
        float[] temp = positions.get(id);
        System.out.println("Mando la pos di "+id + ":"+ temp[0]+", "+temp[1]);
        return temp;
    }

    public synchronized void solveRequest(MoveRequest mv) throws InterruptedException
    {
        if (!isNear(mv))
        {
            positions.set(mv.id, mv.newPos);
            mv.isDone = true;
        }
        else
        {
            System.out.println("Ho atteso, mannaggia");
            mv.hasWaited = true;
        }
        if(!mv.isDone)
        {
            sendRequest(mv); // ritorna dentro la queue
            System.out.println("Ritorna dentro la queue!");
        }
        else {
            if (mv.hasWaited)
            {
                System.out.println("HO ATTESO");
                mv.p.attempts = mv.p.attempts + 1;
            }
            mv.p.requestDone = true; // risolta, persona puà fare una nuova richiesta
            System.out.println("Risolta la move request.");
            isWorking = false;
        }
        notifyAll();
    }


    public void run()
    {
        try
        {
            while (true)
            {
                MoveRequest temp = getNextRequest();
                solveRequest(temp);
                //if (waitingForMove == 2)
                //{
                //    waitingForMove = 0;
                //    notifyAll();
                //}
                // callNotify();

                sleep(100);
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("Terminazione di SharedPosition");
            while(!requests.isEmpty())
            {
                MoveRequest mv = requests.remove(0);
                if(!isNear(mv))
                {
                    positions.set(mv.id, mv.newPos);
                }
                mv.isDone = true;
                if (mv.hasWaited)
                    mv.p.attempts++;
                mv.p.requestDone = true; // risolta, persona puà fare una nuova richiesta
                System.out.println("Risolta la move request.");
            }
        }
    }

}
