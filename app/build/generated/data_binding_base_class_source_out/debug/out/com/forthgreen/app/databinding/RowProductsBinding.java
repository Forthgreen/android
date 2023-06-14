// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public final class RowProductsBinding implements ViewBinding {
  @NonNull
  private final SlidingConstraintLayout rootView;

  @NonNull
  public final ImageView ivProductImage;

  @NonNull
  public final TextView tvBrandName;

  @NonNull
  public final TextView tvProductName;

  private RowProductsBinding(@NonNull SlidingConstraintLayout rootView,
      @NonNull ImageView ivProductImage, @NonNull TextView tvBrandName,
      @NonNull TextView tvProductName) {
    this.rootView = rootView;
    this.ivProductImage = ivProductImage;
    this.tvBrandName = tvBrandName;
    this.tvProductName = tvProductName;
  }

  @Override
  @NonNull
  public SlidingConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RowProductsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RowProductsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.row_products, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RowProductsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
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

      return new RowProductsBinding((SlidingConstraintLayout) rootView, ivProductImage, tvBrandName,
          tvProductName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
