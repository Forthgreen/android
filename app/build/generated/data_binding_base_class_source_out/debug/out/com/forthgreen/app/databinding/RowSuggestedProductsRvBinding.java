// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

public final class RowSuggestedProductsRvBinding implements ViewBinding {
  @NonNull
  private final SlidingConstraintLayout rootView;

  @NonNull
  public final CheckBox cbBookmark;

  @NonNull
  public final ImageView ivProductImage;

  @NonNull
  public final TextView tvBrandName;

  @NonNull
  public final TextView tvProductName;

  @NonNull
  public final TextView tvProductPrice;

  private RowSuggestedProductsRvBinding(@NonNull SlidingConstraintLayout rootView,
      @NonNull CheckBox cbBookmark, @NonNull ImageView ivProductImage,
      @NonNull TextView tvBrandName, @NonNull TextView tvProductName,
      @NonNull TextView tvProductPrice) {
    this.rootView = rootView;
    this.cbBookmark = cbBookmark;
    this.ivProductImage = ivProductImage;
    this.tvBrandName = tvBrandName;
    this.tvProductName = tvProductName;
    this.tvProductPrice = tvProductPrice;
  }

  @Override
  @NonNull
  public SlidingConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RowSuggestedProductsRvBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RowSuggestedProductsRvBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.row_suggested_products_rv, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RowSuggestedProductsRvBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cbBookmark;
      CheckBox cbBookmark = rootView.findViewById(id);
      if (cbBookmark == null) {
        break missingId;
      }

      id = R.id.ivProductImage;
      ImageView ivProductImage = rootView.findViewById(id);
      if (ivProductImage == null) {
        break missingId;
      }

      id = R.id.tvBrandName;
      TextView tvBrandName = rootView.findViewById(id);
      if (tvBrandName == null) {
        break missingId;
      }

      id = R.id.tvProductName;
      TextView tvProductName = rootView.findViewById(id);
      if (tvProductName == null) {
        break missingId;
      }

      id = R.id.tvProductPrice;
      TextView tvProductPrice = rootView.findViewById(id);
      if (tvProductPrice == null) {
        break missingId;
      }

      return new RowSuggestedProductsRvBinding((SlidingConstraintLayout) rootView, cbBookmark,
          ivProductImage, tvBrandName, tvProductName, tvProductPrice);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
