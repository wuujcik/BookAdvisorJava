package com.wuujcik.bookadvisor;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;


public class BookAdapter extends ArrayAdapter<Book> {

    public static final String LOG_TAG = BookAdapter.class.getName();

    public BookAdapter(Activity context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Book currentBook = getItem(position);


        /** This part of adapter takes care of the author*/

        String[] currentAuthorArr = currentBook.getmAuthor();

        String currentAuthor = Arrays.toString(currentAuthorArr);

        // Find the TextView in the list_item.xml layout with the ID element_author
        TextView authorView = (TextView) listItemView.findViewById(R.id.element_author);
        // Display the author of the current book in that TextView
        authorView.setText(currentAuthor.substring(3, currentAuthor.length()-3));

        /** This part of adapter takes care of the title*/

        String currentTitle = currentBook.getmTitle();


        // Find the TextView in the list_item.xml layout with the ID element_title
        TextView titleView = (TextView) listItemView.findViewById(R.id.element_title);
        // Display the title of the current book in that TextView
        titleView.setText(currentTitle);


        /** This part of adapter takes care of the description*/

        String currentDescription = currentBook.getmDescription();


        // Find the TextView in the list_item.xml layout with the ID element_description
        TextView descriptionView = (TextView) listItemView.findViewById(R.id.element_description);
        // Display the description of the current book in that TextView
        descriptionView.setText(currentDescription);

        /** This part of adapter takes care of the price*/

        double currentPrice = currentBook.getmPrice();

        NumberFormat priceFormatter = NumberFormat.getCurrencyInstance();
        String priceToDisplay = priceFormatter.format(currentPrice);

        // Find the TextView in the list_item.xml layout with the ID element_price
        TextView priceView = (TextView) listItemView.findViewById(R.id.element_price);
        // Display the price of the current book in that TextView
        priceView.setText(priceToDisplay);

        //TODO image adapter
        /** This part of adapter takes care of the image*/

        return listItemView;}
}
