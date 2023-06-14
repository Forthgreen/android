// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.viewbinding.ViewBinding;
import com.forthgreen.app.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ShimmerMyProductsRvBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final CircleImageView ciBrandImage;

  @NonNull
  public final Group groupBrand;

  @NonNull
  public final Group groupProduct;

  @NonNull
  public final ImageView ivProduct;

  @NonNull
  public final TextView tvBrand;

  @NonNull
  public final TextView tvBrandName;

  @NonNull
  public final TextView tvPrice;

  @NonNull
  public final TextView tvProductName;

  private ShimmerMyProductsRvBinding(@NonNull ConstraintLayout rootView,
      @NonNull CircleImageView ciBrandImage, @NonNull Group groupBrand, @NonNull Group groupProduct,
      @NonNull ImageView ivProduct, @NonNull TextView tvBrand, @NonNull TextView tvBrandName,
      @NonNull TextView tvPrice, @NonNull TextView tvProductName) {
    this.rootView = rootView;
    this.ciBrandImage = ciBrandImage;
    this.groupBrand = groupBrand;
    this.groupProduct = groupProduct;
    this.ivProduct = ivProduct;
    this.tvBrand = tvBrand;
    this.tvBrandName = tvBrandName;
    this.tvPrice = tvPrice;
    this.tvProductName = tvProductName;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ShimmerMyProductsRvBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ShimmerMyProductsRvBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.shimmer_my_products_rv, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ShimmerMyProductsRvBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ciBrandImage;
      CircleImageView ciBrandImage = rootView.findViewById(id);
      if (ciBrandImage == null) {
        break missingId;
      }

      id = R.id.groupBrand;
      Group groupBrand = rootView.findViewById(id);
      if (groupBrand == null) {
        break missingId;
      }

      id = R.id.groupProduct;
      Group groupProduct = rootView.findViewById(id);
      if (groupProduct == null) {
        break missingId;
      }

      id = R.id.ivProduct;
      ImageView ivProduct = rootView.findViewById(id);
      if (ivProduct == null) {
        break missingId;
      }

      id = R.id.tvBrand;
      TextView tvBrand = rootView.findViewById(id);
      if (tvBrand == null) {
        break missingId;
      }

      id = R.id.tvBrandName;
      TextView tvBrandName = rootView.findViewById(id);
      if (tvBrandName == null) {
        break missingId;
      }

      id = R.id.tvPrice;
      TextView tvPrice = rootView.findViewById(id);
      if (tvPrice == null) {
        break missingId;
      }

      id = R.id.tvProductName;
      TextView tvProductName = rootView.findViewById(id);
      if (tvProductName == null) {
        break missingId;
      }

      return new ShimmerMyProductsRvBinding((ConstraintLayout) rootView, ciBrandImage, groupBrand,
          groupProduct, ivProduct, tvBrand, tvBrandName, tvPrice, tvProductName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
