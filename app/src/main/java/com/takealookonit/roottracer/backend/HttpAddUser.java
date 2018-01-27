package com.takealookonit.roottracer.backend;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Makhanov Madiyar
 * Date: 1/27/18.
 */

public class HttpAddUser extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... args) {
        try {
            String email = args[0];
            String password = args[1];
            URL url = new URL("https://sdu.000webhostapp.com/addUser.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter =
                    new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") +
                    "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            inputStream.close();
            httpURLConnection.disconnect();
            return "server - HttpAddUser successful";
        } catch (Exception ex) {
            return "server - HttpAddUser " + ex.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("mylog", result);
    }

}
