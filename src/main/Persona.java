package main;


public class Persona extends Thread
{
    int id;
    SharedPosition positions;
    int changePos = 0;
    int attempts = 0;
    float totalDistance = 0.f;
    boolean requestDone = true;
    boolean requestInterrupt = false;
    float[] actualPosition;

    Persona(int id, SharedPosition positions)
    {
        this.id = id;
        this.positions = positions;
        this.positions.addPerson(id, this);
        this.setName("P-"+id);
        this.actualPosition = new float[]{0.f,0.f};
    }

    private float[] generateNewPos()
    {
        float[] ret = new float[2];
        ret[0] = (float) ((Math.random()*20.f)-10.f);
        ret[1] = (float) ((Math.random()*20.f)-10.f);
        return ret;
    }

    public void endRequest(MoveRequest mv)
    {
        if(mv.hasWaited)
            attempts++;
        changePos++;
        requestDone = true;
    }

    public void run()
    {
        try
        {
            while (true)
            {
                // System.out.println("Request Done: "+ requestDone);
                if (requestDone && !requestInterrupt)
                {
                    requestDone = false;
                    float[] nextPos = generateNewPos();

                    // System.out.println("----"+positions.distance(nextPos, currentPos));
                    // System.out.println("Distanza totale di "+ getName()+" :"+(float)totalDistance);
                    //System.out.println(getName() + " genera nuova richiesta!");
                    positions.sendRequest(new MoveRequest(id, nextPos, this));
                }
                sleep(100);
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("Nome: "+getName()+"\nCambi: "+changePos+"\nAttese: "+attempts+"\nDistanza: "+totalDistance +
                    "\nPosizione finale: "+actualPosition[0]+", "+actualPosition[1]+"\n");
        }
    }

}
