package rs.in.raf1.web;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONObject;
import java.util.HashMap;
import rs.in.raf1.OnTaskDoneListener;
import rs.in.raf1.TermListActivity;
import rs.in.raf1.TermListFragment;

public class WebService extends AsyncTask<String, Void, JSONObject> {
    private Context mContext;
    private OnTaskDoneListener onTaskDoneListener;
    private String urlStr = "";
    HashMap<String, String> params;

    public WebService(Context context, String urlStr, HashMap<String, String> params,
                      OnTaskDoneListener onTaskDoneListener) {

        this.mContext = context;
        this.urlStr = urlStr;
        this.params = params;
        this.onTaskDoneListener = onTaskDoneListener;
    }

    @Override
    protected JSONObject doInBackground(String... args) {
        HashMap<String, String> map = new HashMap<>();
        JSON json = new JSON();
        JSONObject jObj = JSON.makeHttpRequest(urlStr, "POST", this.params);
        return jObj;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONObject obj) {
        super.onPostExecute(obj);

        if (onTaskDoneListener != null && obj != null) {
            onTaskDoneListener.onTaskDone(obj);
        } else {
            onTaskDoneListener.onError();
        }

    }
}
