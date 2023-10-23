import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Map;

/*
 * ZMQ Publisher
 */
public class PubZMQ {
    public ZMQ.Socket globalPub;
    public String defaultTopic = "miki-topic";

    public PubZMQ(ZContext context, Map<String, String> envVariables) {
        start(context, envVariables);
    }

    public void start(ZContext context, Map<String, String> envVariables) {
        String pubZmqPort = envVariables.get("ZMQ_PUB_PORT");
        ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
        publisher.bind("tcp://localhost:" + pubZmqPort);
        this.globalPub = publisher;
        System.out.println("BE-1 ZMQ-PUB is up and running at :" + pubZmqPort);
    }

    public void sendMessage(String msg) {
        this.globalPub.send(defaultTopic + " " + msg);
    }
}
