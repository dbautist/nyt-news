package com.codepath.nytnews.utils;

import android.content.Context;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class PicassoViewHelper {
  private Picasso mPicasso;
    private RequestCreator requestCreator;

    public PicassoViewHelper(Context context, String imagePath, int placeHolderDrawable) {
      mPicasso = Picasso.with(context);
      requestCreator = mPicasso.load(imagePath).placeholder(placeHolderDrawable);
    }

    public void setRoundedCorner(int radius, int margin, RoundedCornersTransformation.CornerType cornerType) {
      RoundedCornersTransformation roundedCornersTransformation = null;
      if (cornerType == null) {
        roundedCornersTransformation = new RoundedCornersTransformation(radius, margin);
      } else {
        roundedCornersTransformation = new RoundedCornersTransformation(radius, margin, cornerType);
      }

      requestCreator = requestCreator.transform(roundedCornersTransformation);
    }

    public RequestCreator getRequestCreator() {
      return this.requestCreator;
    }
}
