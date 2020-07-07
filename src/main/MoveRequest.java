package main;

public class MoveRequest
{
    int id;
    float[] newPos;
    Persona p;
    boolean isDone = false;
    boolean hasWaited = false;

    MoveRequest(int id, float[] newPos, Persona p)
    {
        this.id = id;
        this.newPos = newPos;
        this.p = p;
    }
}
