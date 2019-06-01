package patterns.producerConsumer;

import java.util.LinkedList;

public class BoundedBuffer {
    // Create a list shared by producer and consumer
    private LinkedList<String> list = new LinkedList<>();
    private volatile String message;

    // Function called by producer thread
    public void produce(String message) throws InterruptedException {
        while (true) synchronized (this) {
            int capacity = 1;

            // producer thread waits while list is full
            while (list.size() == capacity)
                wait();

            // to insert the jobs in the list
            list.add(message);

            // notifies the consumer thread that now it can start consuming
            notify();

        }
    }

    // Function called by consumer thread
    public void consume() throws InterruptedException {
        while (true) synchronized (this) {
            // consumer thread waits while list is empty
            while (list.size() == 0)
                wait();

            //to retrieve the first job in the list
            message= list.removeFirst();

            // Wake up producer thread
            notify();
        }
    }

    public String getMessage() {
        return message;
    }
}
