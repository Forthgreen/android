// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Guideline;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewbinding.ViewBinding;
import com.forthgreen.app.R;
import com.forthgreen.app.views.utils.SlidingConstraintLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentBrandDetailBinding implements ViewBinding {
  @NonNull
  private final SlidingConstraintLayout rootView;

  @NonNull
  public final Button btnWebsite;

  @NonNull
  public final CheckBox cbBookmark;

  @NonNull
  public final CircleImageView civBrandIcon;

  @NonNull
  public final Guideline guidelineEnd;

  @NonNull
  public final Guideline guidelineStart;

  @NonNull
  public final ImageView ivBrandImage;

  @NonNull
  public final RecyclerView recyclerView;

  @NonNull
  public final SwipeRefreshLayout swipeRefreshLayout;

  @NonNull
  public final TextView tvBrandDetail;

  @NonNull
  public final TextView tvBrandFollowers;

  @NonNull
  public final TextView tvBrandName;

  @NonNull
  public final TextView tvViewMore;

  private FragmentBrandDetailBinding(@NonNull SlidingConstraintLayout rootView,
      @NonNull Button btnWebsite, @NonNull CheckBox cbBookmark,
      @NonNull CircleImageView civBrandIcon, @NonNull Guideline guidelineEnd,
      @NonNull Guideline guidelineStart, @NonNull ImageView ivBrandImage,
      @NonNull RecyclerView recyclerView, @NonNull SwipeRefreshLayout swipeRefreshLayout,
      @NonNull TextView tvBrandDetail, @NonNull TextView tvBrandFollowers,
      @NonNull TextView tvBrandName, @NonNull TextView tvViewMore) {
    this.rootView = rootView;
    this.btnWebsite = btnWebsite;
    this.cbBookmark = cbBookmark;
    this.civBrandIcon = civBrandIcon;
    this.guidelineEnd = guidelineEnd;
    this.guidelineStart = guidelineStart;
    this.ivBrandImage = ivBrandImage;
    this.recyclerView = recyclerView;
    this.swipeRefreshLayout = swipeRefreshLayout;
    this.tvBrandDetail = tvBrandDetail;
    this.tvBrandFollowers = tvBrandFollowers;
    this.tvBrandName = tvBrandName;
    this.tvViewMore = tvViewMore;
  }

  @Override
  @NonNull
  public SlidingConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentBrandDetailBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentBrandDetailBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_brand_detail, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentBrandDetailBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnWebsite;
      Button btnWebsite = rootView.findViewById(id);
      if (btnWebsite == null) {
        break missingId;
      }

      id = R.id.cbBookmark;
      CheckBox cbBookmark = rootView.findViewById(id);
      if (cbBookmark == null) {
        break missingId;
      }

      id = R.id.civBrandIcon;
      CircleImageView civBrandIcon = rootView.findViewById(id);
      if (civBrandIcon == null) {
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

      id = R.id.ivBrandImage;
      ImageView ivBrandImage = rootView.findViewById(id);
      if (ivBrandImage == null) {
        break missingId;
      }

      id = R.id.recyclerView;
      RecyclerView recyclerView = rootView.findViewById(id);
      if (recyclerView == null) {
        break missingId;
      }

      id = R.id.swipeRefreshLayout;
      SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(id);
      if (swipeRefreshLayout == null) {
        break missingId;
      }

      id = R.id.tvBrandDetail;
      TextView tvBrandDetail = rootView.findViewById(id);
      if (tvBrandDetail == null) {
        break missingId;
      }

      id = R.id.tvBrandFollowers;
      TextView tvBrandFollowers = rootView.findViewById(id);
      if (tvBrandFollowers == null) {
        break missingId;
      }

      id = R.id.tvBrandName;
      TextView tvBrandName = rootView.findViewById(id);
      if (tvBrandName == null) {
        break missingId;
      }

      id = R.id.tvViewMore;
      TextView tvViewMore = rootView.findViewById(id);
      if (tvViewMore == null) {
        break missingId;
      }

      return new FragmentBrandDetailBinding((SlidingConstraintLayout) rootView, btnWebsite,
          cbBookmark, civBrandIcon, guidelineEnd, guidelineStart, ivBrandImage, recyclerView,
          swipeRefreshLayout, tvBrandDetail, tvBrandFollowers, tvBrandName, tvViewMore);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
