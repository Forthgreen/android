// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.viewbinding.ViewBinding;
import com.forthgreen.app.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class RowRepliesRvBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final CheckBox cbLike;

  @NonNull
  public final CircleImageView civUserImage;

  @NonNull
  public final CardView cvReply;

  @NonNull
  public final Guideline guidelineEnd;

  @NonNull
  public final Guideline guidelineStart;

  @NonNull
  public final Guideline guidelineStartOuter;

  @NonNull
  public final ImageView ivReplyOptions;

  @NonNull
  public final TextView tvLikes;

  @NonNull
  public final TextView tvLoadMoreReplies;

  @NonNull
  public final TextView tvReply;

  @NonNull
  public final TextView tvUserFullName;

  @NonNull
  public final TextView tvViewMore;

  private RowRepliesRvBinding(@NonNull ConstraintLayout rootView, @NonNull CheckBox cbLike,
      @NonNull CircleImageView civUserImage, @NonNull CardView cvReply,
      @NonNull Guideline guidelineEnd, @NonNull Guideline guidelineStart,
      @NonNull Guideline guidelineStartOuter, @NonNull ImageView ivReplyOptions,
      @NonNull TextView tvLikes, @NonNull TextView tvLoadMoreReplies, @NonNull TextView tvReply,
      @NonNull TextView tvUserFullName, @NonNull TextView tvViewMore) {
    this.rootView = rootView;
    this.cbLike = cbLike;
    this.civUserImage = civUserImage;
    this.cvReply = cvReply;
    this.guidelineEnd = guidelineEnd;
    this.guidelineStart = guidelineStart;
    this.guidelineStartOuter = guidelineStartOuter;
    this.ivReplyOptions = ivReplyOptions;
    this.tvLikes = tvLikes;
    this.tvLoadMoreReplies = tvLoadMoreReplies;
    this.tvReply = tvReply;
    this.tvUserFullName = tvUserFullName;
    this.tvViewMore = tvViewMore;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RowRepliesRvBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RowRepliesRvBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.row_replies_rv, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RowRepliesRvBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cbLike;
      CheckBox cbLike = rootView.findViewById(id);
      if (cbLike == null) {
        break missingId;
      }

      id = R.id.civUserImage;
      CircleImageView civUserImage = rootView.findViewById(id);
      if (civUserImage == null) {
        break missingId;
      }

      id = R.id.cvReply;
      CardView cvReply = rootView.findViewById(id);
      if (cvReply == null) {
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

      id = R.id.guidelineStartOuter;
      Guideline guidelineStartOuter = rootView.findViewById(id);
      if (guidelineStartOuter == null) {
        break missingId;
      }

      id = R.id.ivReplyOptions;
      ImageView ivReplyOptions = rootView.findViewById(id);
      if (ivReplyOptions == null) {
        break missingId;
      }

      id = R.id.tvLikes;
      TextView tvLikes = rootView.findViewById(id);
      if (tvLikes == null) {
        break missingId;
      }

      id = R.id.tvLoadMoreReplies;
      TextView tvLoadMoreReplies = rootView.findViewById(id);
      if (tvLoadMoreReplies == null) {
        break missingId;
      }

      id = R.id.tvReply;
      TextView tvReply = rootView.findViewById(id);
      if (tvReply == null) {
        break missingId;
      }

      id = R.id.tvUserFullName;
      TextView tvUserFullName = rootView.findViewById(id);
      if (tvUserFullName == null) {
        break missingId;
      }

      id = R.id.tvViewMore;
      TextView tvViewMore = rootView.findViewById(id);
      if (tvViewMore == null) {
        break missingId;
      }

      return new RowRepliesRvBinding((ConstraintLayout) rootView, cbLike, civUserImage, cvReply,
          guidelineEnd, guidelineStart, guidelineStartOuter, ivReplyOptions, tvLikes,
          tvLoadMoreReplies, tvReply, tvUserFullName, tvViewMore);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}