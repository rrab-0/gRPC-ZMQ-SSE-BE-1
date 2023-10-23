import com.sun.net.httpserver.HttpServer;
import org.zeromq.ZContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        // load ENV
        // start DB instance
        GreeterAOD greeter = new GreeterAOD();
        try {
            Connection dbConn = greeter.startDB();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // start PubZMQ server
        ZContext pubZmqContext = new ZContext();
        PubZMQ zmqPublisher = new PubZMQ(pubZmqContext);

        // start SubZMQ server
        BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
        ZContext zmqSubContext = new ZContext();
        SubZMQ zmqSub = new SubZMQ(zmqSubContext, messageQueue);
        Thread zmqSubThread = new Thread(zmqSub);
        zmqSubThread.start();

        // start GRPC server
        ServerGRPC serverGRPC = new ServerGRPC();
        try {
            serverGRPC.start(zmqPublisher);
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }

        // start SSE server
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            // Routes
            server.createContext("/", new HelloWorld());
            server.createContext("/yo", new SSE());

            server.setExecutor(null); // creates a default executor
            server.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

// Project Structure -> Libraries -> "+" sign -> add jars
