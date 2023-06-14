// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.constraintlayout.widget.Guideline;
import androidx.viewbinding.ViewBinding;
import androidx.viewpager2.widget.ViewPager2;
import com.forthgreen.app.R;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.flexbox.FlexboxLayout;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class RowHomePostsRvBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final CheckBox cbLike;

  @NonNull
  public final CheckBox cbMute;

  @NonNull
  public final CircleImageView civPostUserImage;

  @NonNull
  public final SpringDotsIndicator dotIndicator;

  @NonNull
  public final FlexboxLayout flexBox;

  @NonNull
  public final Group groupMultiPictures;

  @NonNull
  public final Group groupUserLiked;

  @NonNull
  public final Group groupVideoPlay;

  @NonNull
  public final Guideline guidelineEnd;

  @NonNull
  public final Guideline guidelineStart;

  @NonNull
  public final PlayerView itemVideoPlayer;

  @NonNull
  public final ImageView itemVideoPlayerThumbnail;

  @NonNull
  public final ImageView ivMessage;

  @NonNull
  public final ImageView ivPostMenuOptions;

  @NonNull
  public final ProgressBar pbVideo;

  @NonNull
  public final TextView tvComments;

  @NonNull
  public final TextView tvLikes;

  @NonNull
  public final TextView tvPostDesc;

  @NonNull
  public final TextView tvSeeMore;

  @NonNull
  public final TextView tvTimeStamp;

  @NonNull
  public final TextView tvUserFullName;

  @NonNull
  public final TextView tvUserLiked;

  @NonNull
  public final View viewBottomDivider;

  @NonNull
  public final View viewFocus;

  @NonNull
  public final ViewPager2 viewPager;

  @NonNull
  public final View viewPictureDivider;

  @NonNull
  public final View viewProfile;

  @NonNull
  public final View viewUserDivider;

  @NonNull
  public final View viewVideoPlayer;

  private RowHomePostsRvBinding(@NonNull ConstraintLayout rootView, @NonNull CheckBox cbLike,
      @NonNull CheckBox cbMute, @NonNull CircleImageView civPostUserImage,
      @NonNull SpringDotsIndicator dotIndicator, @NonNull FlexboxLayout flexBox,
      @NonNull Group groupMultiPictures, @NonNull Group groupUserLiked,
      @NonNull Group groupVideoPlay, @NonNull Guideline guidelineEnd,
      @NonNull Guideline guidelineStart, @NonNull PlayerView itemVideoPlayer,
      @NonNull ImageView itemVideoPlayerThumbnail, @NonNull ImageView ivMessage,
      @NonNull ImageView ivPostMenuOptions, @NonNull ProgressBar pbVideo,
      @NonNull TextView tvComments, @NonNull TextView tvLikes, @NonNull TextView tvPostDesc,
      @NonNull TextView tvSeeMore, @NonNull TextView tvTimeStamp, @NonNull TextView tvUserFullName,
      @NonNull TextView tvUserLiked, @NonNull View viewBottomDivider, @NonNull View viewFocus,
      @NonNull ViewPager2 viewPager, @NonNull View viewPictureDivider, @NonNull View viewProfile,
      @NonNull View viewUserDivider, @NonNull View viewVideoPlayer) {
    this.rootView = rootView;
    this.cbLike = cbLike;
    this.cbMute = cbMute;
    this.civPostUserImage = civPostUserImage;
    this.dotIndicator = dotIndicator;
    this.flexBox = flexBox;
    this.groupMultiPictures = groupMultiPictures;
    this.groupUserLiked = groupUserLiked;
    this.groupVideoPlay = groupVideoPlay;
    this.guidelineEnd = guidelineEnd;
    this.guidelineStart = guidelineStart;
    this.itemVideoPlayer = itemVideoPlayer;
    this.itemVideoPlayerThumbnail = itemVideoPlayerThumbnail;
    this.ivMessage = ivMessage;
    this.ivPostMenuOptions = ivPostMenuOptions;
    this.pbVideo = pbVideo;
    this.tvComments = tvComments;
    this.tvLikes = tvLikes;
    this.tvPostDesc = tvPostDesc;
    this.tvSeeMore = tvSeeMore;
    this.tvTimeStamp = tvTimeStamp;
    this.tvUserFullName = tvUserFullName;
    this.tvUserLiked = tvUserLiked;
    this.viewBottomDivider = viewBottomDivider;
    this.viewFocus = viewFocus;
    this.viewPager = viewPager;
    this.viewPictureDivider = viewPictureDivider;
    this.viewProfile = viewProfile;
    this.viewUserDivider = viewUserDivider;
    this.viewVideoPlayer = viewVideoPlayer;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RowHomePostsRvBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RowHomePostsRvBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.row_home_posts_rv, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RowHomePostsRvBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cbLike;
      CheckBox cbLike = rootView.findViewById(id);
      if (cbLike == null) {
        break missingId;
      }

      id = R.id.cbMute;
      CheckBox cbMute = rootView.findViewById(id);
      if (cbMute == null) {
        break missingId;
      }

      id = R.id.civPostUserImage;
      CircleImageView civPostUserImage = rootView.findViewById(id);
      if (civPostUserImage == null) {
        break missingId;
      }

      id = R.id.dotIndicator;
      SpringDotsIndicator dotIndicator = rootView.findViewById(id);
      if (dotIndicator == null) {
        break missingId;
      }

      id = R.id.flexBox;
      FlexboxLayout flexBox = rootView.findViewById(id);
      if (flexBox == null) {
        break missingId;
      }

      id = R.id.groupMultiPictures;
      Group groupMultiPictures = rootView.findViewById(id);
      if (groupMultiPictures == null) {
        break missingId;
      }

      id = R.id.groupUserLiked;
      Group groupUserLiked = rootView.findViewById(id);
      if (groupUserLiked == null) {
        break missingId;
      }

      id = R.id.groupVideoPlay;
      Group groupVideoPlay = rootView.findViewById(id);
      if (groupVideoPlay == null) {
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

      id = R.id.itemVideoPlayer;
      PlayerView itemVideoPlayer = rootView.findViewById(id);
      if (itemVideoPlayer == null) {
        break missingId;
      }

      id = R.id.itemVideoPlayerThumbnail;
      ImageView itemVideoPlayerThumbnail = rootView.findViewById(id);
      if (itemVideoPlayerThumbnail == null) {
        break missingId;
      }

      id = R.id.ivMessage;
      ImageView ivMessage = rootView.findViewById(id);
      if (ivMessage == null) {
        break missingId;
      }

      id = R.id.ivPostMenuOptions;
      ImageView ivPostMenuOptions = rootView.findViewById(id);
      if (ivPostMenuOptions == null) {
        break missingId;
      }

      id = R.id.pbVideo;
      ProgressBar pbVideo = rootView.findViewById(id);
      if (pbVideo == null) {
        break missingId;
      }

      id = R.id.tvComments;
      TextView tvComments = rootView.findViewById(id);
      if (tvComments == null) {
        break missingId;
      }

      id = R.id.tvLikes;
      TextView tvLikes = rootView.findViewById(id);
      if (tvLikes == null) {
        break missingId;
      }

      id = R.id.tvPostDesc;
      TextView tvPostDesc = rootView.findViewById(id);
      if (tvPostDesc == null) {
        break missingId;
      }

      id = R.id.tvSeeMore;
      TextView tvSeeMore = rootView.findViewById(id);
      if (tvSeeMore == null) {
        break missingId;
      }

      id = R.id.tvTimeStamp;
      TextView tvTimeStamp = rootView.findViewById(id);
      if (tvTimeStamp == null) {
        break missingId;
      }

      id = R.id.tvUserFullName;
      TextView tvUserFullName = rootView.findViewById(id);
      if (tvUserFullName == null) {
        break missingId;
      }

      id = R.id.tvUserLiked;
      TextView tvUserLiked = rootView.findViewById(id);
      if (tvUserLiked == null) {
        break missingId;
      }

      id = R.id.viewBottomDivider;
      View viewBottomDivider = rootView.findViewById(id);
      if (viewBottomDivider == null) {
        break missingId;
      }

      id = R.id.viewFocus;
      View viewFocus = rootView.findViewById(id);
      if (viewFocus == null) {
        break missingId;
      }

      id = R.id.viewPager;
      ViewPager2 viewPager = rootView.findViewById(id);
      if (viewPager == null) {
        break missingId;
      }

      id = R.id.viewPictureDivider;
      View viewPictureDivider = rootView.findViewById(id);
      if (viewPictureDivider == null) {
        break missingId;
      }

      id = R.id.viewProfile;
      View viewProfile = rootView.findViewById(id);
      if (viewProfile == null) {
        break missingId;
      }

      id = R.id.viewUserDivider;
      View viewUserDivider = rootView.findViewById(id);
      if (viewUserDivider == null) {
        break missingId;
      }

      id = R.id.viewVideoPlayer;
      View viewVideoPlayer = rootView.findViewById(id);
      if (viewVideoPlayer == null) {
        break missingId;
      }

      return new RowHomePostsRvBinding((ConstraintLayout) rootView, cbLike, cbMute,
          civPostUserImage, dotIndicator, flexBox, groupMultiPictures, groupUserLiked,
          groupVideoPlay, guidelineEnd, guidelineStart, itemVideoPlayer, itemVideoPlayerThumbnail,
          ivMessage, ivPostMenuOptions, pbVideo, tvComments, tvLikes, tvPostDesc, tvSeeMore,
          tvTimeStamp, tvUserFullName, tvUserLiked, viewBottomDivider, viewFocus, viewPager,
          viewPictureDivider, viewProfile, viewUserDivider, viewVideoPlayer);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}