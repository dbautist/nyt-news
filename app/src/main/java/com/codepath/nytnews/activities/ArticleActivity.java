package com.codepath.nytnews.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.codepath.nytnews.R;
import com.codepath.nytnews.models.Article;
import com.codepath.nytnews.utils.AppConstants;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleActivity extends AppCompatActivity {
  @BindView(R.id.articleWebView)
  WebView articleWebView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_article);
    ButterKnife.bind(this);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    Article article = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.ARTICLE_EXTRA));
    if (article != null) {
      actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, article.newsDeskColorId)));
      loadArticle(article);
    } else {
      // TODO: Error
    }
  }

  private void loadArticle(Article article) {
    articleWebView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
      }
    });
    articleWebView.loadUrl(article.webUrl);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_article, menu);

    MenuItem item = menu.findItem(R.id.menu_item_share);
    ShareActionProvider miShare = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");

    // pass in the URL currently being used by the WebView
    shareIntent.putExtra(Intent.EXTRA_TEXT, articleWebView.getUrl());

    miShare.setShareIntent(shareIntent);
    return super.onCreateOptionsMenu(menu);
  }
}
