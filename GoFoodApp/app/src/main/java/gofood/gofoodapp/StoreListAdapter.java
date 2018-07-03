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
 * Created by dhoy__000 on 6/9/2018.
 */

public class StoreListAdapter extends ArrayAdapter<StoreListItem> {

    Context context;

    public StoreListAdapter(Context context, List<StoreListItem> items){
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

        StoreListItem currentItem = getItem(position);

        ImageView img = (ImageView) listItemView.findViewById(R.id.imageView2);

        Picasso.get().load(currentItem.getImage()).fit().centerCrop().into(img);

        TextView name = (TextView) listItemView.findViewById(R.id.item_title);
        name.setText(currentItem.getName());


        return listItemView;
    }
}