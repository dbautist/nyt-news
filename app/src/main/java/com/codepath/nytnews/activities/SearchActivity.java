package com.codepath.nytnews.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.codepath.nytnews.R;
import com.codepath.nytnews.adapters.ArticlesAdapter;
import com.codepath.nytnews.fragment.SettingsDialogFragment;
import com.codepath.nytnews.models.Article;
import com.codepath.nytnews.models.FilterSettings;
import com.codepath.nytnews.network.ArticleClient;
import com.codepath.nytnews.utils.AppConstants;
import com.codepath.nytnews.utils.view.EndlessRecyclerViewScrollListener;
import com.codepath.nytnews.utils.view.ItemClickSupport;
import com.codepath.nytnews.utils.errors.ErrorHandler;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements SettingsDialogFragment.SettingsDialogListener {
  private static final String TAG = SearchActivity.class.getSimpleName();
  @BindView(R.id.articleRecyclerView)
  RecyclerView articleRecyclerView;

  @NonNull
  private ArticleClient mClient;
  private ArticlesAdapter mAdapter;
  private ArrayList<Article> mArticleList;
  private FilterSettings mFilterSettings;
  private String mQueryString;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    ButterKnife.bind(this);

    initList();
  }

  private void initList() {
    mClient = new ArticleClient();
    mArticleList = new ArrayList<>();
    mAdapter = new ArticlesAdapter(this, mArticleList);
    articleRecyclerView.setAdapter(mAdapter);

    StaggeredGridLayoutManager gridLayoutManager =
        new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
    articleRecyclerView.setLayoutManager(gridLayoutManager);

    ItemClickSupport.addTo(articleRecyclerView).setOnItemClickListener(
        new ItemClickSupport.OnItemClickListener() {
          @Override
          public void onItemClicked(RecyclerView recyclerView, int position, View v) {
            Article article = mArticleList.get(position);
            if (article != null) {
              Intent intent = new Intent(SearchActivity.this, ArticleActivity.class);
              intent.putExtra(AppConstants.ARTICLE_EXTRA, Parcels.wrap(article));
              startActivity(intent);
            } else {
              ErrorHandler.logAppError("Cannot start ArticleActivity -- Article is NULL");
              ErrorHandler.displayError(SearchActivity.this, AppConstants.DEFAULT_ERROR_MESSAGE);
            }
          }
        }
    );

    // http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView#implementing-with-recyclerview
    articleRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
      @Override
      public void onLoadMore(int page, int totalItemsCount) {
        // Triggered only when new data needs to be appended to the list
        // Add whatever code is needed to append new items to the bottom of the list
        customLoadMoreDataFromApi(page);
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
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      showSettingsDialog();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


  // Append more data into the adapter
  public void customLoadMoreDataFromApi(int offset) {
    getArticleList(offset, false);
  }

  /**
   * @param page
   * @param shouldClear true if list needs to be cleared. This is the case when a query string is entered
   *                    or additional filters are provided.
   */
  private void getArticleList(int page, final boolean shouldClear) {
    if (shouldClear) {
      int listSize = mArticleList.size();
      mArticleList.clear();
      mAdapter.notifyItemRangeRemoved(0, listSize);
    }

    Map<String, String> requestParams = getRequestParams();
    mClient.getArticleList(requestParams, page, new ArticleClient.ArticleCatalogListener() {
      @Override
      public void onRequestSuccess(List requestList) {
        Log.d(TAG, "--------- onRequestSuccess");

        int curSize = mAdapter.getItemCount();
        mArticleList.addAll(requestList);
        Log.d(TAG, "======= size:" + curSize + "; requestList size: " + requestList.size());
        mAdapter.notifyItemRangeInserted(curSize, requestList.size());

        if (shouldClear) {
          articleRecyclerView.scrollToPosition(0);
        }
      }

      @Override
      public void onRequestFailure(String errorMessage, boolean shouldDisplayToUser) {
        ErrorHandler.displayError(SearchActivity.this, errorMessage);
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
    // Fetch new results; addl filters have been provided
    getArticleList(0, true);
  }
}
