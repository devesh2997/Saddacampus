package com.saddacampus.app.app.Adapter;

/**
 * Created by Devesh Anand on 08-06-2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.saddacampus.app.R;
import com.saddacampus.app.activity.OfferItems;
import com.saddacampus.app.activity.OffersRestaurants;
import com.saddacampus.app.activity.SubCategoryActivity;
import com.saddacampus.app.app.Config.Config;
import com.saddacampus.app.app.DataObjects.Category;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private static final String TAG = CategoryAdapter.class.getSimpleName();

    private ArrayList<Category> categoryArrayList;

    public CategoryAdapter(ArrayList<Category> categoryArrayList) {
        this.categoryArrayList =(categoryArrayList);
    }



    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();

        int layoutIdForListItem = R.layout.category_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        CategoryViewHolder viewHolder = new CategoryViewHolder(view);

        return viewHolder;
        // }
    }


    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }


    @Override
    public int getItemCount() {

        return categoryArrayList.size();
    }


    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // Will display the position in the list, ie 0 through getItemCount() - 1
        TextView categoryNameTextView;
        ImageView categoryImageView;
        ImageView categoryImageViewOffer;
        LinearLayout withoutOffer;
        LinearLayout withOffer;
        //TextView too;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            withOffer = (LinearLayout)itemView.findViewById(R.id.layout_image_with_offer);
            withoutOffer = (LinearLayout)itemView.findViewById(R.id.layout_image_without_offer);

            categoryImageView = (ImageView) itemView.findViewById(R.id.category_image_without_offer) ;
            categoryImageViewOffer = (ImageView) itemView.findViewById(R.id.category_image_with_offer) ;

            categoryNameTextView = (TextView) itemView.findViewById(R.id.tv_item_number);


            withOffer.setVisibility(View.GONE);
            //too = (TextView) itemView.findViewById(R.id.default_text_view);

        }

        @Override
        public void onClick(View view) {
            /*Toast.makeText(view.getContext(), "position = " + getPosition(), Toast.LENGTH_SHORT).show();*/
            Category category= categoryArrayList.get(getPosition());
            String isOffer = category.getIsOffer();
            if(isOffer.equals("1")){


                String tags = category.getTags();
                String res[] = tags.split(":");

                if(res[0].equals("restaurant"))
                {
                    Intent intent = new Intent(view.getContext(), OffersRestaurants.class);
                 //   intent.putExtra("tag",category.getTags());
                   // Toast.makeText(view.getContext(),"fucffk",Toast.LENGTH_SHORT).show();
                    intent.putExtra("category",category);
                    intent.putExtra("tableName",res[1]);
                    view.getContext().startActivity(intent);
                }
                else
                {
                   // Toast.makeText(view.getContext(),"ffk",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), OfferItems.class);
                    //intent.putExtra("tag",category.getTags());
                    intent.putExtra("tableName",res[1]);
                    intent.putExtra("category",category);
                    view.getContext().startActivity(intent);
                }
            }
            else {
               // Toast.makeText(view.getContext(), isOffer, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), SubCategoryActivity.class);
                intent.putExtra("category", category);
                view.getContext().startActivity(intent);
            }

        }

        void bind(int listIndex) {


            if(categoryArrayList.get(listIndex).getIsOffer().equals("1")){

                withoutOffer.setVisibility(View.GONE);
                withOffer.setVisibility(View.VISIBLE);
                String url = Config.URL_GET_IMAGE_ASSETS + "Categories/".concat(categoryArrayList.get(listIndex).getImageResourceId());
                categoryNameTextView.setText(categoryArrayList.get(listIndex).getCategoryName());
                Picasso.with(itemView.getContext()).load(url).placeholder(R.drawable.placeholder).fit().centerCrop().into(categoryImageViewOffer);
            }
            else if(categoryArrayList.get(listIndex).getIsOffer().equals("0")){

                withOffer.setVisibility(View.GONE);
                withoutOffer.setVisibility(View.VISIBLE);
                String url = Config.URL_GET_IMAGE_ASSETS + "Categories/".concat(categoryArrayList.get(listIndex).getImageResourceId());
                categoryNameTextView.setText(categoryArrayList.get(listIndex).getCategoryName());
                Picasso.with(itemView.getContext()).load(url).placeholder(R.drawable.placeholder).fit().centerCrop().into(categoryImageView);
            }

        }
    }
}

