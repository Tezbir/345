import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {

        Socket socket = null;
        InputStreamReader isr = null;
        OutputStreamWriter osw = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        ServerSocket serverSocket = null;

        serverSocket = new ServerSocket(1234);

        while (true) {

            try {

                socket = ServerSocket.accept();

                isr = new InputStreamReader(socket.getInputStream());
                osw = new OutputStreamWriter(socket.getOutputStream());

                br = new BufferedReader(isr);
                bw = new BufferedWriter(osw);

                while (true) {

                    String msgFromClient = br.readLine();

                    System.out.println("Client: " + msgFromClient);

                    bw.write("MSG Recieved.");
                    bw.newLine();
                    bw.flush();

                    if (msgFromClient.equalsIgnoreCase("END"))
                        break;
                }
            socket.close();
            isr.close();
            osw.close();
            br.close();
            br.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

    

