import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class PubZMQ {
    public ZMQ.Socket globalPub;
    public String defaultTopic = "miki-topic";

    public PubZMQ(ZContext context) {
        start(context);
    }

    public void start(ZContext context) {
        ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
        publisher.bind("tcp://localhost:5555");
        this.globalPub = publisher;
        System.out.println("BE-1 ZMQ-PUB is up and running at 5555");
    }

    public void sendMessage(String msg) {
        this.globalPub.send(defaultTopic + " " + msg);
    }
}
