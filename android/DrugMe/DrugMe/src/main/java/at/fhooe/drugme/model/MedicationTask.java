package at.fhooe.drugme.model;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import at.fhooe.drugme.R;

import static com.squareup.okhttp.internal.Util.readFully;

/**
 * Created by ch on 27.11.13.
 */
public class MedicationTask extends AsyncTask<String, Void, String> {
    String TAG="DragMe";

    MedicationTaskListener listener=null;
    @Override
    protected String doInBackground(String... urls) {
        OkHttpClient client = new OkHttpClient();

        String response = "";
        for (String url : urls) {

            HttpURLConnection connection =null;
            InputStream in = null;
            try {
                connection=client.open(new URL(url));
                // Read the response.

                in = connection.getInputStream();

                Reader r=new InputStreamReader(in);

                return readFully(r);
            }
            catch (MalformedURLException e) {
                Log.e(TAG, e.getMessage());
            }
            catch(Exception e){
                Log.e( TAG,e.getMessage());

            }
            finally {

                if (in != null) try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    public void setListener(MedicationTaskListener listener) {
        this.listener = listener;
    }


    @Override
    protected void onPostExecute(String result) {
        Gson gson = new Gson();
        MedicationPlan plan=gson.fromJson(result, MedicationPlan.class);
        if(listener!=null){
            listener.medicationTaskFinished(plan);
        }

    }

}
