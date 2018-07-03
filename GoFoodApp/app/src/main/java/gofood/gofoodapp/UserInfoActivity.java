package gofood.gofoodapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {

    private TextView Email,ID,Name,type;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUsersRef = mRootRef.child("users");
    DatabaseReference mUsersInfoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_reset_password);
        setContentView(R.layout.activity_userinfo);



        auth = FirebaseAuth.getInstance();

        String userUid = auth.getCurrentUser().getUid();
        mUsersInfoRef = mUsersRef.child(userUid);

        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        Email = (TextView) findViewById(R.id.user_email);
        ID = (TextView) findViewById(R.id.user_id);
        Name = (TextView) findViewById(R.id.user_Name);
        type = (TextView) findViewById(R.id.user_type);


        mUsersInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                   //Toast.makeText(UserInfoActivity.this, auth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
                    Email.setText(auth.getCurrentUser().getEmail());
                    ID.setText(dataSnapshot.child("ID").getValue().toString().trim());
                    Name.setText(dataSnapshot.child("Name").getValue().toString().trim());
                    String userType = dataSnapshot.child("userType").getValue().toString().trim();
                    if(userType.matches("Storehand")){
                        userType = userType + " - " + dataSnapshot.child("Store").getValue().toString().trim();
                    }
                    type.setText(userType);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        progressBar.setVisibility(View.GONE);
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