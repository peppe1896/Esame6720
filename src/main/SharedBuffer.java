package main;

import java.util.ArrayList;

public class SharedBuffer {
    private ArrayList<MoveRequest> requests = new ArrayList();
    private int actualSize = 0;

    public synchronized void receiveRequest(MoveRequest mr){
        requests.add(mr);
        ++actualSize;
        notify();
    }

    public synchronized MoveRequest getRequest() throws InterruptedException{
        while(actualSize == 0)
            wait();
        actualSize--;
        MoveRequest returnRequest = requests.remove(0);
        return returnRequest;
    }

    public synchronized ArrayList<MoveRequest> getRequestRemaining(){
        ArrayList<MoveRequest> returnRequests = new ArrayList<>();
        while(!requests.isEmpty())
            returnRequests.add(requests.remove(0));
        return returnRequests;
    }

    public synchronized boolean isEmpty(){
        return requests.isEmpty();
    }
}
