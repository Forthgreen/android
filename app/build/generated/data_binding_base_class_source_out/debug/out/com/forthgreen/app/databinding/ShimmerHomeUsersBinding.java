// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.viewbinding.ViewBinding;
import com.forthgreen.app.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ShimmerHomeUsersBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ToggleButton btnFollow;

  @NonNull
  public final CircleImageView civUserImage;

  @NonNull
  public final Guideline guidelineEnd;

  @NonNull
  public final Guideline guidelineStart;

  @NonNull
  public final TextView tvUserBio;

  @NonNull
  public final TextView tvUserFullName;

  @NonNull
  public final TextView tvUserName;

  @NonNull
  public final View viewDivider;

  @NonNull
  public final View viewFollow;

  private ShimmerHomeUsersBinding(@NonNull ConstraintLayout rootView,
      @NonNull ToggleButton btnFollow, @NonNull CircleImageView civUserImage,
      @NonNull Guideline guidelineEnd, @NonNull Guideline guidelineStart,
      @NonNull TextView tvUserBio, @NonNull TextView tvUserFullName, @NonNull TextView tvUserName,
      @NonNull View viewDivider, @NonNull View viewFollow) {
    this.rootView = rootView;
    this.btnFollow = btnFollow;
    this.civUserImage = civUserImage;
    this.guidelineEnd = guidelineEnd;
    this.guidelineStart = guidelineStart;
    this.tvUserBio = tvUserBio;
    this.tvUserFullName = tvUserFullName;
    this.tvUserName = tvUserName;
    this.viewDivider = viewDivider;
    this.viewFollow = viewFollow;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ShimmerHomeUsersBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ShimmerHomeUsersBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.shimmer_home_users, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ShimmerHomeUsersBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnFollow;
      ToggleButton btnFollow = rootView.findViewById(id);
      if (btnFollow == null) {
        break missingId;
      }

      id = R.id.civUserImage;
      CircleImageView civUserImage = rootView.findViewById(id);
      if (civUserImage == null) {
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

      id = R.id.tvUserBio;
      TextView tvUserBio = rootView.findViewById(id);
      if (tvUserBio == null) {
        break missingId;
      }

      id = R.id.tvUserFullName;
      TextView tvUserFullName = rootView.findViewById(id);
      if (tvUserFullName == null) {
        break missingId;
      }

      id = R.id.tvUserName;
      TextView tvUserName = rootView.findViewById(id);
      if (tvUserName == null) {
        break missingId;
      }

      id = R.id.viewDivider;
      View viewDivider = rootView.findViewById(id);
      if (viewDivider == null) {
        break missingId;
      }

      id = R.id.viewFollow;
      View viewFollow = rootView.findViewById(id);
      if (viewFollow == null) {
        break missingId;
      }

      return new ShimmerHomeUsersBinding((ConstraintLayout) rootView, btnFollow, civUserImage,
          guidelineEnd, guidelineStart, tvUserBio, tvUserFullName, tvUserName, viewDivider,
          viewFollow);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
