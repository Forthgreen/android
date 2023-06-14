// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import com.forthgreen.app.R;
import com.forthgreen.app.views.utils.SlidingConstraintLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class RowBrandsBinding implements ViewBinding {
  @NonNull
  private final SlidingConstraintLayout rootView;

  @NonNull
  public final CircleImageView civBrandImage;

  @NonNull
  public final TextView tvBrandName;

  private RowBrandsBinding(@NonNull SlidingConstraintLayout rootView,
      @NonNull CircleImageView civBrandImage, @NonNull TextView tvBrandName) {
    this.rootView = rootView;
    this.civBrandImage = civBrandImage;
    this.tvBrandName = tvBrandName;
  }

  @Override
  @NonNull
  public SlidingConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RowBrandsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RowBrandsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.row_brands, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RowBrandsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.civBrandImage;
      CircleImageView civBrandImage = rootView.findViewById(id);
      if (civBrandImage == null) {
        break missingId;
      }

      id = R.id.tvBrandName;
      TextView tvBrandName = rootView.findViewById(id);
      if (tvBrandName == null) {
        break missingId;
      }

      return new RowBrandsBinding((SlidingConstraintLayout) rootView, civBrandImage, tvBrandName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}