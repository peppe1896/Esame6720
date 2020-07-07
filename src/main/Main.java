package main;

public class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        int N = 10;
        SharedPosition sp = new SharedPosition(N);
        Persona[] p = new Persona[N];
        sp.start();
        for(int i=0;i<N;i++)
        {
            p[i] = new Persona(i, sp);
            p[i].start();
        }
        Thread.sleep(60000);
        for(Persona pp:p)
            pp.requestInterrupt = true;

        sp.interrupt();

        for(Persona pp:p)
            pp.interrupt();
        //Thread.sleep(10000);
    }
}
