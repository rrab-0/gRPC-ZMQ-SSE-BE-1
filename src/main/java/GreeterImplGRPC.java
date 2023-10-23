import io.grpc.stub.StreamObserver;
import proto.GreeterGrpc;
import proto.Helloworld;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GreeterImplGRPC extends GreeterGrpc.GreeterImplBase {
    private final PubZMQ zmqPublisher;
    private final Connection dbConn;
    // private final GreeterAOD greeter;

    public GreeterImplGRPC(PubZMQ publisher, Connection dbConn) {
        this.zmqPublisher = publisher;
        this.dbConn = dbConn;
        // this.greeter = greeter;
    }

    @Override
    public void sayHello(Helloworld.HelloRequest request, StreamObserver<Helloworld.HelloReply> responseObserver) {
        Helloworld.HelloReply reply = Helloworld.HelloReply.newBuilder().setMessage("you request was sent to BE-2 ZMQ-SUB hopefully.").build();
        System.out.println("gRPC, client says: " + request.getName());

        zmqPublisher.sendMessage(request.getName());
        System.out.printf("PUB sent %s to sub\n", request.getName());

        // try {
        //     GreeterAOD newGreeter = new GreeterAOD();
        //     // TODO: change ID to env
        //     newGreeter.setIdentifier("1");
        //     newGreeter.setMessage("monkey");
        //
        //     greeter.create(newGreeter);
        // } catch (SQLException e) {
        //     System.out.println(e.getMessage());
        // }

        try {
            GreeterAOD greeter = new GreeterAOD();
            // TODO: change ID to env
            greeter.setIdentifier("1");
            greeter.setMessage("monkey");
            PreparedStatement st = dbConn.prepareStatement("INSERT INTO dump (identifier, message) VALUES (?, ?)");
            st.setString(1, greeter.getIdentifier());
            st.setString(2, greeter.getMessage());

            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
