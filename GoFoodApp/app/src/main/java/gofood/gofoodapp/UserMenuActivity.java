package gofood.gofoodapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserMenuActivity extends AppCompatActivity {

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mOrdersRef = mRootRef.child("orders");
    //DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUsersRef = mRootRef.child("users");
    private Button btnOrders, btnLogout,btnSettings;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);
        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(UserMenuActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        getAllLayoutId();
        createListeners();

    }
    private void createListeners() {

        btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userUID  = auth.getCurrentUser().getUid();
                DatabaseReference mUserType = mUsersRef.child(userUID).child("userType");

                mUserType.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userType = dataSnapshot.getValue(String.class);
                        //Toast.makeText(UserMenuActivity.this, userType, Toast.LENGTH_SHORT).show();
                        if(userType.equals("User")){
                            mOrdersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                int i = 0;
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data: dataSnapshot.getChildren()){
                                        if (data.getKey().equals(userUID)){
                                            i++;
                                            break;
                                        }
                                    }
                                    if (i!=0){
                                        Intent intent = new Intent(UserMenuActivity.this, OngoingOrder.class);
                                        intent.putExtra("activityCheck","B" );
                                        startActivity(intent);
                                    }else{
                                        startActivity(new Intent(UserMenuActivity.this, StoreActivity.class));
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }else{
                            startActivity(new Intent(UserMenuActivity.this, OrdersActivity.class));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //startActivity(new Intent(UserMenuActivity.this, StoreActivity.class));
                //final String userUID  = auth.getCurrentUser().getUid();

            }
        }

        );
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserMenuActivity.this, SettingsActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
            }
        });
    }


    private void getAllLayoutId() {
        btnOrders = (Button) findViewById(R.id.user_order);
        btnLogout = (Button) findViewById(R.id.logout);
        btnSettings = (Button) findViewById(R.id.settings_button);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }
    /*
    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }*/
}
