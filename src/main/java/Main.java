import com.sun.net.httpserver.HttpServer;
import org.zeromq.ZContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*
 * Entry point
 */
public class Main {
    public static void main(String[] args) {
        // TODO:
        //  - load env
        // load ENV

        // start DB instance
        GreeterAOD greeter = new GreeterAOD();
        Connection dbConn = null;
        try {
            dbConn = greeter.startDB();
            if (dbConn.isValid(5)) {
                System.out.println("Connected to PostgreSQL successfully");
            }
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
            if (dbConn != null) {
                serverGRPC.start(zmqPublisher, greeter);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }

        // start SSE server
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            // Routes
            server.createContext("/", new ServerSSE.HelloWorld());
            server.createContext("/yo", new ServerSSE.SSE(messageQueue));

            server.setExecutor(null); // creates a default executor
            server.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

// Project Structure -> Libraries -> "+" sign -> add jars
