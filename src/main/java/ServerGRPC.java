import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.zeromq.ZContext;

import java.io.IOException;
import java.sql.Connection;

/*
 * gRPC Server instance
 */
public class ServerGRPC {
    public void start(PubZMQ zmqPublisher, GreeterAOD greeter) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(8999).addService(new GreeterImplGRPC(zmqPublisher, greeter)).build();
        server.start();
        System.out.println("GRPC Server started at " + server.getPort());
        server.awaitTermination();
    }
}
