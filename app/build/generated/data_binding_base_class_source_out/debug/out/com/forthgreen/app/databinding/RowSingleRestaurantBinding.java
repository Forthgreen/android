// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.viewbinding.ViewBinding;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.forthgreen.app.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class RowSingleRestaurantBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ShimmerFrameLayout flShimmer;

  @NonNull
  public final Group groupRestaurant;

  @NonNull
  public final ImageView ivCross;

  @NonNull
  public final ImageView ivRestaurantImage;

  @NonNull
  public final RatingBar rbRating;

  @NonNull
  public final TextView tvCuisineInfo;

  @NonNull
  public final TextView tvDistanceInfo;

  @NonNull
  public final TextView tvPrice;

  @NonNull
  public final TextView tvRatingCounts;

  @NonNull
  public final TextView tvRestaurantName;

  private RowSingleRestaurantBinding(@NonNull ConstraintLayout rootView,
      @NonNull ShimmerFrameLayout flShimmer, @NonNull Group groupRestaurant,
      @NonNull ImageView ivCross, @NonNull ImageView ivRestaurantImage, @NonNull RatingBar rbRating,
      @NonNull TextView tvCuisineInfo, @NonNull TextView tvDistanceInfo, @NonNull TextView tvPrice,
      @NonNull TextView tvRatingCounts, @NonNull TextView tvRestaurantName) {
    this.rootView = rootView;
    this.flShimmer = flShimmer;
    this.groupRestaurant = groupRestaurant;
    this.ivCross = ivCross;
    this.ivRestaurantImage = ivRestaurantImage;
    this.rbRating = rbRating;
    this.tvCuisineInfo = tvCuisineInfo;
    this.tvDistanceInfo = tvDistanceInfo;
    this.tvPrice = tvPrice;
    this.tvRatingCounts = tvRatingCounts;
    this.tvRestaurantName = tvRestaurantName;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RowSingleRestaurantBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RowSingleRestaurantBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.row_single_restaurant, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RowSingleRestaurantBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.flShimmer;
      ShimmerFrameLayout flShimmer = rootView.findViewById(id);
      if (flShimmer == null) {
        break missingId;
      }

      id = R.id.groupRestaurant;
      Group groupRestaurant = rootView.findViewById(id);
      if (groupRestaurant == null) {
        break missingId;
      }

      id = R.id.ivCross;
      ImageView ivCross = rootView.findViewById(id);
      if (ivCross == null) {
        break missingId;
      }

      id = R.id.ivRestaurantImage;
      ImageView ivRestaurantImage = rootView.findViewById(id);
      if (ivRestaurantImage == null) {
        break missingId;
      }

      id = R.id.rbRating;
      RatingBar rbRating = rootView.findViewById(id);
      if (rbRating == null) {
        break missingId;
      }

      id = R.id.tvCuisineInfo;
      TextView tvCuisineInfo = rootView.findViewById(id);
      if (tvCuisineInfo == null) {
        break missingId;
      }

      id = R.id.tvDistanceInfo;
      TextView tvDistanceInfo = rootView.findViewById(id);
      if (tvDistanceInfo == null) {
        break missingId;
      }

      id = R.id.tvPrice;
      TextView tvPrice = rootView.findViewById(id);
      if (tvPrice == null) {
        break missingId;
      }

      id = R.id.tvRatingCounts;
      TextView tvRatingCounts = rootView.findViewById(id);
      if (tvRatingCounts == null) {
        break missingId;
      }

      id = R.id.tvRestaurantName;
      TextView tvRestaurantName = rootView.findViewById(id);
      if (tvRestaurantName == null) {
        break missingId;
      }

      return new RowSingleRestaurantBinding((ConstraintLayout) rootView, flShimmer, groupRestaurant,
          ivCross, ivRestaurantImage, rbRating, tvCuisineInfo, tvDistanceInfo, tvPrice,
          tvRatingCounts, tvRestaurantName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
