// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import com.forthgreen.app.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class RowSingleBrandBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final CircleImageView ciBrandImage;

  @NonNull
  public final TextView tvBrandFollowers;

  @NonNull
  public final TextView tvBrandNameLabel;

  private RowSingleBrandBinding(@NonNull ConstraintLayout rootView,
      @NonNull CircleImageView ciBrandImage, @NonNull TextView tvBrandFollowers,
      @NonNull TextView tvBrandNameLabel) {
    this.rootView = rootView;
    this.ciBrandImage = ciBrandImage;
    this.tvBrandFollowers = tvBrandFollowers;
    this.tvBrandNameLabel = tvBrandNameLabel;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RowSingleBrandBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RowSingleBrandBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.row_single_brand, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RowSingleBrandBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ciBrandImage;
      CircleImageView ciBrandImage = rootView.findViewById(id);
      if (ciBrandImage == null) {
        break missingId;
      }

      id = R.id.tvBrandFollowers;
      TextView tvBrandFollowers = rootView.findViewById(id);
      if (tvBrandFollowers == null) {
        break missingId;
      }

      id = R.id.tvBrandNameLabel;
      TextView tvBrandNameLabel = rootView.findViewById(id);
      if (tvBrandNameLabel == null) {
        break missingId;
      }

      return new RowSingleBrandBinding((ConstraintLayout) rootView, ciBrandImage, tvBrandFollowers,
          tvBrandNameLabel);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
