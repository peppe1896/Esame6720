package main;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int N = 100;
        Persona[] persone = new Persona[N];
        SharedBuffer sharedBuffer = new SharedBuffer();
        SharedPosition sharedPosition = new SharedPosition(persone, sharedBuffer);
        for(int i=0;i<N;i++) {
            persone[i] = new Persona(i, sharedBuffer);
            persone[i].start();
        }
        sharedPosition.start();
        Thread.sleep(30000);
        System.out.println("\n\n---\n Interrupt dei threads Persona\n---");
        for(Persona pp:persone)
            pp.interrupt();
        System.out.println("\n\n---\n Interrupt del thread SharedPosition\n---");
        sharedPosition.interrupt();
    }
}
