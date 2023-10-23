import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.concurrent.BlockingQueue;

public class SubZMQ implements Runnable {
    private final ZMQ.Socket globalSub;
    public String defaultTopic = "miki-topic";
    private final BlockingQueue<String> messageQueue;

    public SubZMQ(ZContext context, BlockingQueue<String> messageQueue) {
        this.messageQueue = messageQueue;
        this.globalSub = context.createSocket(SocketType.SUB);
        globalSub.connect("tcp://localhost:5555");
        globalSub.subscribe(defaultTopic.getBytes());
        System.out.println("BE-1 ZMQ-SUB is connected to 5555");
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            String message = new String(globalSub.recv());
            System.out.printf("From BE-1 PUB : %s\n", message);
            try {
                // Push the received message to the messageQueue
                messageQueue.put(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}