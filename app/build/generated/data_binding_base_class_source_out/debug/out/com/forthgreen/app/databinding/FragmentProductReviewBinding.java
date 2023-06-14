// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Guideline;
import androidx.viewbinding.ViewBinding;
import com.forthgreen.app.R;
import com.forthgreen.app.views.utils.SlidingConstraintLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentProductReviewBinding implements ViewBinding {
  @NonNull
  private final SlidingConstraintLayout rootView;

  @NonNull
  public final Button btnSubmitReview;

  @NonNull
  public final TextInputEditText etReview;

  @NonNull
  public final TextInputEditText etTitle;

  @NonNull
  public final Guideline guidelineEnd;

  @NonNull
  public final Guideline guidelineStart;

  @NonNull
  public final RatingBar rbProductRating;

  @NonNull
  public final TextInputLayout tilReview;

  @NonNull
  public final TextInputLayout tilTitle;

  @NonNull
  public final View viewDivider;

  private FragmentProductReviewBinding(@NonNull SlidingConstraintLayout rootView,
      @NonNull Button btnSubmitReview, @NonNull TextInputEditText etReview,
      @NonNull TextInputEditText etTitle, @NonNull Guideline guidelineEnd,
      @NonNull Guideline guidelineStart, @NonNull RatingBar rbProductRating,
      @NonNull TextInputLayout tilReview, @NonNull TextInputLayout tilTitle,
      @NonNull View viewDivider) {
    this.rootView = rootView;
    this.btnSubmitReview = btnSubmitReview;
    this.etReview = etReview;
    this.etTitle = etTitle;
    this.guidelineEnd = guidelineEnd;
    this.guidelineStart = guidelineStart;
    this.rbProductRating = rbProductRating;
    this.tilReview = tilReview;
    this.tilTitle = tilTitle;
    this.viewDivider = viewDivider;
  }

  @Override
  @NonNull
  public SlidingConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentProductReviewBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentProductReviewBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_product_review, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentProductReviewBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnSubmitReview;
      Button btnSubmitReview = rootView.findViewById(id);
      if (btnSubmitReview == null) {
        break missingId;
      }

      id = R.id.etReview;
      TextInputEditText etReview = rootView.findViewById(id);
      if (etReview == null) {
        break missingId;
      }

      id = R.id.etTitle;
      TextInputEditText etTitle = rootView.findViewById(id);
      if (etTitle == null) {
        break missingId;
      }

      id = R.id.guidelineEnd;
      Guideline guidelineEnd = rootView.findViewById(id);
      if (guidelineEnd == null) {
        break missingId;
      }

      id = R.id.guidelineStart;
      Guideline guidelineStart = rootView.findViewById(id);
      if (guidelineStart == null) {
        break missingId;
      }

      id = R.id.rbProductRating;
      RatingBar rbProductRating = rootView.findViewById(id);
      if (rbProductRating == null) {
        break missingId;
      }

      id = R.id.tilReview;
      TextInputLayout tilReview = rootView.findViewById(id);
      if (tilReview == null) {
        break missingId;
      }

      id = R.id.tilTitle;
      TextInputLayout tilTitle = rootView.findViewById(id);
      if (tilTitle == null) {
        break missingId;
      }

      id = R.id.viewDivider;
      View viewDivider = rootView.findViewById(id);
      if (viewDivider == null) {
        break missingId;
      }

      return new FragmentProductReviewBinding((SlidingConstraintLayout) rootView, btnSubmitReview,
          etReview, etTitle, guidelineEnd, guidelineStart, rbProductRating, tilReview, tilTitle,
          viewDivider);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}