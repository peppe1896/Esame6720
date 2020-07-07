package main;

public class MoveRequest
{
    int id;
    float[] newPos;
    Persona p;
    boolean isDone = false;
    boolean hasWaited = false;
    int count = 0;

    MoveRequest(int id, float[] newPos, Persona p)
    {
        this.id = id;
        this.newPos = newPos;
        this.p = p;
    }

    public MoveRequest(MoveRequest mv)
    {
        id = mv.id;
        newPos = mv.newPos;
        isDone = mv.isDone;
        p = mv.p;
        hasWaited = mv.hasWaited;
    }

    public String toString()
    {
        return "\n\nid " + id + "\nisDone: "+ isDone + "\nhasWaited: " + hasWaited + "\n\n";
    }
}
