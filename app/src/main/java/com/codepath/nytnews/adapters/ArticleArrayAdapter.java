package com.codepath.nytnews.adapters;

import android.content.Context;
import android.graphics.Movie;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.nytnews.R;
import com.codepath.nytnews.models.Article;
import com.codepath.nytnews.utils.PicassoViewHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleArrayAdapter extends ArrayAdapter<Article> {
  private static final String TAG = ArticleArrayAdapter.class.getSimpleName();

  public ArticleArrayAdapter(Context context, ArrayList<Article> articleList) {
    super(context, 0, articleList);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ArticleViewHolder viewHolder = null;

    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_article, parent, false);
      viewHolder = new ArticleViewHolder(convertView);
      convertView.setTag(viewHolder);
    }

    viewHolder = (ArticleViewHolder) convertView.getTag();
    Article article = getItem(position);
    if (article != null) {
      viewHolder.headlineTextView.setText(article.headline);
      if ( !TextUtils.isEmpty(article.thumbNail)) {
        Log.d(TAG, "====== thumbNail: " + article.thumbNail);
        int defaultDpWidth = (int)getContext().getResources().getDimension(R.dimen.image_background_width);
        PicassoViewHelper picassoViewHelper = new PicassoViewHelper(getContext(), article.thumbNail, R.drawable.poster_image_placeholder);
        picassoViewHelper.getRequestCreator()
            .into(viewHolder.imageView);
      }
    }

    return convertView;
  }

  public static class ArticleViewHolder {
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.headlineTextView)
    TextView headlineTextView;

    public ArticleViewHolder(View view) {
      ButterKnife.bind(this, view);
    }
  }
}
