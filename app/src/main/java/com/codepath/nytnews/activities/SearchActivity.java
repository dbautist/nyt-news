package com.codepath.nytnews.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.codepath.nytnews.R;
import com.codepath.nytnews.adapters.ArticleArrayAdapter;
import com.codepath.nytnews.models.Article;
import com.codepath.nytnews.network.ArticleClient;
import com.codepath.nytnews.utils.AppConstants;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity {
  private static final String TAG = SearchActivity.class.getSimpleName();
  @BindView(R.id.articleGridView)
  GridView articleGridView;

  @NonNull
  private ArticleClient mClient;
  private ArticleArrayAdapter mAdapter;
  private ArrayList<Article> mArticleList;

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
  }

  private void getArticleList(@NonNull String query) {
    mClient.getArticleList(query, new ArticleClient.ArticleCatalogListener() {
      @Override
      public void onRequestSuccess(List requestList) {
        Log.d(TAG, "--------- onRequestSuccess");
        mArticleList.clear();
        mArticleList.addAll(requestList);
        mAdapter.notifyDataSetChanged();
      }

      @Override
      public void onRequestFailure() {

      }
    });
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
        // clear the list; it's a new search
        mArticleList.clear();
        mAdapter.notifyDataSetChanged();

        // perform query here
        getArticleList(query);

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

    // Expand the search view and request focus
    searchItem.expandActionView();
    searchView.requestFocus();

    // Customize searchview text and hint colors
    int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
    EditText et = (EditText) searchView.findViewById(searchEditId);
    et.setTextColor(Color.WHITE);
    et.setHintTextColor(Color.WHITE);

    return super.onCreateOptionsMenu(menu);
  }
}
