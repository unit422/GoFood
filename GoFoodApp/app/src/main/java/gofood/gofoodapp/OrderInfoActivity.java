package gofood.gofoodapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderInfoActivity extends AppCompatActivity {
    TimePicker tm;
    EditText cash;
    String userUID,time,cashS;
    //Calendar rightnow;
    Button confirm;
    Date currentTime;
    ArrayList<ShoppingItem> newQuant = new ArrayList<ShoppingItem>();
    Map mquantity = new HashMap();

    int h,m;
    TextView totalview;
    private FirebaseAuth auth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mOrdersRef = mRootRef.child("orders");
    DatabaseReference mStoreOrdersRef;
    DatabaseReference mUsersOrderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        auth = FirebaseAuth.getInstance();
        userUID  = auth.getCurrentUser().getUid();
        mUsersOrderRef = mOrdersRef.child(userUID);

        tm = (TimePicker) findViewById(R.id.time_get);
        cash = (EditText) findViewById(R.id.user_cash);
        confirm = (Button) findViewById(R.id.confirm_button);
        totalview = (TextView) findViewById(R.id.totalPriceCheckout);
        cash.setText("0.00");
        //totalview.setText("Hey");

        newQuant = (ArrayList<ShoppingItem>) getIntent().getSerializableExtra("UserOrderList");
        final String storename = getIntent().getExtras().getString("storeName");
        final float total = getTotal(newQuant);

        confirm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Calendar rightNow = Calendar.getInstance();
                final Calendar timep = Calendar.getInstance();
                timep.set(Calendar.HOUR_OF_DAY,tm.getHour());
                timep.set(Calendar.MINUTE,tm.getMinute());
                cashS = cash.getText().toString().trim();
                cashS = cashS.replace("₱","");
                float cashF = Float.parseFloat(cashS);
                //time = String.valueOf(tm.getHour())+":"+String.valueOf(tm.getMinute());
                //Toast.makeText(OrderInfoActivity.this, rightNow.getTime().toString(), Toast.LENGTH_LONG).show();
                //Toast.makeText(OrderInfoActivity.this, timep.getTime().toString(), Toast.LENGTH_LONG).show();
                if(timep.getTimeInMillis() > rightNow.getTimeInMillis()+600000&!cashS.matches("")&cashF>=total){
                    //Toast.makeText(OrderInfoActivity.this, rightNow.getTime().toString(), Toast.LENGTH_LONG).show();

                    new AlertDialog.Builder(OrderInfoActivity.this)
                            .setTitle("Cancel")
                            .setMessage("Do you want to finalize your selections?")
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0, int arg1) {


                                    Map mOrderDetails = new HashMap();
                                    int position=0;
                                    for (ShoppingItem item: newQuant) {
                                        mOrderDetails.put("Name",item.getTitle());
                                        mOrderDetails.put("quant",item.getQuantity());
                                        mOrderDetails.put("itemID",item.getProductID());
                                        mOrderDetails.put("price",item.getPrice());
                                        mOrderDetails.put("Image",item.getImage());
                                        mquantity.put("item"+position,mOrderDetails);
                                        mOrderDetails = new HashMap();
                                        position++;
                                    }
                                    String a,b;
                                    a = String.valueOf(timep.get(Calendar.HOUR_OF_DAY));
                                    b = String.valueOf(timep.get(Calendar.MINUTE));
                                    mOrdersRef.child(userUID).setValue(mquantity);
                                    mUsersOrderRef.child("TIME!").setValue(a+":"+b);
                                    mUsersOrderRef.child("CASH!").setValue("₱"+cashS);
                                    mUsersOrderRef.child("STORE!").setValue(storename);
                                    mStoreOrdersRef = mRootRef;
                                    mStoreOrdersRef.child(storename).child("orders").child(userUID).setValue(userUID);
                                    Intent intent = new Intent(OrderInfoActivity.this, OngoingOrder.class);
                                    startActivity(intent);
                                }

                            }).create().show();
                }else{
                    if(cashF<total){
                        Toast.makeText(OrderInfoActivity.this, "Cash Entered Should Be Higher Than Total Cost!", Toast.LENGTH_LONG).show();
                    }else{
                        if(timep.getTimeInMillis() > rightNow.getTimeInMillis()){
                            Toast.makeText(OrderInfoActivity.this, "Time Selected Has To Be At least 10 Minutes After Current Time!", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(OrderInfoActivity.this, "Time Selected Cannot be Before Or On Current Time!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }
    public float getTotal(ArrayList<ShoppingItem> itemlist){

        float total=0;
        int position = 0;
        String ps;
        while(position<itemlist.size()){
            int q=0;
            float p=0;
            q = Integer.parseInt(itemlist.get(position).getQuantity());
            ps = itemlist.get(position).getPrice();
            ps = ps.replace("₱","");
            p = Float.parseFloat(ps);
            p = q * p;
            total +=p ;

            position++;
        }

        ps = String.format("%.02f",total);
        ps = "₱"+ps;
        totalview.setText(ps);
        return total;
    }

}
