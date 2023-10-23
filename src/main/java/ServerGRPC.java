import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.zeromq.ZContext;

import java.io.IOException;

public class ServerGRPC {
    // public static void main(String[] args) {
    //     try {
    //         ZContext context = new ZContext();
    //         PubZMQ publisher = new PubZMQ(context);
    //
    //         Server server = ServerBuilder.forPort(8999).addService(new GreeterImplGRPC(publisher)).build();
    //         server.start();
    //         System.out.println("GRPC Server started at " + server.getPort());
    //         server.awaitTermination();
    //     } catch (IOException | InterruptedException e) {
    //         System.out.println(e.getMessage());
    //     }
    // }

    public void start(PubZMQ zmqPublisher) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(8999).addService(new GreeterImplGRPC(zmqPublisher)).build();
        server.start();
        System.out.println("GRPC Server started at " + server.getPort());
        server.awaitTermination();
    }
}
