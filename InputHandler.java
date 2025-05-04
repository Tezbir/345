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
        try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
            while (!client.isDone()) {
                System.out.print("> ");
                String input = console.readLine();

                if (input == null) continue;

                if (input.startsWith("/sendfile")) {
                    String[] parts = input.split(" ", 2);
                    if (parts.length < 2) {
                        System.out.println("Usage: /sendfile <filepath>");
                        continue;
                    }
                    sendFile(parts[1]);
                } else if (input.equalsIgnoreCase("/quit")) {
                    client.shutdown();
                } else {
                    out.println(input);
                }
            }
        } catch (IOException e) {
            client.shutdown();
        }
    }

    private void sendFile(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                System.out.println("File not found.");
                return;
            }

            long length = file.length();
            out.println("/sendfile " + file.getName());

            DataOutputStream dataOut = new DataOutputStream(client.getOutputStream());
            dataOut.writeLong(length);

            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                dataOut.write(buffer, 0, read);
            }
            dataOut.flush();
            fis.close();

            System.out.println("File sent: " + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
