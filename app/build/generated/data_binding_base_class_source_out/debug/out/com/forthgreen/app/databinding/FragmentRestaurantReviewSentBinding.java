// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import com.forthgreen.app.R;
import com.forthgreen.app.views.utils.SlidingConstraintLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentRestaurantReviewSentBinding implements ViewBinding {
  @NonNull
  private final SlidingConstraintLayout rootView;

  @NonNull
  public final Button btnBack;

  @NonNull
  public final ImageView ivReviewSent;

  @NonNull
  public final TextView tvReviewSent;

  private FragmentRestaurantReviewSentBinding(@NonNull SlidingConstraintLayout rootView,
      @NonNull Button btnBack, @NonNull ImageView ivReviewSent, @NonNull TextView tvReviewSent) {
    this.rootView = rootView;
    this.btnBack = btnBack;
    this.ivReviewSent = ivReviewSent;
    this.tvReviewSent = tvReviewSent;
  }

  @Override
  @NonNull
  public SlidingConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentRestaurantReviewSentBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentRestaurantReviewSentBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_restaurant_review_sent, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentRestaurantReviewSentBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnBack;
      Button btnBack = rootView.findViewById(id);
      if (btnBack == null) {
        break missingId;
      }

      id = R.id.ivReviewSent;
      ImageView ivReviewSent = rootView.findViewById(id);
      if (ivReviewSent == null) {
        break missingId;
      }

      id = R.id.tvReviewSent;
      TextView tvReviewSent = rootView.findViewById(id);
      if (tvReviewSent == null) {
        break missingId;
      }

      return new FragmentRestaurantReviewSentBinding((SlidingConstraintLayout) rootView, btnBack,
          ivReviewSent, tvReviewSent);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}