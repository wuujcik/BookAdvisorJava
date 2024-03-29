package com.wuujcik.bookadvisor;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    // Adapter for the list of earthquakes
    private BookAdapter mAdapter;

    //result from search view
    private String mSearchResult;

    //constructors to build request URL
    private String BEGINNING_OF_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private String END_OF_URL = "&maxResults=30";

    //URL for books data from the API dataset
    private String BOOK_REQUEST_URL = BEGINNING_OF_URL + mSearchResult + END_OF_URL;


    //test for emptyView
    //private static final String BOOKS_REQUEST_URL = "";

    //Constant value for the books loader ID.
    private static final int BOOK_LOADER_ID = 1;

    //ListView for the list of books
    private ListView bookListView;
    //TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;
    //ProgressBar shows progress of loading data
    private ProgressBar mProgressBar;

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        Log.v(LOG_TAG, "TEST onCreateLoader");
        Log.v(LOG_TAG, BOOK_REQUEST_URL + " " + mSearchResult);
        // Create a new loader for the given URL
        return new BookLoader(MainActivity.this, BOOK_REQUEST_URL);

    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        Log.v(LOG_TAG, "TEST onLoadFinished");
        Log.v(LOG_TAG, BOOK_REQUEST_URL + " " + mSearchResult);
        //sets the ProgressBar to gone when data is loaded
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        //if there's no valid list of books, shows empty state text that informs of no books found
        mEmptyStateTextView.setText(R.string.no_books_found);

        // Clear the adapter of previous books data
        mAdapter.clear();

        // If there is a valid list of Books, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(LOG_TAG, "TEST onCreate");
        Log.v(LOG_TAG, BOOK_REQUEST_URL + " " + mSearchResult);

        //set on click listener for the button to handle search results
        Button buttonSearch = findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "TEST onClick button");
                Log.v(LOG_TAG, BOOK_REQUEST_URL + " " + mSearchResult);
//                mAdapter.clear();
                EditText viewSearch = findViewById(R.id.view_search);
                mSearchResult = viewSearch.getText().toString();
                Log.v(LOG_TAG, "TEST onClick button after mSearchResult is updated");
                Log.v(LOG_TAG, BOOK_REQUEST_URL + " " + mSearchResult);
                BOOK_REQUEST_URL = BEGINNING_OF_URL + mSearchResult + END_OF_URL;
                Log.v(LOG_TAG, "TEST onClick button after BOOK_REQUEST_URL is updated");
                Log.v(LOG_TAG, BOOK_REQUEST_URL + " " + mSearchResult);
                getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                loadResults();
            }
        });

        loadResults();

    }

    private boolean checkConnectionStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        Log.v(LOG_TAG, "TEST checkConnectionStatus");
        Log.v(LOG_TAG, BOOK_REQUEST_URL + " " + mSearchResult);
        return isConnected;
    }

    private void loadResults() {
        Log.v(LOG_TAG, "TEST loadResults - start");
        Log.v(LOG_TAG, BOOK_REQUEST_URL + " " + mSearchResult);

        if (mSearchResult == null) {
            //sets the ProgressBar to gone if no search provided yet
            mProgressBar = findViewById(R.id.progress_bar);
            mProgressBar.setVisibility(View.GONE);

            //inform user of search not being defined yet
            mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
            bookListView = findViewById(R.id.list);
            bookListView.setEmptyView(mEmptyStateTextView);
            mEmptyStateTextView.setText(R.string.no_search);
        } else {


            // Create a new adapter that takes an empty list of books as input
            mAdapter = new BookAdapter(this, new ArrayList<Book>());

            // Get a reference to the ListView, and attach the adapter to the listView.
            bookListView = findViewById(R.id.list);
            bookListView.setAdapter(mAdapter);

            //Check if there is internet connection
            boolean connectedToInternet = checkConnectionStatus();
            if (!connectedToInternet) {
                Log.v(LOG_TAG, "TEST loadResults - no internet");
                Log.v(LOG_TAG, BOOK_REQUEST_URL + " " + mSearchResult);

                //sets the ProgressBar to gone if no internet
                mProgressBar = findViewById(R.id.progress_bar);
                mProgressBar.setVisibility(View.GONE);

                //inform user of lack of internet connection
                mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
                bookListView.setEmptyView(mEmptyStateTextView);
                mEmptyStateTextView.setText(R.string.no_internet);
            } else {
                Log.v(LOG_TAG, "TEST loadResults - internet connected");
                Log.v(LOG_TAG, BOOK_REQUEST_URL + " " + mSearchResult);

                //When a list item is clicked, the website with more details about the book is opened
                bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                        Book currentBook = mAdapter.getItem(position);
                        Log.v(LOG_TAG, "TEST loadResults - on item click - website");
                        Log.v(LOG_TAG, BOOK_REQUEST_URL + " " + mSearchResult);

                        if (currentBook.getmWeb() != null) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(currentBook.getmWeb()));
                            startActivity(intent);
                        }

                    }
                });

                mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
                bookListView.setEmptyView(mEmptyStateTextView);

                // Get a reference to the LoaderManager, in order to interact with loaders.
                LoaderManager loaderManager = getLoaderManager();
                Log.v(LOG_TAG, "TEST under loaderManager command");
                Log.v(LOG_TAG, BOOK_REQUEST_URL + " " + mSearchResult);

                // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                // because this activity implements the LoaderCallbacks interface).
                loaderManager.initLoader(BOOK_LOADER_ID, null, this);
                Log.v(LOG_TAG, "TEST under initLoader" + " " + mSearchResult);
                Log.v(LOG_TAG, BOOK_REQUEST_URL);
            }
        }
    }
}
