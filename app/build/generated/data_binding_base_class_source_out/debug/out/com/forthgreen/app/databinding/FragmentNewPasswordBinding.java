// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import com.forthgreen.app.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentNewPasswordBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button btnSave;

  @NonNull
  public final EditText etConfirmPassword;

  @NonNull
  public final EditText etPassword;

  @NonNull
  public final TextView tvConfirmTag;

  @NonNull
  public final TextView tvNewPassTag;

  private FragmentNewPasswordBinding(@NonNull ConstraintLayout rootView, @NonNull Button btnSave,
      @NonNull EditText etConfirmPassword, @NonNull EditText etPassword,
      @NonNull TextView tvConfirmTag, @NonNull TextView tvNewPassTag) {
    this.rootView = rootView;
    this.btnSave = btnSave;
    this.etConfirmPassword = etConfirmPassword;
    this.etPassword = etPassword;
    this.tvConfirmTag = tvConfirmTag;
    this.tvNewPassTag = tvNewPassTag;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentNewPasswordBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentNewPasswordBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_new_password, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentNewPasswordBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnSave;
      Button btnSave = rootView.findViewById(id);
      if (btnSave == null) {
        break missingId;
      }

      id = R.id.etConfirmPassword;
      EditText etConfirmPassword = rootView.findViewById(id);
      if (etConfirmPassword == null) {
        break missingId;
      }

      id = R.id.etPassword;
      EditText etPassword = rootView.findViewById(id);
      if (etPassword == null) {
        break missingId;
      }

      id = R.id.tvConfirmTag;
      TextView tvConfirmTag = rootView.findViewById(id);
      if (tvConfirmTag == null) {
        break missingId;
      }

      id = R.id.tvNewPassTag;
      TextView tvNewPassTag = rootView.findViewById(id);
      if (tvNewPassTag == null) {
        break missingId;
      }

      return new FragmentNewPasswordBinding((ConstraintLayout) rootView, btnSave, etConfirmPassword,
          etPassword, tvConfirmTag, tvNewPassTag);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}