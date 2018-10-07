package me.devnatan.socket4m;

import lombok.Data;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class TestServer {

    private static final Set<TestServerClient> clients = new HashSet<>();

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(4434);
            System.out.println("[S] Waiting for connections...");
            while(true) {
                Socket s = ss.accept();
                TestServerClient tsc = new TestServerClient(clients.size() + 1, s);
                clients.add(tsc);
                new Thread(tsc).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Data
    public static class TestServerClient implements Runnable {

        private final int id;
        private final Socket socket;

        public void run() {
            System.out.println("[C] Connected successfully.");
            try {
                DataOutputStream os = new DataOutputStream(socket.getOutputStream());
                os.writeUTF("Hi client =)");
                os.flush();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
