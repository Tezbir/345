import java.io.*;

public class InputHandler implements Runnable {
    private final PrintWriter out;
    private final Client client;

    public InputHandler(PrintWriter out, Client client) {
        this.out = out;
        this.client = client;
    }

    @Override
    public void run() {
        try (BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in))) {
            while (!client.isDone()) {
                String message = inReader.readLine();
                if (message.equalsIgnoreCase("/quit")) {
                    out.println(message);
                    client.shutdown();
                    break;
                } else {
                    out.println(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
