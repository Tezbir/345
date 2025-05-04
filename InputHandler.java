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
                if (message.startsWith("/sendfile")) {
                    String[] parts = message.split(" ", 2);
                    if (parts.length < 2) {
                        System.out.println("Usage: /sendfile filename");
                        continue;
                    }
                    sendFile(parts[1]);
                } else {
                    out.println(message);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private void sendFile(String string) {
		// TODO Auto-generated method stub
		    try {
		        File file = new File(filename);
		        if (!file.exists()) {
		            System.out.println("File not found.");
		            return;
		        }

		        long length = file.length();
		        out.println("/sendfile " + file.getName());

		        DataOutputStream dataOut = new DataOutputStream(client.getOutputStream());
		        dataOut.writeLong(length);

		        FileInputStream fis = new FileInputStream(file);
		        byte[] buffer = new byte[(int) length];
		        fis.read(buffer);
		        fis.close();

		        dataOut.write(buffer);
		        dataOut.flush();

		        System.out.println("File sent: " + filename);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}

	}
}
