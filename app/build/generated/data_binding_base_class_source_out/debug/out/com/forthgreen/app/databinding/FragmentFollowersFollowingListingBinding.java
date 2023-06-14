// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewbinding.ViewBinding;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.forthgreen.app.R;
import com.forthgreen.app.views.utils.SlidingConstraintLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentFollowersFollowingListingBinding implements ViewBinding {
  @NonNull
  private final SlidingConstraintLayout rootView;

  @NonNull
  public final ShimmerFrameLayout flShimmer;

  @NonNull
  public final LinearLayout llUsers;

  @NonNull
  public final RecyclerView recyclerView;

  @NonNull
  public final SwipeRefreshLayout swipeRefreshLayout;

  @NonNull
  public final TextView tvNoData;

  private FragmentFollowersFollowingListingBinding(@NonNull SlidingConstraintLayout rootView,
      @NonNull ShimmerFrameLayout flShimmer, @NonNull LinearLayout llUsers,
      @NonNull RecyclerView recyclerView, @NonNull SwipeRefreshLayout swipeRefreshLayout,
      @NonNull TextView tvNoData) {
    this.rootView = rootView;
    this.flShimmer = flShimmer;
    this.llUsers = llUsers;
    this.recyclerView = recyclerView;
    this.swipeRefreshLayout = swipeRefreshLayout;
    this.tvNoData = tvNoData;
  }

  @Override
  @NonNull
  public SlidingConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentFollowersFollowingListingBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentFollowersFollowingListingBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_followers_following_listing, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentFollowersFollowingListingBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.flShimmer;
      ShimmerFrameLayout flShimmer = rootView.findViewById(id);
      if (flShimmer == null) {
        break missingId;
      }

      id = R.id.llUsers;
      LinearLayout llUsers = rootView.findViewById(id);
      if (llUsers == null) {
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

      id = R.id.tvNoData;
      TextView tvNoData = rootView.findViewById(id);
      if (tvNoData == null) {
        break missingId;
      }

      return new FragmentFollowersFollowingListingBinding((SlidingConstraintLayout) rootView,
          flShimmer, llUsers, recyclerView, swipeRefreshLayout, tvNoData);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
