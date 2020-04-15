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
import com.android.volley.toolbox.HurlStack;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashSet;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


public class MainActivity extends AppCompatActivity {

    private static final String HOST = "64.227.66.122";
    private static final int OAUTH_CODE = 112;
    private static final String TAG = "yyy : ";
    private static final String TRUSTED_CERT = "-----BEGIN CERTIFICATE-----\n" +
            "MIID3zCCAsegAwIBAgIUOZk1sAsmDzpVpWgDTxEHK+K637cwDQYJKoZIhvcNAQEL\n" +
            "BQAwfzELMAkGA1UEBhMCSU4xEzARBgNVBAgMClNvbWUtU3RhdGUxITAfBgNVBAoM\n" +
            "GEludGVybmV0IFdpZGdpdHMgUHR5IEx0ZDEWMBQGA1UEAwwNNjQuMjI3LjY2LjEy\n" +
            "MjEgMB4GCSqGSIb3DQEJARYRc2FzZWJvdEBnbWFpbC5jb20wHhcNMjAwNDEzMDky\n" +
            "NjUyWhcNMjEwNDEzMDkyNjUyWjB/MQswCQYDVQQGEwJJTjETMBEGA1UECAwKU29t\n" +
            "ZS1TdGF0ZTEhMB8GA1UECgwYSW50ZXJuZXQgV2lkZ2l0cyBQdHkgTHRkMRYwFAYD\n" +
            "VQQDDA02NC4yMjcuNjYuMTIyMSAwHgYJKoZIhvcNAQkBFhFzYXNlYm90QGdtYWls\n" +
            "LmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBALpBECNhZuZiP1qq\n" +
            "xHh4CGWtSCmC3rQJPmo+F2VUbhzON5y91mnPYGPidI42OgcSEjZwto1GhvgM/f4u\n" +
            "CFDlhTE2slY9IpmkQ6xKVOEovExOjtB3np3th7oNz9ZnEXFqoOVkz71OVs7nhsLa\n" +
            "EupoQKc93FUYWvE6p8S++XijPDDswLLZ0hcVU+cYzhz7y76G1/8dnYCTGPG3mfRB\n" +
            "bOqDgrf0Ij0kM8NQNq6quexxtvikGyMBSM6PeoduER82rNnnIygP2mdMcph5Sser\n" +
            "9mCSpsQ39R4mAMWkNGKWuwS/GD8JNbcYiLoOvBTw0C7ci1/GSLksViF8wJGirWmo\n" +
            "duOkp/kCAwEAAaNTMFEwHQYDVR0OBBYEFDRH7kix4wpNiZTHglDJas6MyQKnMB8G\n" +
            "A1UdIwQYMBaAFDRH7kix4wpNiZTHglDJas6MyQKnMA8GA1UdEwEB/wQFMAMBAf8w\n" +
            "DQYJKoZIhvcNAQELBQADggEBAGh3cTgFvMPvoYLYe4MNYr81Nl4qKTIRrrNiRWD5\n" +
            "t4jPRgumtmStAOkFhqSEIUxgbNZIrohHW4YLPqDgw/y/EkVddVUyNhYycVwVa288\n" +
            "Ww8WGwUla8C0urCTQK6VPCKRg6sP+4rx+TONJdvt0aPkgqiwbOSlI6e++ftOqI2Y\n" +
            "T6fyet9e0LD8uEPc5/38Af57F+KARsDuZn74SEU3hyFko1GvgnkmRw7EpQHFAxJU\n" +
            "mzsQSlL6Z0ugaNYE84LaGtJ8/dwSrcGv1wBjn07rFBPayJDoA4b2w9YB3+TfVAoJ\n" +
            "oI+ED0nlF//pTT7znSsaxKMYEUZlb9AtT6SMSsYpWr9C3eQ=\n" +
            "-----END CERTIFICATE-----\n";

    private String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signIn();

    }

    private void newQuery(){
        String url = "https://64.227.66.122:8787";

        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext(), getTrustedHttps());

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

        mRequestQueue.add(req);
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
                                    newQuery();
//                                    query();
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

    private HurlStack getTrustedHttps(){
        return new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(newSslSocketFactory());
                    httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };

    }

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                if (hostname.equals(HOST)){
                    return true;
                }
                return false;
            }
        };
    }

    private SSLSocketFactory newSslSocketFactory() {
        try {

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca;

            InputStream in = new ByteArrayInputStream(TRUSTED_CERT.getBytes(StandardCharsets.UTF_8));

            try {
                ca = cf.generateCertificate(in);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                in.close();
            }

            // Create a KeyStore containing our trusted CAs
            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Get the raw resource, which contains the keystore with
            // your trusted certificates (root and any intermediate certs)
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            SSLSocketFactory sf = context.getSocketFactory();
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }



}
