import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket client;
    private final Server server; // <-- Add this
    private BufferedReader in;
    private PrintWriter out;
    private boolean done = false;
    private String nickname;

    public ConnectionHandler(Socket client, Server server) {
        this.client = client;
        this.server = server; // <-- Store the server reference
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out.println("Enter a nickname:");
            nickname = in.readLine();
            System.out.println(nickname + " connected");

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    shutdown();
                    break;
                }
                System.out.println(nickname + ": " + message);
                server.broadcast(nickname + ": " + message); // <-- Use broadcast
            }

        } catch (IOException e) {
            shutdown();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void shutdown() {
        done = true;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (!client.isClosed()) client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
