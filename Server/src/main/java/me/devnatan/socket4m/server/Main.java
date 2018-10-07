package me.devnatan.socket4m.server;

public class Main {

    public static void main(String[] args){
        Proxy proxy = new Proxy();
        try {
            proxy.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
