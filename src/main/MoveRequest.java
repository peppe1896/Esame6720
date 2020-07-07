package main;

public class MoveRequest extends Thread
{
    int id;
    float[] newPos;

    MoveRequest(int id, float[] newPos)
    {
        this.id = id;
        this.newPos = newPos;
    }

    public void run()
    {
        while(true)
        {
            ;
        }
    }
}
