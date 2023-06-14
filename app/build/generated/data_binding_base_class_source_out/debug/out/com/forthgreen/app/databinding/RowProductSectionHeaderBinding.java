// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.viewbinding.ViewBinding;
import androidx.viewpager2.widget.ViewPager2;
import com.forthgreen.app.R;
import com.forthgreen.app.views.utils.SlidingConstraintLayout;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class RowProductSectionHeaderBinding implements ViewBinding {
  @NonNull
  private final SlidingConstraintLayout rootView;

  @NonNull
  public final SpringDotsIndicator circlePagerIndicatorShop;

  @NonNull
  public final Guideline guidelineEnd;

  @NonNull
  public final Guideline guidelineStart;

  @NonNull
  public final CircleImageView ivProductImageSectionHeader;

  @NonNull
  public final ConstraintLayout sectionHeaderConstraintLayout;

  @NonNull
  public final TextView tvProductNameSectionHeader;

  @NonNull
  public final TextView tvProductNameTop;

  @NonNull
  public final TextView tvProductPriceSectionHeader;

  @NonNull
  public final ViewPager2 viewPager;

  @NonNull
  public final View viewTopDivider;

  private RowProductSectionHeaderBinding(@NonNull SlidingConstraintLayout rootView,
      @NonNull SpringDotsIndicator circlePagerIndicatorShop, @NonNull Guideline guidelineEnd,
      @NonNull Guideline guidelineStart, @NonNull CircleImageView ivProductImageSectionHeader,
      @NonNull ConstraintLayout sectionHeaderConstraintLayout,
      @NonNull TextView tvProductNameSectionHeader, @NonNull TextView tvProductNameTop,
      @NonNull TextView tvProductPriceSectionHeader, @NonNull ViewPager2 viewPager,
      @NonNull View viewTopDivider) {
    this.rootView = rootView;
    this.circlePagerIndicatorShop = circlePagerIndicatorShop;
    this.guidelineEnd = guidelineEnd;
    this.guidelineStart = guidelineStart;
    this.ivProductImageSectionHeader = ivProductImageSectionHeader;
    this.sectionHeaderConstraintLayout = sectionHeaderConstraintLayout;
    this.tvProductNameSectionHeader = tvProductNameSectionHeader;
    this.tvProductNameTop = tvProductNameTop;
    this.tvProductPriceSectionHeader = tvProductPriceSectionHeader;
    this.viewPager = viewPager;
    this.viewTopDivider = viewTopDivider;
  }

  @Override
  @NonNull
  public SlidingConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RowProductSectionHeaderBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RowProductSectionHeaderBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.row_product_section_header, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RowProductSectionHeaderBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.circlePagerIndicatorShop;
      SpringDotsIndicator circlePagerIndicatorShop = rootView.findViewById(id);
      if (circlePagerIndicatorShop == null) {
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

      id = R.id.ivProductImageSectionHeader;
      CircleImageView ivProductImageSectionHeader = rootView.findViewById(id);
      if (ivProductImageSectionHeader == null) {
        break missingId;
      }

      id = R.id.sectionHeaderConstraintLayout;
      ConstraintLayout sectionHeaderConstraintLayout = rootView.findViewById(id);
      if (sectionHeaderConstraintLayout == null) {
        break missingId;
      }

      id = R.id.tvProductNameSectionHeader;
      TextView tvProductNameSectionHeader = rootView.findViewById(id);
      if (tvProductNameSectionHeader == null) {
        break missingId;
      }

      id = R.id.tvProductNameTop;
      TextView tvProductNameTop = rootView.findViewById(id);
      if (tvProductNameTop == null) {
        break missingId;
      }

      id = R.id.tvProductPriceSectionHeader;
      TextView tvProductPriceSectionHeader = rootView.findViewById(id);
      if (tvProductPriceSectionHeader == null) {
        break missingId;
      }

      id = R.id.viewPager;
      ViewPager2 viewPager = rootView.findViewById(id);
      if (viewPager == null) {
        break missingId;
      }

      id = R.id.viewTopDivider;
      View viewTopDivider = rootView.findViewById(id);
      if (viewTopDivider == null) {
        break missingId;
      }

      return new RowProductSectionHeaderBinding((SlidingConstraintLayout) rootView,
          circlePagerIndicatorShop, guidelineEnd, guidelineStart, ivProductImageSectionHeader,
          sectionHeaderConstraintLayout, tvProductNameSectionHeader, tvProductNameTop,
          tvProductPriceSectionHeader, viewPager, viewTopDivider);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}