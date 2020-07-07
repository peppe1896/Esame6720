package main;

import java.util.ArrayList;

public class SharedPosition extends Thread
{
    ArrayList<float[]> positions;
    ArrayList<Persona> personas;
    boolean hasWaited = false;
    int waitingForMove = 0;

    SharedPosition(int N)
    {
        positions = new ArrayList<>(N);
    }

    public void addPerson(int id)
    {
        positions.add(id, new float[]{0,0});
    }

    public void addPerson(Persona p)
    {
        //positions.add(id, new float[]{0,0});
        ;
    }

    public synchronized boolean move(int id, float[] position) throws InterruptedException
    {
        // System.out.println("Sopra");
        while(isNear(id) && !(waitingForMove==3))
        {
            hasWaited = true;
            System.out.println("Dentro");
            wait();
        }
        System.out.println("Sotto");
        positions.set(id, position);
        boolean temp = hasWaited;
        hasWaited = false;
        notifyAll();
        return temp;
    }

    public synchronized boolean isNear(int id)
    {
        for (int i=0;i<positions.size();i++)
            if(i!=id) {
                System.out.println("QUI");
                if (!(Math.abs(distance(positions.get(id), (positions.get(i)))) <= 1)) {
                    System.out.println("QUI2->"+distance(positions.get(id), (positions.get(i))));
                    waitingForMove++;
                    return true;
                }
            }
        waitingForMove++;
        System.out.println("QUI3");
        return false;
    }

    public float distance(float[] a, float[] b)
    {
        return (float) Math.sqrt((a[0]*a[0]-b[0]*b[0])+(a[1]*a[1]+b[1]*b[1]));
    }

    public float[] getPosition(int id)
    {
        return positions.get(id);
    }

    private synchronized void callNotify()
    {
        notifyAll();
    }

    public void run()
    {
        try
        {
            while (true)
            {
                if (waitingForMove == 3)
                {
                    waitingForMove = 0;
                    notifyAll();
                }
                // callNotify();
                sleep(100);
            }
        }catch (InterruptedException e)
        {

        }
    }

}
