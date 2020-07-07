package main;

public class Persona extends Thread
{
    int id;
    SharedPosition positions;
    int changePos = 0;
    int attempts = 0;
    float totalDistance = 0.f;
   // MoveRequest mv;

    Persona(int id, SharedPosition positions)
    {
        this.id = id;
        this.positions = positions;
        this.positions.addPerson(id);
        this.setName("P-"+id);
    }

    private float[] generateNewPos()
    {
        float[] ret = new float[2];
        ret[0] = (float) ((Math.random()*20.f)-10.f);
        ret[1] = (float) ((Math.random()*20.f)-10.f);
        return ret;
    }

    public void run()
    {
        try
        {
            while (true)
            {
                float[] nextPos = generateNewPos();
                float[] currentPos = positions.getPosition(id);
                totalDistance += positions.distance(nextPos, currentPos);
                if(positions.move(id, nextPos))
                    attempts++;
                changePos++;
                sleep(100);
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("Nome: "+getName()+"\nCambi: "+changePos+"\nAttese: "+attempts+"\nDistanza: "+totalDistance);
        }
    }

}
