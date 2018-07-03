package gofood.gofoodapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrdersActivity extends AppCompatActivity {
    ListView listMenu;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef = mRootRef.child("condition");
    DatabaseReference mStoresRef = mRootRef.child("Chicos").child("orders");
    DatabaseReference mStoreHandsRef;
    DatabaseReference mUsersRef = mRootRef.child("users");
    DatabaseReference mOrderUsersRef;
    private FirebaseAuth auth;
    ArrayList<String> table = new ArrayList<>();
    private FirebaseAuth.AuthStateListener authListener;
    EditText searchbar;
    FloatingActionButton floater;
    ProgressBar prog;
    ArrayList<UserListItem> userlist = new ArrayList<>();
    UserListAdapter adapter;

    TextView notauth;

    String nameS,idS,userType,userUID;
    String store;
    String storeHandUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        storeHandUID = auth.getCurrentUser().getUid();
        mUsersRef.child(storeHandUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                store = dataSnapshot.child("Store").getValue().toString();
                mStoresRef = mRootRef.child(store).child("orders");
                mStoreHandsRef = mRootRef.child(store).child("storehands");
                mStoreHandsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(storeHandUID).getValue().toString().equals("TRUE")){
                            setContentView(R.layout.activity_orders);
                            setTitle("Store Customers");
                            searchbar = (EditText) findViewById(R.id.searchBar);
                            searchbar.setVisibility(View.GONE);
                            floater = (FloatingActionButton) findViewById(R.id.fab);
                            floater.setVisibility(View.GONE);
                            prog = (ProgressBar) findViewById(R.id.ProgressBar);
                            listMenu = (ListView) findViewById(R.id.menu_items_list);

                            mStoresRef.addValueEventListener(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                for (DataSnapshot data:dataSnapshot.getChildren()){
                                                    mOrderUsersRef = mUsersRef.child(data.getValue().toString().trim());
                                                    mOrderUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            userlist = collectOrders(dataSnapshot);
                                                            adapter = new UserListAdapter(OrdersActivity.this, userlist);
                                                            listMenu.setAdapter(adapter);
                                                        }
                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            Toast.makeText(OrdersActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }else{
                                                Toast.makeText(OrdersActivity.this, "Currently No Orders!", Toast.LENGTH_LONG).show();
                                            }

                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            //handle databaseError
                                        }
                                    });
                            prog.setVisibility(View.GONE);
                            listMenu.setOnItemClickListener(new AdapterView.OnItemClickListener()
                            {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        final int position, long id) {
                                    UserListItem user = userlist.get(position);
                                    Intent intent = new Intent(OrdersActivity.this, OngoingStoreHand.class);
                                    intent.putExtra("userList",user);
                                    startActivity(intent);
                                }

                            });
                        }else{
                            //Toast.makeText(OrdersActivity.this, "Not Yet Authenticated", Toast.LENGTH_SHORT).show();
                            setContentView(R.layout.not_authenticated);
                            notauth = (TextView) findViewById(R.id.storeName);
                            notauth.setText(store);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(OrdersActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OrdersActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }
    private ArrayList<UserListItem> collectOrders(DataSnapshot dataSnapshot) {
        ArrayList<UserListItem> orders = new ArrayList<UserListItem>();
        nameS = dataSnapshot.child("Name").getValue().toString().trim();
        userType = dataSnapshot.child("userType").getValue().toString().trim();
        idS = dataSnapshot.child("ID").getValue().toString().trim();
        userUID = mOrderUsersRef.getKey();
        orders.add(new UserListItem(nameS,idS,userType,userUID));
        return orders;
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
