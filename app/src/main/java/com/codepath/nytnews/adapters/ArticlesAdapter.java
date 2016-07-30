package com.codepath.nytnews.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Movie;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.nytnews.R;
import com.codepath.nytnews.databinding.ItemArticleBinding;
import com.codepath.nytnews.models.Article;
import com.codepath.nytnews.utils.PicassoViewHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder> {
  private static final String TAG = ArticlesAdapter.class.getSimpleName();
  private List<Article> mArticleList;
  private Context mContext;

  public ArticlesAdapter(Context context, List<Article> articleList) {
    this.mContext = context;
    this.mArticleList = articleList;
  }

  @Override
  public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_article, parent, false);
    ArticleViewHolder viewHolder =  new ArticleViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(ArticleViewHolder holder, int position) {
    Article article = mArticleList.get(position);
    holder.bindTo(article);

    if (article != null) {
      holder.imageView.setImageResource(R.drawable.new_york_time_placeholder);

      if ( !TextUtils.isEmpty(article.thumbNail)) {
        int defaultDpWidth = (int)mContext.getResources().getDimension(R.dimen.image_background_width);
        PicassoViewHelper picassoViewHelper = new PicassoViewHelper(mContext, article.thumbNail, R.drawable.loading_placeholder);
        picassoViewHelper.getRequestCreator()
            .resize(defaultDpWidth, 0)
            .into(holder.imageView);
      }

      if (!TextUtils.isEmpty(article.newsDesk)) {
        holder.newsDeskTextView.setVisibility(View.VISIBLE);
        holder.newsDeskTextView.setBackgroundColor(ContextCompat.getColor(mContext, article.newsDeskColorId));
      } else {
        holder.newsDeskTextView.setVisibility(View.GONE);
      }
    }
  }

  @Override
  public int getItemCount() {
    return mArticleList.size();
  }

  public static class ArticleViewHolder extends RecyclerView.ViewHolder {
    private ItemArticleBinding mBinding;

    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.headlineTextView)
    TextView headlineTextView;
    @BindView(R.id.newsDeskTextView)
    TextView newsDeskTextView;
    @BindView(R.id.snippetTextView)
    TextView snippetTextView;

    public ArticleViewHolder(View itemView) {
      super(itemView);
      this.mBinding = DataBindingUtil.bind(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void bindTo(Article article) {
      mBinding.setArticle(article);
      mBinding.executePendingBindings();
    }
  }
}
