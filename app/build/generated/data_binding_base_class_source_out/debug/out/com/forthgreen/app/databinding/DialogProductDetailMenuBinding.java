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
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class DialogProductDetailMenuBinding implements ViewBinding {
  @NonNull
  private final SlidingConstraintLayout rootView;

  @NonNull
  public final TextView tvCancel;

  @NonNull
  public final TextView tvShareTo;

  private DialogProductDetailMenuBinding(@NonNull SlidingConstraintLayout rootView,
      @NonNull TextView tvCancel, @NonNull TextView tvShareTo) {
    this.rootView = rootView;
    this.tvCancel = tvCancel;
    this.tvShareTo = tvShareTo;
  }

  @Override
  @NonNull
  public SlidingConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DialogProductDetailMenuBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DialogProductDetailMenuBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.dialog_product_detail_menu, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DialogProductDetailMenuBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.tvCancel;
      TextView tvCancel = rootView.findViewById(id);
      if (tvCancel == null) {
        break missingId;
      }

      id = R.id.tvShareTo;
      TextView tvShareTo = rootView.findViewById(id);
      if (tvShareTo == null) {
        break missingId;
      }

      return new DialogProductDetailMenuBinding((SlidingConstraintLayout) rootView, tvCancel,
          tvShareTo);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}