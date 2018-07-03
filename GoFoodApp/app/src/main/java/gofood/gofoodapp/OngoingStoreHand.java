package gofood.gofoodapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.storage.StorageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OngoingStoreHand extends AppCompatActivity {

    View v;
    ListView listMenu;
    private FirebaseAuth auth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mOrdersRef = mRootRef.child("orders");
    DatabaseReference mUsersOrderRef;
    DatabaseReference mStoreOrdersRef;

    ArrayList<String> itemName = new ArrayList();
    ArrayList<String> itemQuant = new ArrayList();
    private FirebaseAuth.AuthStateListener authListener;
    private ItemsAdapter listAdapter;

    EditText searchbar;
    ProgressBar progressBar;
    FloatingActionButton floatingActionButton,floatingActionButton2;
    ArrayList<ShoppingItem> Orders = new ArrayList<>();
    ArrayList<ShoppingItem> shoppingItems;
    ShoppingListAdapter adapter;
    TextView totalview;
    float total = 0;
    String cashS,timeS,storeS;
    ShoppingItem s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_store_hand);
        UserListItem userListItem = (UserListItem) getIntent().getExtras().get("userList");
        //Toast.makeText(OngoingStoreHand.this,userListItem.getUserUID(), Toast.LENGTH_SHORT).show();

        //auth = FirebaseAuth.getInstance();
        final String userUID  = userListItem.getUserUID();
        mUsersOrderRef = mOrdersRef.child(userUID);
        setTitle("Customer's Order");


        //Toast.makeText(OngoingStoreHand.this, userUID, Toast.LENGTH_LONG).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        totalview = (TextView) findViewById(R.id.totalPriceCheckout);
        //totalview.setVisibility(View.GONE);
        //totalview.setText("Hey");

        searchbar = (EditText)findViewById(R.id.searchBar);
        searchbar.setClickable(false);
        searchbar.setEnabled(false);
        //searchbar.setText("UwU");
        //searchbar.setVisibility(View.GONE);

        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.fab2);
        floatingActionButton2.setImageResource(R.drawable.ic_info_outline_black_24dp);
        floatingActionButton2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.menuItems)));

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setBackgroundResource(R.color.logout);
        floatingActionButton.setImageResource(R.drawable.ic_check_black_24dp);
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.scan)));
        //floatingActionButton.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        listMenu = (ListView) findViewById(R.id.menu_items_list);

        mUsersOrderRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        if(dataSnapshot.exists()) {
                            for (DataSnapshot data: dataSnapshot.getChildren()){
                                //itemName.add(data.child("Name").getValue().toString().trim());
                                //itemQuant.add(data.child("quant").getValue().toString().trim());
                                shoppingItems = getUserItems(dataSnapshot);
                                adapter = new ShoppingListAdapter(OngoingStoreHand.this, shoppingItems);
                                listMenu.setAdapter(adapter);
                                //Toast.makeText(OngoingOrder.this, shoppingItems.get(0).getTitle(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                        Toast.makeText(OngoingStoreHand.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

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
                    Toast.makeText(OngoingStoreHand.this,s.getTitle()+" ready", Toast.LENGTH_SHORT).show();
                }else{
                    listMenu.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                    Toast.makeText(OngoingStoreHand.this, s.getTitle()+" removed", Toast.LENGTH_SHORT).show();
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

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(OngoingStoreHand.this)
                        .setTitle("Finish")
                        .setMessage("Did the Customer Pick up the Order?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                mStoreOrdersRef = mRootRef;
                                mStoreOrdersRef.child(storeS).child("orders").child(userUID).removeValue();
                                mUsersOrderRef.removeValue();
                                startActivity(new Intent(OngoingStoreHand.this, UserMenuActivity.class));
                            }
                        }).create().show();
            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new OngoingStoreHand.FireMissilesDialogFragment();
                newFragment.show(getFragmentManager(),"text");
            }
        });
    }
    @SuppressLint("ValidFragment")
    public class FireMissilesDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View v=inflater.inflate(R.layout.dialog_orderdetails, null);
            TextView cash = (TextView) v.findViewById(R.id.user_cash);
            TextView time = (TextView) v.findViewById(R.id.user_time);
            TextView change = (TextView) v.findViewById(R.id.user_change);
            TextView store = (TextView) v.findViewById(R.id.user_store);
            cashS = cashS.replace("₱","");
            float cashF = Float.parseFloat(cashS);
            String totaltext = totalview.getText().toString();
            totaltext = totaltext.replace("₱","");
            float changecash = cashF - Float.parseFloat(totaltext);
            change.setText("Customer Change: ₱"+String.format("%.02f",changecash));
            cash.setText("Customer Cash: ₱"+cashS);
            store.setText("Store Selected: "+storeS);
            String timeH[];
            timeH = timeS.split(":");
            int timeHint = Integer.parseInt(timeH[0]);
            if(timeH[1].length()<2){
                timeH[1] = "0"+timeH[1];
            }
            if(timeHint<=12){
                time.setText("Time to Get: "+timeS+" AM");
            }else{
                timeHint -= 12;
                time.setText("Time to Get: "+timeHint+":"+timeH[1]+" PM");
            }

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setTitle("Order Details");
            builder.setView(v)
                    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            OngoingStoreHand.FireMissilesDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }
    public ArrayList<ShoppingItem> getUserItems(DataSnapshot dataSnapshot){

        ArrayList<ShoppingItem> items  = new ArrayList<ShoppingItem>();
        float total=0;
        String ps;
        for (DataSnapshot item : dataSnapshot.getChildren()) {
            if(item.getKey().equals("CASH!")||item.getKey().equals("TIME!")|item.getKey().equals("STORE!")){
                //Toast.makeText(OngoingOrder.this, item.getValue().toString(), Toast.LENGTH_SHORT).show();
                if(item.getKey().equals("CASH!")){
                    cashS = item.getValue().toString();
                }else{
                    if(item.getKey().equals("TIME!")){
                        timeS = item.getValue().toString();
                    }else{
                        storeS = item.getValue().toString();
                    }
                }
            }else{
                int q=0;
                float p=0;
                q = Integer.parseInt(item.child("quant").getValue().toString());
                ps = item.child("price").getValue().toString();
                ps = ps.replace("₱","");
                p = Float.parseFloat(ps);
                p = q * p;
                total +=p ;
                ps = String.format("%.02f",p);
                ps = "₱"+ps;
                items.add(new ShoppingItem(
                        item.child("itemID").getValue().toString(),
                        item.child("Name").getValue().toString(),
                        ps,
                        item.child("quant").getValue().toString(),
                        item.child("Image").getValue().toString()
                ));
            }


        }
        ps = String.format("%.02f",total);
        ps = "₱"+ps;
        totalview.setText(ps);
        return items;
    }

    static class ViewHolder{
        ImageView picture;
        TextView item;
        TextView quant;
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

