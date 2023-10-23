import com.google.api.Http;
import com.sun.net.httpserver.HttpServer;
import org.zeromq.ZContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*
 * Entry point
 */
public class Main {
    public static void main(String[] args) {
        // load ENV
        Map<String, String> envVariables = DotEnvLoader.loadEnvVariables();

        // start DB instance
        GreeterAOD greeter = new GreeterAOD();
        Connection dbConn = null;
        try {
            dbConn = greeter.startDB(envVariables);
            if (dbConn.isValid(5)) {
                System.out.println("Connected to PostgreSQL successfully");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // start PubZMQ server
        ZContext pubZmqContext = new ZContext();
        PubZMQ zmqPublisher = new PubZMQ(pubZmqContext, envVariables);

        // start SubZMQ server
        BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
        ZContext zmqSubContext = new ZContext();
        SubZMQ zmqSub = new SubZMQ(zmqSubContext, messageQueue, envVariables);
        Thread zmqSubThread = new Thread(zmqSub);
        zmqSubThread.start();

        // start SSE server
        try {
            int httpServerPort = Integer.parseInt(envVariables.get("SSE_SERVER_PORT"));
            HttpServer server = HttpServer.create(new InetSocketAddress(httpServerPort), 0);

            // Routes
            server.createContext("/", new ServerSSE.HelloWorld());
            server.createContext("/yo", new ServerSSE.SSE(messageQueue));

            server.setExecutor(null); // creates a default executor
            server.start();
            System.out.println("SSE Server is up and running on :8080");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // start GRPC server
        ServerGRPC serverGRPC = new ServerGRPC();
        try {
            if (dbConn != null) {
                serverGRPC.start(zmqPublisher, greeter, envVariables);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}

// How to load dependencies with .jar:
// - Project Structure -> Libraries -> "+" sign -> add jars
