package com.codepath.nytnews.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.nytnews.R;
import com.codepath.nytnews.databinding.ItemArticleBinding;
import com.codepath.nytnews.databinding.ItemArticlePreviewBinding;
import com.codepath.nytnews.models.Article;
import com.codepath.nytnews.utils.view.DynamicHeightImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {
  private static final String TAG = ArticlesAdapter.class.getSimpleName();
  public static final int TYPE_ARTICLE = 0;
  public static final int TYPE_ARTICLE_PREVIEW = 1;

  private List<Article> mArticleList;
  private Context mContext;

  public ArticlesAdapter(Context context, List<Article> articleList) {
    this.mContext = context;
    this.mArticleList = articleList;
  }

  @Override
  public int getItemViewType(int position) {
    Article article = mArticleList.get(position);
    if (article.isArticlePreview) {
      return TYPE_ARTICLE_PREVIEW;
    } else {
      return TYPE_ARTICLE;
    }
  }


  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    ViewHolder viewHolder;
    if (viewType == TYPE_ARTICLE_PREVIEW) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_article_preview, parent, false);
      viewHolder = new ArticlePreviewViewHolder(view);
    } else {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_article, parent, false);
      viewHolder = new ArticleViewHolder(view);
    }

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Article article = mArticleList.get(position);

    int type = getItemViewType(position);
    if (type == TYPE_ARTICLE_PREVIEW) {
      ArticlePreviewViewHolder articlePreviewViewHolder = (ArticlePreviewViewHolder) holder;
      articlePreviewViewHolder.bindTo(article);
    } else {
      final ArticleViewHolder articleViewHolder = (ArticleViewHolder) holder;
      articleViewHolder.bindTo(article);

      if (article != null) {
        if (!TextUtils.isEmpty(article.thumbNail)) {
          articleViewHolder.imageView.setVisibility(View.VISIBLE);
          articleViewHolder.imageView.setImageResource(0);

          articleViewHolder.imageView.setHeightRatio(1.0f);
          Glide.with(mContext).load(article.thumbNail).placeholder(R.drawable.loading_placeholder)
              .fitCenter().centerCrop()
              .into(articleViewHolder.imageView);
        } else {
          articleViewHolder.imageView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(article.newsDesk)) {
          articleViewHolder.newsDeskTextView.setVisibility(View.VISIBLE);
          articleViewHolder.newsDeskTextView.setBackgroundColor(ContextCompat.getColor(mContext, article.colorId));
        } else {
          articleViewHolder.newsDeskTextView.setVisibility(View.GONE);
        }
      }
    }
  }

  @Override
  public int getItemCount() {
    return mArticleList.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    protected ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }

  public static class ArticleViewHolder extends ViewHolder {
    private ItemArticleBinding mBinding;

    @BindView(R.id.imageView)
    DynamicHeightImageView imageView;
    @BindView(R.id.newsDeskTextView)
    TextView newsDeskTextView;

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

  public static class ArticlePreviewViewHolder extends ViewHolder {
    private ItemArticlePreviewBinding mBinding;

    public ArticlePreviewViewHolder(View itemView) {
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
