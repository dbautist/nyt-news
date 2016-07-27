package com.codepath.nytnews.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.codepath.nytnews.R;
import com.codepath.nytnews.adapters.ArticleArrayAdapter;
import com.codepath.nytnews.models.Article;
import com.codepath.nytnews.network.ArticleClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity {
  private static final String TAG = SearchActivity.class.getSimpleName();
  @BindView(R.id.searchButton)
  Button searchButton;
  @BindView(R.id.queryTextView)
  TextView queryTextView;
  @BindView(R.id.articleGridView)
  GridView articleGridView;

  @NonNull private ArticleClient mClient;
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
    mArticleList = new ArrayList<>();
    mAdapter = new ArticleArrayAdapter(this, mArticleList);
    articleGridView.setAdapter(mAdapter);
    mClient = new ArticleClient();

    getArticleList("");
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

  @OnClick(R.id.searchButton)
  public void search(View view) {
    String queryText = queryTextView.getText().toString();
    getArticleList(queryText);
  }
}
