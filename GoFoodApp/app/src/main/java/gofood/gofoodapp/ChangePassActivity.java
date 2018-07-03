package gofood.gofoodapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ChangePassActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText currPass,newPass,confirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();



        currPass = (EditText) findViewById(R.id.CurrentPass_ed);
        newPass = (EditText) findViewById(R.id.NewPass_ed);
        confirmPass = (EditText) findViewById(R.id.ConfirmPass_ed);
    }
    public void onStart(){
        super.onStart();
    }
    public void onResume(){
        super.onResume();
    }
    public void onPause(){
        super.onPause();
    }
    public void onStop(){
        super.onStop();
    }
    public void onDestroy(){
        super.onDestroy();
    }
}
