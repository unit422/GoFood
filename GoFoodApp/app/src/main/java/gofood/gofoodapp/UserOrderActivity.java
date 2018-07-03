package gofood.gofoodapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
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
public class UserOrderActivity extends AppCompatActivity {

    View v;

    private FirebaseAuth auth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mOrdersRef = mRootRef.child("orders");
    DatabaseReference mUsersOrderRef;
    private ItemsAdapter listAdapter;
    ArrayList<String> table = new ArrayList<>();
    private FirebaseAuth.AuthStateListener authListener;
    ArrayList<String> quantity = new ArrayList<>();
    ArrayList<String> item = new ArrayList<>();
    Map mquantity = new HashMap();
    String s;
    String userUID;

    ArrayList<ShoppingItem> shoppingItems = new ArrayList<ShoppingItem>();
    ArrayList<ShoppingItem> newQuant = new ArrayList<ShoppingItem>();
    EditText searchbar;
    ProgressBar progressBar;
    FloatingActionButton floatingActionButton;
    ListView listMenu;
    gofood.gofoodapp.ItemsAdapter adapter;
    LinearLayout linlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering);

        auth = FirebaseAuth.getInstance();
        userUID  = auth.getCurrentUser().getUid();
        //  Toast.makeText(UserOrderActivity.this, userUID, Toast.LENGTH_LONG).show();

        linlay = (LinearLayout) findViewById(R.id.paymentDetails);
        linlay.setVisibility(View.GONE);

        searchbar = (EditText)findViewById(R.id.searchBar);
        searchbar.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        //Intent intent = getIntent();
        shoppingItems = (ArrayList<ShoppingItem>) getIntent().getSerializableExtra("UserOrderList");
        final String storename = getIntent().getExtras().getString("storeName");
        //Toast.makeText(UserOrderActivity.this, shoppingItems.get(0).getTitle(), Toast.LENGTH_LONG).show();
        adapter = new ItemsAdapter(getApplicationContext(),shoppingItems);
        listMenu = (ListView) findViewById(R.id.menu_items_list);
        listMenu.setAdapter(adapter);



        progressBar.setVisibility(View.GONE);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab2);
        floatingActionButton.setVisibility(View.GONE);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setBackgroundResource(R.color.scan);
        floatingActionButton.setImageResource(R.drawable.ic_check_black_24dp);
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.scan)));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position=0;
                String test;
                int testnum;
                int err=0;
                EditText et;
                NumberPicker picker;
                for (ShoppingItem item: shoppingItems) {
                    v = listMenu.getChildAt(position);
                    listMenu.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                    //et = (EditText) v.findViewById(R.id.edit_quant);
                    picker = (NumberPicker) v.findViewById(R.id.numpick_quant);
                    //test = et.getText().toString().trim();
                    testnum = picker.getValue();
                    if(testnum<1){
                        v.setBackgroundResource(R.color.logout);
                        //Toast.makeText(UserOrderActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        err++;
                    }else{
                        //Toast.makeText(UserOrderActivity.this, test, Toast.LENGTH_SHORT).show();
                        newQuant.add(item);
                        newQuant.get(position).setQuantity(String.valueOf(testnum));
                    }
                    position++;
                }
                if(err>0){
                    Toast.makeText(UserOrderActivity.this, "Error:Please Input Valid Quantity", Toast.LENGTH_SHORT).show();
                    newQuant.clear();
                }else{/*
                    new AlertDialog.Builder(UserOrderActivity.this)
                            .setTitle("Cancel")
                            .setMessage("Do you want to finalize your selections?")
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0, int arg1) {
                                    //mOrdersRef.child(userUID).setValue(newQuant);

                                    for (ShoppingItem item: newQuant) {
                                        Toast.makeText(UserOrderActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                                    }
                                    Intent intent = new Intent(UserOrderActivity.this, OngoingOrder.class);
                                    startActivity(intent);

                                    Map mOrderDetails = new HashMap();
                                    int position=0;
                                    for (ShoppingItem item: newQuant) {
                                        mOrderDetails.put("Name",item.getTitle());
                                        mOrderDetails.put("quant",item.getQuantity());
                                        mOrderDetails.put("itemID",item.getProductID());
                                        mOrderDetails.put("price",item.getPrice());
                                        mquantity.put("item"+position,mOrderDetails);
                                        mOrderDetails = new HashMap();
                                        position++;
                                    }
                                    mOrdersRef.child(userUID).setValue(mquantity);
                                    Intent intent = new Intent(UserOrderActivity.this, OrderInfoActivity.class);
                                    startActivity(intent);
                                }

                            }).create().show();*/
                    Intent intent = new Intent(UserOrderActivity.this, OrderInfoActivity.class);
                    intent.putExtra("UserOrderList",newQuant );
                    intent.putExtra("storeName",storename);
                    startActivity(intent);
                }
            }
        });
    }

    static class ViewHolder{
        ImageView picture;
        TextView item;
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel")
                .setMessage("Do you want to cancel your selections?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        //mUsersOrderRef.removeValue();
                        UserOrderActivity.super.onBackPressed();
                    }
                }).create().show();
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

//shoppingItems = intent.getExtras().getParcelableArray("UserOrderList");
        /*
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(UserOrderActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        auth = FirebaseAuth.getInstance();
        userUID  = auth.getCurrentUser().getUid();
        mUsersOrderRef = mOrdersRef.child(userUID);
        setTitle("Your Order Quantities");*/
        /*
        mUsersOrderRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            table = collectOrderUser((Map<String, Object>) dataSnapshot.getValue());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });*/
        /*
        listMenu = (ListView) findViewById(R.id.menu_items_list);
        adapter = new ArrayAdapter<String>(this,
                R.layout.userorder_item, R.id.item_title , table);
        listMenu.setAdapter(adapter);
        listAdapter = new ItemsAdapter(UserOrderActivity.this, table);
        listMenu = (ListView) findViewById(R.id.menu_items_list);
        listMenu.setAdapter(listAdapter);

        listMenu.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                s = table.get(position).trim();
                Toast.makeText(UserOrderActivity.this, s, Toast.LENGTH_SHORT).show();

            }

        });*/

        /*
    private ArrayList<String> collectOrderUser(Map<String,Object> users) {
        ArrayList<String> items = new ArrayList<>();
        int i =0;
        for (Map.Entry<String, Object> entry : users.entrySet()){
            Map singleUser = (Map) entry.getValue();
            items.add((String) singleUser.get("Name"));
            i++;

        }
        adapter = new ArrayAdapter<String>(this,
                R.layout.userorder_item, R.id.item_title, items);
        listMenu.setAdapter(adapter);
        System.out.println(items.toString());
        return items;
    }*/
