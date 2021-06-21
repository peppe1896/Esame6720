package main;

public class MoveRequest {
    int id;
    float[] newPos;
    Persona persona;
    int countAttempts = 0;                      // Numero di volte che non è stata cambiata la posizione

    MoveRequest(int id, float[] newPos, Persona persona) {
        this.id = id;
        this.newPos = newPos;
        this.persona = persona;
    }

}
