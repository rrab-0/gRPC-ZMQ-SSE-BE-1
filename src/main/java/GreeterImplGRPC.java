import io.grpc.stub.StreamObserver;
import proto.GreeterGrpc;
import proto.Helloworld;

public class GreeterImplGRPC extends GreeterGrpc.GreeterImplBase {
    private final PubZMQ zmqPublisher;

    public GreeterImplGRPC(PubZMQ publisher) {
        this.zmqPublisher = publisher;
    }

    @Override
    public void sayHello(Helloworld.HelloRequest request, StreamObserver<Helloworld.HelloReply> responseObserver) {
        // Helloworld.HelloReply reply = Helloworld.HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
        Helloworld.HelloReply reply = Helloworld.HelloReply.newBuilder().setMessage("you request was sent to BE-2 ZMQ-SUB hopefully.").build();
        System.out.println("gRPC, client says: " + request.getName());

        zmqPublisher.sendMessage(request.getName());
        System.out.printf("PUB sent %s to sub\n", request.getName());

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
