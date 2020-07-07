package main;

public class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        int N = 100;
        SharedPosition sp = new SharedPosition(N);
        Persona[] p = new Persona[N];
        sp.start();
        for(int i=0;i<N;i++)
        {
            p[i] = new Persona(i, sp);
            p[i].start();
        }
        Thread.sleep(5000);
        for(Persona pp:p)
            pp.interrupt();
    }
}
