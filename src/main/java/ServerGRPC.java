import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.zeromq.ZContext;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

/*
 * gRPC Server instance
 */
public class ServerGRPC {
    public void start(PubZMQ zmqPublisher, GreeterAOD greeter, Map<String, String> envVariables) throws IOException, InterruptedException {
        int grpcServerPort = Integer.parseInt(envVariables.get("GRPC_PORT"));
        Server server = ServerBuilder.forPort(grpcServerPort).addService(new GreeterImplGRPC(zmqPublisher, greeter)).build();
        server.start();
        System.out.println("GRPC Server started at :" + server.getPort());
        server.awaitTermination(); // I think this makes it so that grpc server can only be run after everything (zmq, http, etc) runs
    }
}
