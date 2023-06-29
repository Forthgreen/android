// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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

public final class FragmentAccountBinding implements ViewBinding {
  @NonNull
  private final SlidingConstraintLayout rootView;

  @NonNull
  public final Button btnUpdate;

  @NonNull
  public final TextInputEditText etEmail;

  @NonNull
  public final TextInputEditText etFull;

  @NonNull
  public final TextInputEditText etPass;

  @NonNull
  public final Guideline guidelineEnd;

  @NonNull
  public final Guideline guidelineStart;

  @NonNull
  public final TextInputLayout tilEmail;

  @NonNull
  public final TextInputLayout tilFull;

  @NonNull
  public final TextInputLayout tilPass;

  @NonNull
  public final TextView tvChangePassword;

  private FragmentAccountBinding(@NonNull SlidingConstraintLayout rootView,
      @NonNull Button btnUpdate, @NonNull TextInputEditText etEmail,
      @NonNull TextInputEditText etFull, @NonNull TextInputEditText etPass,
      @NonNull Guideline guidelineEnd, @NonNull Guideline guidelineStart,
      @NonNull TextInputLayout tilEmail, @NonNull TextInputLayout tilFull,
      @NonNull TextInputLayout tilPass, @NonNull TextView tvChangePassword) {
    this.rootView = rootView;
    this.btnUpdate = btnUpdate;
    this.etEmail = etEmail;
    this.etFull = etFull;
    this.etPass = etPass;
    this.guidelineEnd = guidelineEnd;
    this.guidelineStart = guidelineStart;
    this.tilEmail = tilEmail;
    this.tilFull = tilFull;
    this.tilPass = tilPass;
    this.tvChangePassword = tvChangePassword;
  }

  @Override
  @NonNull
  public SlidingConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentAccountBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentAccountBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_account, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentAccountBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnUpdate;
      Button btnUpdate = rootView.findViewById(id);
      if (btnUpdate == null) {
        break missingId;
      }

      id = R.id.etEmail;
      TextInputEditText etEmail = rootView.findViewById(id);
      if (etEmail == null) {
        break missingId;
      }

      id = R.id.etFull;
      TextInputEditText etFull = rootView.findViewById(id);
      if (etFull == null) {
        break missingId;
      }

      id = R.id.etPass;
      TextInputEditText etPass = rootView.findViewById(id);
      if (etPass == null) {
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

      id = R.id.tilEmail;
      TextInputLayout tilEmail = rootView.findViewById(id);
      if (tilEmail == null) {
        break missingId;
      }

      id = R.id.tilFull;
      TextInputLayout tilFull = rootView.findViewById(id);
      if (tilFull == null) {
        break missingId;
      }

      id = R.id.tilPass;
      TextInputLayout tilPass = rootView.findViewById(id);
      if (tilPass == null) {
        break missingId;
      }

      id = R.id.tvChangePassword;
      TextView tvChangePassword = rootView.findViewById(id);
      if (tvChangePassword == null) {
        break missingId;
      }

      return new FragmentAccountBinding((SlidingConstraintLayout) rootView, btnUpdate, etEmail,
          etFull, etPass, guidelineEnd, guidelineStart, tilEmail, tilFull, tilPass,
          tvChangePassword);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}