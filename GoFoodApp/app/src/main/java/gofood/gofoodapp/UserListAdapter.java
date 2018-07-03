package gofood.gofoodapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by dhoy__000 on 6/8/2018.
 */

public class UserListAdapter extends ArrayAdapter<UserListItem> {
    Context context;

    public UserListAdapter(Context context, List<UserListItem> items){
        super(context, 0, items);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    //R.layout.ordering_items, parent, false
                    R.layout.ordering_items, parent, false
            );
        }

        UserListItem currentItem = getItem(position);

        //ImageView img = (ImageView) listItemView.findViewById(R.id.imageView2);

        //Picasso.get().load("http://i.imgur.com/DvpvklR.png").fit().centerCrop().into(img);

        TextView name = (TextView) listItemView.findViewById(R.id.item_title);
        name.setText(currentItem.getName());

        TextView quant = (TextView) listItemView.findViewById(R.id.item_quant);
        quant.setText(currentItem.getID());

        TextView cost = (TextView) listItemView.findViewById(R.id.item_price);
        cost.setText(currentItem.getUserType());

        return listItemView;
    }
}
