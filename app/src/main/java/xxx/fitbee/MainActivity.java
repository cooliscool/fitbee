package xxx.fitbee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.gson.Gson;

import java.util.HashSet;


public class MainActivity extends AppCompatActivity {


    private static final int OAUTH_CODE = 112;
    private static final String TAG = "yyy : ";

    private String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signIn();

    }

    private void query(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://64.227.66.122:8282";

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "request error : " + error.toString());
                    }
                });

        queue.add(req);
    }



    private void signIn(){


        GoogleSignInOptions opts  = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Fitness.SCOPE_ACTIVITY_READ)
                .requestEmail()
                .requestId()
                .build();

        json = new Gson().toJson(opts);
        Log.i(TAG,"GoogleSignInOptions : opts :: "+  json);

        GoogleSignInClient sClient = GoogleSignIn.getClient(this,opts);

        Intent intent = sClient.getSignInIntent();
        startActivityForResult(intent,OAUTH_CODE);
    }

    private void lightConnect(){
        // See if connection to Google Fit API is working

        GoogleSignInOptionsExtension fitOptions  =  FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .build();

        GoogleSignIn.getAccountForExtension(this,fitOptions);

        try {
            Task<DataSet> task = Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA);

            DataSet response = Tasks.await(task);

            if (response != null) {
                Log.i(TAG, "lightConnect() : Task<DataSet> readDailyTotal success : " + response.toString());
            }

        }catch (Exception e ){
            Log.e(TAG, "lightConnect() : Task<DataSet> failed : " + e.toString());
        }


    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == OAUTH_CODE){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnSuccessListener(
                            new OnSuccessListener<GoogleSignInAccount>() {
                                @Override
                                public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                                    Log.i(TAG, "onActivityResult() : Task OAUTH : Sign in successful");
                                    query();
                                }
                            }
                    )
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "onActivityResult() : Task OAUTH : Sign in failed: "+ e.toString());
                                    Log.i(TAG, "onActivityResult() : Task OAUTH : Attempting Sign in again : ");

                                }
                            }
                    );

        }
    }

}
