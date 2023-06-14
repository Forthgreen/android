// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import com.forthgreen.app.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class RowCountryBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final LinearLayout llParent;

  @NonNull
  public final TextView tvCountryCode;

  @NonNull
  public final TextView tvCountryName;

  private RowCountryBinding(@NonNull LinearLayout rootView, @NonNull LinearLayout llParent,
      @NonNull TextView tvCountryCode, @NonNull TextView tvCountryName) {
    this.rootView = rootView;
    this.llParent = llParent;
    this.tvCountryCode = tvCountryCode;
    this.tvCountryName = tvCountryName;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RowCountryBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RowCountryBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.row_country, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RowCountryBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      LinearLayout llParent = (LinearLayout) rootView;

      id = R.id.tvCountryCode;
      TextView tvCountryCode = rootView.findViewById(id);
      if (tvCountryCode == null) {
        break missingId;
      }

      id = R.id.tvCountryName;
      TextView tvCountryName = rootView.findViewById(id);
      if (tvCountryName == null) {
        break missingId;
      }

      return new RowCountryBinding((LinearLayout) rootView, llParent, tvCountryCode, tvCountryName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}