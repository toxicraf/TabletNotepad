package rs.in.raf1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rs.in.raf1.database.TermBaseHelper;
import rs.in.raf1.web.WebService;

import static rs.in.raf1.web.ParseJSON.jsonParse;

public class TermListActivity extends SingleFragmentActivity
        implements TermListFragment.Callbacks, TermFragment.Callbacks {

    ProgressDialog pDialog;
    public static String sFilter = "";
    Term term;
    List<Term> termsListWeb = new ArrayList<>();
    List<Term> termListDb = new ArrayList<>();


    public TermBaseHelper db;


    @Override
    protected Fragment createFragment() {
        return new TermListFragment();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        pDialog = new ProgressDialog(this);

        db = new TermBaseHelper(this);

        if (db.ifExists(db.getReadableDatabase()) == false) {
            if (!isOnline()) {
                Toast.makeText(getApplicationContext(), "Cannot download terms, you are not connected to Internet...", Toast.LENGTH_LONG).show();
            } else {
                pDialog.setMessage("Loading glossary. Please wait... When completed program will restart");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();

                readWebTerms();


            }
        }



   }

    private void readWebTerms() {
       WebService task = new WebService(getApplicationContext(),
               "http://www.raf1.in.rs/terms/get_all_terms.php", null, new OnTaskDoneListener() {

           @Override
           public void onTaskDone(JSONObject jObj) {
               try {
                   List<Term> termsListWeb = jsonParse(jObj);
                   TermBaseHelper db = new TermBaseHelper(getApplicationContext());
                   db.populate(termsListWeb);
                   pDialog.dismiss();
                   finish();
                   Intent i = getIntent();
                   startActivity(i);

               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }

           @Override
           public void onError() {

           }
       });

       task.execute();
   }

    @Override
    public void onTermSelected(Term term) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = TermPagerActivity.newIntent(this, term.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = TermFragment.newInstance(term.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

   public void onTermUpdated(Term term) {
        updateListFragment(sFilter);
    }

    public void onTermDeleted(UUID termId) {
        updateListFragment(sFilter);
    }

    private void updateListFragment(String filter) {
        TermListFragment listFragment = (TermListFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);
        listFragment.updateUI(filter);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

}
