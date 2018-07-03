package gofood.gofoodapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StoreSelectStorehand extends AppCompatActivity {
    ListView listMenu;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef = mRootRef.child("condition");
    DatabaseReference mStoresRef = mRootRef.child("stores");
    DatabaseReference mUsersRef = mRootRef.child("users");
    private FirebaseAuth auth;
    ArrayList<String> table = new ArrayList<>();
    private FirebaseAuth.AuthStateListener authListener;

    StoreListAdapter adapter;
    ArrayList<StoreListItem> storeList = new ArrayList<>();

    EditText searchbar;
    ProgressBar progressBar;
    FloatingActionButton floatingActionButton;
    String storeHandUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_select_storehand);
        setTitle("Select your Store!");
        auth = FirebaseAuth.getInstance();
        storeHandUID = auth.getCurrentUser().getUid();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchbar = (EditText)findViewById(R.id.searchBar);
        searchbar.setVisibility(View.GONE);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        listMenu = (ListView) findViewById(R.id.menu_items_list);

        mStoresRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //table = collectStores((Map<String,Object>) dataSnapshot.getValue());
                        storeList = collectStoresNew(dataSnapshot);
                        adapter = new StoreListAdapter(StoreSelectStorehand.this, storeList);
                        listMenu.setAdapter(adapter);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        progressBar.setVisibility(View.GONE);
        /*


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.ongoingorder_item, R.id.item_title, table);
        listMenu.setAdapter(adapter);
        */

        //listMenu.setTextFilterEnabled(true);
        /*
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int textlength = charSequence.length();
                ArrayList<String> tempShoppingItems = new ArrayList<>();
                for(String x: table){
                    if (textlength <= x.length()) {
                        if (x.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            tempShoppingItems.add(x);
                        }
                    }
                }
                listMenu.setAdapter(new ArrayAdapter<String>(StoreSelectStorehand.this , R.layout.ongoingorder_item, R.id.item_title, tempShoppingItems));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Toast.makeText(StoreSelectStorehand.this, "Your Cart is Empty!", Toast.LENGTH_SHORT).show();
            }
        });*/

        listMenu.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final StoreListItem s = storeList.get(position);
                new AlertDialog.Builder(StoreSelectStorehand.this)
                        .setTitle("Finalize")
                        .setMessage("Is "+s.getName()+" Your Store?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                mUsersRef.child(storeHandUID).child("Store").setValue(s.getName());
                                mRootRef.child(s.getName()).child("storehands").child(storeHandUID).setValue("FALSE");
                                startActivity(new Intent(StoreSelectStorehand.this, UserMenuActivity.class));
                            }

                        }).create().show();
                /*
                ArrayList<ShoppingItem> tempShoppingItems = new ArrayList<>();
                Intent intent = new Intent(StoreSelectStorehand.this, OrderingActivity.class);
                intent.putExtra("storeName",s );
                startActivity(intent);
                finish();
                */
                //startActivity(new Intent(StoreActivity.this, OrderingActivity.class));
            }
        });

    }
    private ArrayList<String> collectStores(Map<String,Object> users) {
        ArrayList<String> stores = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            Map singleUser = (Map) entry.getValue();
            stores.add((String) singleUser.get("Name"));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.ongoingorder_item, R.id.item_title, stores);

        listMenu.setAdapter(adapter);
        System.out.println(stores.toString());
        return stores;
    }
    private ArrayList<StoreListItem> collectStoresNew(DataSnapshot dataSnapshot) {
        ArrayList<StoreListItem> stores = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (DataSnapshot item:dataSnapshot.getChildren()){
            stores.add(new StoreListItem(
                    item.child("Name").getValue().toString(),
                    item.child("Image").getValue().toString()
            ));
        }
        ;
        return stores;
    }

    public void signOut() {
        auth.signOut();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_drawer, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            //Intent intent1 = new Intent(this,MyActivity.class);
            //this.startActivity(intent1);
            //return true;
        }

        if(id == R.id.action_logout){
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /*
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
    */
}