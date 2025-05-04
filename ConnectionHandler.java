import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket client;
    private final Server server;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done = false;
    private String nickname;

    public ConnectionHandler(Socket client, Server server) {
        this.client = client;
        this.server = server; 
    }

    @Override
    public void run() {
        try {
        	  out = new PrintWriter(client.getOutputStream(), true);
              in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            nickname = in.readLine(); 
            System.out.println(nickname + " connected"); 

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    shutdown();
                    break;
                } else if (message.startsWith("/sendfile")) {
                    receiveAndBroadcastFile(message); // ADD THIS
                } else {
                    System.out.println(nickname + ": " + message);
                    server.broadcast(nickname + ": " + message);
                }
            }
        } catch (IOException e) {
            shutdown();
        }
    }
    private void receiveAndBroadcastFile(String command) throws IOException {
        String[] parts = command.split(" ", 2);
        if (parts.length < 2) return;

        String filename = parts[1];
        DataInputStream dataIn = new DataInputStream(client.getInputStream());
        long fileSize = dataIn.readLong();

        byte[] buffer = new byte[(int) fileSize];
        dataIn.readFully(buffer);

        System.out.println("Received file: " + filename + " from " + nickname);

        // Broadcast file
        for (ConnectionHandler ch : server.getConnections()) {
            if (ch != this) {
                ch.sendFile(filename, buffer);
            }
        }
    }

    public void sendFile(String filename, byte[] data) {
        try {
            PrintWriter pw = new PrintWriter(client.getOutputStream(), true);
            pw.println("/file " + filename + " " + data.length);

            DataOutputStream dataOut = new DataOutputStream(client.getOutputStream());
            dataOut.writeLong(data.length);
            dataOut.write(data);
            dataOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
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
