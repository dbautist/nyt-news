package com.codepath.nytnews.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.codepath.nytnews.R;
import com.codepath.nytnews.adapters.ArticleArrayAdapter;
import com.codepath.nytnews.fragment.SettingsDialogFragment;
import com.codepath.nytnews.models.Article;
import com.codepath.nytnews.models.FilterSettings;
import com.codepath.nytnews.network.ArticleClient;
import com.codepath.nytnews.utils.AppConstants;
import com.codepath.nytnews.utils.EndlessScrollListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements SettingsDialogFragment.SettingsDialogListener {
  private static final String TAG = SearchActivity.class.getSimpleName();
  @BindView(R.id.articleGridView)
  GridView articleGridView;

  @NonNull
  private ArticleClient mClient;
  private ArticleArrayAdapter mAdapter;
  private ArrayList<Article> mArticleList;
  private FilterSettings mFilterSettings;
  private String mQueryString;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    ButterKnife.bind(this);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    initList();
  }

  private void initList() {
    mClient = new ArticleClient();
    mArticleList = new ArrayList<>();
    mAdapter = new ArticleArrayAdapter(this, mArticleList);
    articleGridView.setAdapter(mAdapter);
    articleGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Article article = mAdapter.getItem(position);
        if (article != null) {
          Intent intent = new Intent(SearchActivity.this, ArticleActivity.class);
          intent.putExtra(AppConstants.ARTICLE_EXTRA, Parcels.wrap(article));
          startActivity(intent);
        } else {
          // TODO: Error
        }
      }
    });
    articleGridView.setOnScrollListener(new EndlessScrollListener() {
      @Override
      public boolean onLoadMore(int page, int totalItemsCount) {
        Log.d(TAG, "======= onLoadMore: page=" + page + "; totalItemsCount=" + totalItemsCount);
        // Triggered only when new data needs to be appended to the list
        // Add whatever code is needed to append new items to your AdapterView
        customLoadMoreDataFromApi(page);
        // or customLoadMoreDataFromApi(totalItemsCount);
        return true; // ONLY if more data is actually being loaded; false otherwise.
      }
    });
    getArticleList(0, true);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_search, menu);

    MenuItem searchItem = menu.findItem(R.id.action_search);
    final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        mQueryString = query;

        // clear the list; it's a new search
        mArticleList.clear();
        mAdapter.notifyDataSetChanged();

        // perform query here
        getArticleList(0, true);

        // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
        // see https://code.google.com/p/android/issues/detail?id=24599
        searchView.clearFocus();

        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    });

    // Customize searchview text and hint colors
    int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
    EditText et = (EditText) searchView.findViewById(searchEditId);
    et.setTextColor(Color.WHITE);
    et.setHintTextColor(Color.WHITE);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      showSettingsDialog();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


  // Append more data into the adapter
  public void customLoadMoreDataFromApi(int offset) {
    // This method probably sends out a network request and appends new data items to your adapter.
    // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
    // Deserialize API response and then construct new objects to append to the adapter
    getArticleList(offset, false);
  }

  /**
   * @param page
   * @param shouldClear true if list needs to be cleared. This is the case when fetching for the first time.
   */
  private void getArticleList(int page, final boolean shouldClear) {
    Map<String, String> requestParams = getRequestParams();
    mClient.getArticleList(requestParams, page, new ArticleClient.ArticleCatalogListener() {
      @Override
      public void onRequestSuccess(List requestList) {
        Log.d(TAG, "--------- onRequestSuccess");
        if (shouldClear) {
          mArticleList.clear();
        }
        mArticleList.addAll(requestList);
        mAdapter.notifyDataSetChanged();
      }

      @Override
      public void onRequestFailure() {

      }
    });
  }

  private Map<String, String> getRequestParams() {
    Map<String, String> requestParams = new HashMap<>();
    if (mQueryString != null) {
      requestParams.put(AppConstants.QUERY_PARAM, mQueryString);
    }

    if (mFilterSettings != null) {
      if (mFilterSettings.beginDate != null) {
        requestParams.put(AppConstants.BEGIN_DATE_PARAM, mFilterSettings.beginDate);
      }
      requestParams.put(AppConstants.SORT_PARAM, mFilterSettings.sortOrder);

      if (mFilterSettings.newsDeskValues != null && mFilterSettings.newsDeskValues.size() > 0) {
        StringBuilder newsDeskValue = new StringBuilder();
        for (String val : mFilterSettings.newsDeskValues) {
          newsDeskValue.append("\"").append(val).append("\"");
          newsDeskValue.append(" ");
        }
        // Strip the last space
        String newsDeskStr = newsDeskValue.toString().trim();
        requestParams.put(AppConstants.FILTER_QUERY_PARAM, String.format(AppConstants.NEWS_DESK_VAL, newsDeskStr));
      }
    }

    return requestParams;
  }

  private void showSettingsDialog() {
    FragmentManager fm = getSupportFragmentManager();
    SettingsDialogFragment settingsDialogFragment = SettingsDialogFragment.newInstance(mFilterSettings);
    settingsDialogFragment.show(fm, "fragment_settings_name");

  }

  @Override
  public void onFinishSettingsDialog(FilterSettings filterSettings) {
    this.mFilterSettings = filterSettings;
    // Update the search result if there's a query string
    getArticleList(0, true);
  }
}
