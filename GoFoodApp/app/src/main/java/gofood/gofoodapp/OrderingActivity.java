package gofood.gofoodapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
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
import java.util.HashMap;
import java.util.Map;


public class OrderingActivity extends AppCompatActivity {


    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mInventoryRef = mRootRef.child("inventory");
    DatabaseReference mOrdersRef = mRootRef.child("orders");
    DatabaseReference mItemsRef;
    private FirebaseAuth auth;
    ArrayList<String> table = new ArrayList<>();
    private FirebaseAuth.AuthStateListener authListener;
    ArrayList<ShoppingItem> Orders = new ArrayList<>();
    final ArrayList<Integer> tablepos = new ArrayList<>();

    ShoppingItem s;
    Map mOrder;
    ShoppingListAdapter adapter;
    String userUID;
    Map mOrderDetails;

    EditText searchbar;
    ProgressBar progressBar;
    FloatingActionButton floatingActionButton;
    ArrayList<ShoppingItem> shoppingItems;
    ListView listMenu;
    LinearLayout linlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering);
        Intent intent = getIntent();
        final String storeName = intent.getExtras().getString("storeName");
        setTitle(storeName);
        //Toast.makeText(OrderingActivity.this, storeName, Toast.LENGTH_LONG).show();

        linlay = (LinearLayout) findViewById(R.id.paymentDetails);
        linlay.setVisibility(View.GONE);

        searchbar = (EditText) findViewById(R.id.searchBar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(storeName);
        setSupportActionBar(toolbar);

        mItemsRef = mInventoryRef.child(storeName);
        mOrder = new HashMap();
        //Toast.makeText(OrderingActivity.this, storeName, Toast.LENGTH_LONG).show();
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        auth = FirebaseAuth.getInstance();
        listMenu = (ListView) findViewById(R.id.menu_items_list);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab2);
        floatingActionButton.setVisibility(View.GONE);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setImageResource(R.drawable.ic_check_black_24dp);
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.menuItems)));

        mItemsRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            //table = collectInventory((Map<String, Object>) dataSnapshot.getValue());
                            //Toast.makeText(OrderingActivity.this, "Store", Toast.LENGTH_SHORT).show();
                            shoppingItems = getAllItems(dataSnapshot);
                            //Toast.makeText(OrderingActivity.this, "Inventory", Toast.LENGTH_SHORT).show();
                            adapter = new ShoppingListAdapter(OrderingActivity.this, shoppingItems);

                            //Toast.makeText(OrderingActivity.this, "exist!", Toast.LENGTH_SHORT).show();
                            listMenu.setAdapter(adapter);
                        }else{
                            Toast.makeText(OrderingActivity.this, "Store Inventory does not exist!", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        /*
        adapter = new ArrayAdapter<String>(this,
                R.layout.ordering_items, R.id.item_title, table);
        listMenu.setAdapter(adapter);*/
        progressBar.setVisibility(View.GONE);

        listMenu.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                s = shoppingItems.get(position);
                Orders.contains(s);
                if (!Orders.contains(s)){
                    Orders.add(s);
                    listMenu.getChildAt(position).setBackgroundResource(R.color.scan);
                    Toast.makeText(OrderingActivity.this,s.getTitle()+" added", Toast.LENGTH_SHORT).show();
                }else{
                    listMenu.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                    Toast.makeText(OrderingActivity.this, s.getTitle()+" removed", Toast.LENGTH_SHORT).show();
                    Orders.remove(s);
                    /*
                    int i =0;
                    for(String string : Orders) {
                        if(string.matches(s)){
                            Toast.makeText(OrderingActivity.this, Orders.remove(i)+" removed", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        i++;
                    }*/

                }
            }

        });
        listMenu.setTextFilterEnabled(true);
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int textlength = charSequence.length();
                ArrayList<ShoppingItem> tempShoppingItems = new ArrayList<>();
                for(ShoppingItem x: shoppingItems){
                    if (textlength <= x.getTitle().length()) {
                        if (x.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            tempShoppingItems.add(x);
                        }
                    }
                }
                listMenu.setAdapter(new ShoppingListAdapter(getApplicationContext(), tempShoppingItems));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                userUID  = auth.getCurrentUser().getUid();
                mOrderDetails = new HashMap();
                for (int i=0;i<Orders.size();i++){
                    mOrderDetails.put("Name",Orders.get(i));
                    mOrder.put("item"+i,mOrderDetails);
                    mOrderDetails = new HashMap();
                }
                mOrdersRef.child(userUID).setValue(mOrder);*/
                if(!Orders.isEmpty()){
                    Intent intent = new Intent(OrderingActivity.this, UserOrderActivity.class);
                    intent.putExtra("UserOrderList",Orders );
                    intent.putExtra("storeName",storeName);
                    startActivity(intent);
                }else{
                    Toast.makeText(OrderingActivity.this, "Your Cart is Empty!", Toast.LENGTH_SHORT).show();
                }
                /*
                Toast.makeText(OrderingActivity.this, "4", Toast.LENGTH_LONG).show();
                recreate();*/
            }
        });

    }
    /*
    private ArrayList<String> collectInventory(Map<String,Object> users) {
        ArrayList<String> items = new ArrayList<>();

        for (Map.Entry<String, Object> entry : users.entrySet()){

            Map singleUser = (Map) entry.getValue();
            items.add((String) singleUser.get("Name"));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.ordering_items, R.id.item_title, items);

        listMenu.setAdapter(adapter);
        System.out.println(items.toString());
        return items;
    }*/

    public static ArrayList<ShoppingItem> getAllItems(DataSnapshot dataSnapshot){

        ArrayList<ShoppingItem> items  = new ArrayList<ShoppingItem>();

        for (DataSnapshot item : dataSnapshot.getChildren()) {

            items.add(new ShoppingItem(
                    item.child("itemID").getValue().toString(),
                    item.child("Name").getValue().toString(),
                    item.child("prodCost").getValue().toString(),
                    item.child("Quant").getValue().toString(),
                    item.child("Image").getValue().toString()
            ));

        }
        return items;
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
    public void signOut() {
        auth.signOut();
    }
}
