package com.takealookonit.roottracer.backend;

import android.os.AsyncTask;
import android.util.Log;

import com.takealookonit.roottracer.LoginAuthActivity;
import com.takealookonit.roottracer.MapsActivity;

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

public class HttpAddPoint extends AsyncTask<String, Void, String> {

    private MapsActivity activity;

    public HttpAddPoint(MapsActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... args) {
        try {
            String email = args[0];
            String lat = args[1];
            String lot = args[2];
            String time = args[3];
            String route = args[4];
            URL url = new URL("https://sdu.000webhostapp.com/addPoints.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter =
                    new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") +
                    "&" + URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8") +
                    "&" + URLEncoder.encode("lot", "UTF-8") + "=" + URLEncoder.encode(lot, "UTF-8") +
                    "&" + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8") +
                    "&" + URLEncoder.encode("route", "UTF-8") + "=" + URLEncoder.encode(route, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            inputStream.close();
            httpURLConnection.disconnect();
            return "server - HttpAddPoint successful";
        } catch (Exception ex) {
            return "server - HttpAddPoint " + ex.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("mylog", result);
        if (result.equals("server - HttpAddPoint successful")) {
//            activity.signUpResponse(1);//TODO
        } else{}
//            activity.signUpResponse(0);//TODO
    }

}
