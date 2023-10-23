import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;
import org.zeromq.ZContext;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.*;

public class ServerSSE {
    public static void main(String[] args) throws Exception {
        BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
        ZContext context = new ZContext();
        SubZMQ subscriber = new SubZMQ(context, messageQueue);
        Thread subZmqThread = new Thread(subscriber);
        subZmqThread.start();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Routes
        server.createContext("/", new HelloWorld());
        server.createContext("/yo", new SseStreamHandler(messageQueue));

        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server is up and running on :8080");
    }

    static class HelloWorld implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String jsonResponse = "{\"message\": \"service is up and running\"}";
            Headers headers = t.getResponseHeaders();
            headers.set("Content-Type", "application/json");

            byte[] responseBytes = jsonResponse.getBytes();
            int responseLength = responseBytes.length;

            t.sendResponseHeaders(200, responseLength);
            OutputStream os = t.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }

    static class SseStreamHandler implements HttpHandler {
        private final BlockingQueue<String> messageQueue;

        public SseStreamHandler(BlockingQueue<String> messageQueue) {
            this.messageQueue = messageQueue;
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            Headers header = t.getResponseHeaders();
            header.set("Content-Type", "text/event-stream");
            header.set("Cache-Control", "no-cache");
            header.set("Connection", "keep-alive");
            t.sendResponseHeaders(200, 0);

            OutputStream os = t.getResponseBody();

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String message = messageQueue.take(); // Wait for a message to arrive
                    message = "data: " + message + "\n\n";
                    os.write(message.getBytes());
                    os.flush();
                    System.out.println(message);
                    System.out.println("sent to SSE clients: " + message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    System.err.println("client disconnected: " + e.getMessage());
                    break;
                }
            }
        }
    }
}