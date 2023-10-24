import io.grpc.stub.StreamObserver;
import proto.GreeterGrpc;
import proto.Helloworld;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;

/*
 * gRPC handlers
 */
public class GreeterImplGRPC extends GreeterGrpc.GreeterImplBase {
    private final PubZMQ zmqPublisher;
    private final GreeterAOD greeter;

    public GreeterImplGRPC(PubZMQ publisher, GreeterAOD greeter) {
        this.zmqPublisher = publisher;
        this.greeter = greeter;
    }

    @Override
    public void sayHello(Helloworld.HelloRequest request, StreamObserver<Helloworld.HelloReply> responseObserver) {
        Helloworld.HelloReply reply = Helloworld.HelloReply.newBuilder().setMessage("you request was sent to BE-2 ZMQ-SUB hopefully.").build();
        String messageBase64 = Base64.getEncoder().encodeToString(request.getName().getBytes());
        System.out.println("gRPC, client says: " + messageBase64);

        zmqPublisher.sendMessage(messageBase64);
        System.out.printf("PUB sent %s to sub\n", messageBase64);

        try {
            GreeterAOD newGreeter = new GreeterAOD();
            // TODO: change ID to env
            // newGreeter.setIdentifier("1");
            newGreeter.setMessage(messageBase64);

            greeter.create(newGreeter);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
