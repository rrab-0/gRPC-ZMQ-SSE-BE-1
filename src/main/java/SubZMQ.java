import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/*
 * ZeroMQ Subscriber
 */
public class SubZMQ implements Runnable {
    private final ZMQ.Socket globalSub;
    public String defaultTopic = "miki-topic";
    private final BlockingQueue<String> messageQueue;
    private final GreeterAOD greeter;

    public SubZMQ(ZContext context, BlockingQueue<String> messageQueue, Map<String, String> envVariables, GreeterAOD greeter) {
        String subZmqHost = envVariables.get("ZMQ_SUB_HOST");
        String subZmqPort = envVariables.get("ZMQ_SUB_PORT");
        this.greeter = greeter;
        this.messageQueue = messageQueue;
        this.globalSub = context.createSocket(SocketType.SUB);
        globalSub.connect("tcp://" + subZmqHost + ":" + subZmqPort);
        globalSub.subscribe(defaultTopic.getBytes());
        System.out.println("BE-1 ZMQ-SUB is connected to " + subZmqHost + ":" + subZmqPort);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            String message = new String(globalSub.recv());
            System.out.printf("From BE-2 SUB: %s\n", message);

            try {
                GreeterAOD newGreeter = new GreeterAOD();
                // TODO: change ID to env
                // newGreeter.setIdentifier("1");
                newGreeter.setMessage(message);

                this.greeter.create(newGreeter);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            try {
                // Push the received message to the messageQueue
                messageQueue.put(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}