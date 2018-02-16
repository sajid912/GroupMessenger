package edu.buffalo.cse.cse486586.groupmessenger1;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import static android.content.ContentValues.TAG;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity implements View.OnClickListener {

    private static final String REMOTE_PORT0 = "11108";
    private static final String REMOTE_PORT1 = "11112";
    private static final String REMOTE_PORT2 = "11116";
    private static final String REMOTE_PORT3 = "11120";
    private static final String REMOTE_PORT4 = "11124";
    private static final int SERVER_PORT = 10000;
    private EditText messageSpace;
    private MySharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);

        } catch (IOException e) {
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */
        Button sendButton = (Button) findViewById(R.id.button4);
        sendButton.setOnClickListener(this);

        messageSpace = (EditText) findViewById(R.id.editText1);

        sharedPreferences = new MySharedPreferences(getApplicationContext());
        sharedPreferences.clearPreferences();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button4) {
            String message = messageSpace.getText().toString();
            messageSpace.setText("");
            new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, message);

        }
    }

    private void writeServerMsgToFile(String filename, String message) {
        Uri uri = new Uri.Builder().authority("edu.buffalo.cse.cse486586.groupmessenger1.provider")
                .scheme("content").build();

        ContentValues contentValues = new ContentValues();
        contentValues.put("key", filename);
        contentValues.put("value", message);

        try {
            getContentResolver().insert(uri, contentValues);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }


    }

    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];
            String message;
            Log.d(TAG, "Server task");

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    InputStream inputStream = socket.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    message = bufferedReader.readLine();
                    publishProgress(message);

                    bufferedReader.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            //return null;
        }

        protected void onProgressUpdate(String... strings) {

            String message = strings[0].trim() + "\n";
            Log.d(TAG, "Message received:" + message);

            int currentSeqNum = sharedPreferences.getSequenceNumber();
            String fileName = Integer.toString(currentSeqNum);

            writeServerMsgToFile(fileName, message);
            sharedPreferences.setSequenceNumber(++currentSeqNum);

            return;
        }
    }

    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {

            String[] ports = new String[]{REMOTE_PORT0, REMOTE_PORT1, REMOTE_PORT2, REMOTE_PORT3, REMOTE_PORT4};

            for (String remotePort : ports) {
                try {
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));

                    String msgToSend = msgs[0];
                    Log.d(TAG, "Msg to send:" + msgToSend);

                    OutputStream outputStream = socket.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    bufferedWriter.write(msgToSend);

                    bufferedWriter.flush();
                    bufferedWriter.close();
                    socket.close();

                } catch (UnknownHostException e) {
                    Log.e(TAG, "ClientTask UnknownHostException");
                } catch (IOException e) {
                    Log.e(TAG, "ClientTask socket IOException");
                }
            }

            return null;
        }
    }

}
