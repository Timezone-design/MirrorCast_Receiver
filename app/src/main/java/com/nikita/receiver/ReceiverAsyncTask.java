package com.nikita.receiver;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiverAsyncTask extends AsyncTask {

    private Context context;
    private static final String TAG = "NIKITA";
    private int port;
    private String address;
    private InetAddress addressInfo;
    private ServerSocket serverSocket;
    private InputStream inputStream;

    public ReceiverAsyncTask(Context context) {
        this.context = context;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setInetAddress(InetAddress addressInfo) {
        this.addressInfo = addressInfo;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            if(socket.isConnected()) {
                Toast.makeText(context, "Receiver Socket is connected", Toast.LENGTH_LONG).show();
                inputStream = socket.getInputStream();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object object) {
        super.onPostExecute(object);
        if (object != null) {

        }
    }
}
