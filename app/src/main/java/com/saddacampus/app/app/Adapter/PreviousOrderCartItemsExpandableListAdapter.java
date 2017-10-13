package com.saddacampus.app.app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.saddacampus.app.R;
import com.saddacampus.app.app.DataObjects.CartItem;

import java.util.ArrayList;

/**
 * Created by Devesh Anand on 29-07-2017.
 */

public class PreviousOrderCartItemsExpandableListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = PreviousOrderCartItemsExpandableListAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<CartItem> cartItems;

    public PreviousOrderCartItemsExpandableListAdapter(Context context, ArrayList<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @Override
    public CartItem getChild(int groupPosition, int childPosition) {
        return this.cartItems.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final CartItem cartItem= getChild(groupPosition,childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.previous_order_cart_items_expandable_child_view, parent,false);
        }

        ImageView orderItemIsVeg = (ImageView)convertView.findViewById(R.id.order_item_is_veg);
        TextView orderItemName = (TextView)convertView.findViewById(R.id.order_item_name);
        TextView orderItemPrice = (TextView)convertView.findViewById(R.id.order_item_price);
        TextView orderItemQuantity = (TextView)convertView.findViewById(R.id.order_item_quantity);

        if(cartItem.getRestaurantItem().isVeg()){
            orderItemIsVeg.setImageResource(R.drawable.veg_icon);
        }else{
            orderItemIsVeg.setImageResource(R.drawable.nonvegicon);
        }

        orderItemName.setText(cartItem.getRestaurantItem().getName());
        orderItemPrice.setText(String.valueOf(cartItem.getRestaurantItem().getPrice()));
        orderItemQuantity.setText(" * ".concat(String.valueOf(cartItem.getItemQuantity())));

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return cartItems.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return "Order Items";
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.previous_order_cart_items_expandable_header, parent,false);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
