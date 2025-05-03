import java.io.*;
import java.net.*;

class FileTransferHandler {
    public static void sendFile(Socket socket, String filePath) throws IOException {
        File file = new File(filePath);
        byte[] buffer = new byte[4096];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        OutputStream os = socket.getOutputStream();

        int bytesRead;
        while ((bytesRead = bis.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        bis.close();
    }

    public static void receiveFile(Socket socket, String saveDir) throws IOException {
        byte[] buffer = new byte[4096];
        InputStream is = socket.getInputStream();
        File dir = new File(saveDir);
        if (!dir.exists()) dir.mkdirs();

        File outFile = new File(dir, "received_file");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));

        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }
        bos.flush();
        bos.close();
    }
}
