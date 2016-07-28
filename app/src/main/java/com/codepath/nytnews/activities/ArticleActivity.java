package com.codepath.nytnews.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    Article article = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.ARTICLE_EXTRA));
    if (article != null) {
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
}
