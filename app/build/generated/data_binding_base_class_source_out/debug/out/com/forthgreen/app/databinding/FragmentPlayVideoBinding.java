// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Guideline;
import androidx.viewbinding.ViewBinding;
import com.forthgreen.app.R;
import com.forthgreen.app.views.utils.SlidingConstraintLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentPlayVideoBinding implements ViewBinding {
  @NonNull
  private final SlidingConstraintLayout rootView;

  @NonNull
  public final Guideline guidelineEnd;

  @NonNull
  public final Guideline guidelineStart;

  @NonNull
  public final ImageView ivCloseVideo;

  @NonNull
  public final VideoView vvPlayer;

  private FragmentPlayVideoBinding(@NonNull SlidingConstraintLayout rootView,
      @NonNull Guideline guidelineEnd, @NonNull Guideline guidelineStart,
      @NonNull ImageView ivCloseVideo, @NonNull VideoView vvPlayer) {
    this.rootView = rootView;
    this.guidelineEnd = guidelineEnd;
    this.guidelineStart = guidelineStart;
    this.ivCloseVideo = ivCloseVideo;
    this.vvPlayer = vvPlayer;
  }

  @Override
  @NonNull
  public SlidingConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentPlayVideoBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentPlayVideoBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_play_video, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentPlayVideoBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
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

      id = R.id.ivCloseVideo;
      ImageView ivCloseVideo = rootView.findViewById(id);
      if (ivCloseVideo == null) {
        break missingId;
      }

      id = R.id.vvPlayer;
      VideoView vvPlayer = rootView.findViewById(id);
      if (vvPlayer == null) {
        break missingId;
      }

      return new FragmentPlayVideoBinding((SlidingConstraintLayout) rootView, guidelineEnd,
          guidelineStart, ivCloseVideo, vvPlayer);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
