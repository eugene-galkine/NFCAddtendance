package com.eg.nfcaddtendance;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Eugene Galkine on 1/25/2017.
 */

public class PHPConnectorWorker extends AsyncTask<String,Void,String>
{
    MainActivity context;
    AlertDialog alertDialog;

    PHPConnectorWorker(MainActivity ctx)
    {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            String login_url = "http://" + context.getIP() + "/clockin.php";
            System.out.println(login_url);

            String fname = params[0];
            String lname = params[1];
            String id = params[2];
            String post_data = URLEncoder.encode("first_name","UTF-8")+"="+URLEncoder.encode(fname,"UTF-8")+"&"
                    +URLEncoder.encode("last_name","UTF-8")+"="+URLEncoder.encode(lname,"UTF-8")+"&"
                    +URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8");

            URL url = new URL(login_url);

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String result="";
            String line="";
            while((line = bufferedReader.readLine())!= null)
                result += line;

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return result;
        } catch (Exception e)
        {
            //e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onPostExecute(String result)
    {
        context.setMessage(result);
        //Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onProgressUpdate(Void... values)
    {
        super.onProgressUpdate(values);
    }
}
