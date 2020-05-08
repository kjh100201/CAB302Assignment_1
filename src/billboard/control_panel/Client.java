package billboard.control_panel;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", 8000);
        OutputStream outputStream = socket.getOutputStream();
        System.out.println("Connected to server");
        Thread.sleep(5000);

        InputStream inputStream = socket.getInputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        System.out.println(inputStream.read());

        //BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        outputStream.write(42);
        //bufferedOutputStream.flush();

        socket.close();
    }
}
