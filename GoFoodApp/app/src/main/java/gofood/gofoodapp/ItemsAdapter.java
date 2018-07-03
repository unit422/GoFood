package gofood.gofoodapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;

/**
 * Created by akshay on 4/4/17.
 */

public class ItemsAdapter extends ArrayAdapter<ShoppingItem> {

    Context context;

    public ItemsAdapter(Context context, List<ShoppingItem> items){
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
                    R.layout.userorder_item, parent, false
            );
        }

        ShoppingItem currentItem = getItem(position);

        ImageView img = (ImageView) listItemView.findViewById(R.id.imageView2);

        Picasso.get().load(currentItem.getImage()).fit().centerCrop().into(img);

        TextView name = (TextView) listItemView.findViewById(R.id.item_title);
        name.setText(currentItem.getTitle());

        TextView quant = (TextView) listItemView.findViewById(R.id.item_quant);
        quant.setText(currentItem.getQuantity());

        TextView cost = (TextView) listItemView.findViewById(R.id.item_price);
        cost.setText(currentItem.getPrice());

        int q = Integer.parseInt(currentItem.getQuantity());
        NumberPicker numberPicker = (NumberPicker) listItemView.findViewById(R.id.numpick_quant);
        numberPicker.setMaxValue(q);
        numberPicker.setWrapSelectorWheel(true);


        return listItemView;
    }
}
//.resizeDimen(R.dimen.forImage, R.dimen.forImage)