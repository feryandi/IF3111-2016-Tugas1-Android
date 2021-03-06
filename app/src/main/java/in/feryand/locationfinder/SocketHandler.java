package in.feryand.locationfinder;

import android.util.Log;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * Created by feryandi on 09/03/2016.
 */
public class SocketHandler implements Runnable {
    private Socket socket;
    private String ServerIP = "167.205.34.132";
    private static final int ServerPort = 3111;

    @Override
    public void run() {
        try {
            socket = new Socket(ServerIP, ServerPort);
        } catch(Exception e) {
            // Die Bitch Die
        }
    }

    public void reconnect() {
        try {
            if (socket != null) socket.close();
            socket = new Socket(ServerIP, ServerPort);
        } catch(Exception e) {
            // Die Bitch Die
        }
    }

    public String Send(String s) {
        try {
            PrintWriter output = new PrintWriter(
                    new OutputStreamWriter(
                            socket.getOutputStream()));

            output.print(s + "\n");
            output.flush();
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            return input.readLine();
        } catch (UnknownHostException e) {
            System.out.print(e.toString());
        } catch (IOException e) {
            System.out.print(e.toString());
        } catch (Exception e) {
            System.out.print(e.toString());
        }
        return "{}";
    }
}
