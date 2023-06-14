// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewbinding.ViewBinding;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.forthgreen.app.R;
import com.forthgreen.app.views.utils.SlidingConstraintLayout;
import com.google.android.gms.maps.MapView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentRestaurantsListingBinding implements ViewBinding {
  @NonNull
  private final SlidingConstraintLayout rootView;

  @NonNull
  public final ShimmerFrameLayout flShimmer;

  @NonNull
  public final Group groupRestaurantListView;

  @NonNull
  public final ImageView ivCurrentLocation;

  @NonNull
  public final LinearLayout llRestaurants;

  @NonNull
  public final MapView mapRestaurants;

  @NonNull
  public final RecyclerView recyclerView;

  @NonNull
  public final RecyclerView rvSingleRestaurant;

  @NonNull
  public final SlidingConstraintLayout sclRoot;

  @NonNull
  public final SwipeRefreshLayout swipeRefreshLayout;

  @NonNull
  public final ToolbarBinding toolbar;

  @NonNull
  public final TextView tvNoData;

  @NonNull
  public final TextView tvSearchHere;

  @NonNull
  public final TextView tvShowMore;

  private FragmentRestaurantsListingBinding(@NonNull SlidingConstraintLayout rootView,
      @NonNull ShimmerFrameLayout flShimmer, @NonNull Group groupRestaurantListView,
      @NonNull ImageView ivCurrentLocation, @NonNull LinearLayout llRestaurants,
      @NonNull MapView mapRestaurants, @NonNull RecyclerView recyclerView,
      @NonNull RecyclerView rvSingleRestaurant, @NonNull SlidingConstraintLayout sclRoot,
      @NonNull SwipeRefreshLayout swipeRefreshLayout, @NonNull ToolbarBinding toolbar,
      @NonNull TextView tvNoData, @NonNull TextView tvSearchHere, @NonNull TextView tvShowMore) {
    this.rootView = rootView;
    this.flShimmer = flShimmer;
    this.groupRestaurantListView = groupRestaurantListView;
    this.ivCurrentLocation = ivCurrentLocation;
    this.llRestaurants = llRestaurants;
    this.mapRestaurants = mapRestaurants;
    this.recyclerView = recyclerView;
    this.rvSingleRestaurant = rvSingleRestaurant;
    this.sclRoot = sclRoot;
    this.swipeRefreshLayout = swipeRefreshLayout;
    this.toolbar = toolbar;
    this.tvNoData = tvNoData;
    this.tvSearchHere = tvSearchHere;
    this.tvShowMore = tvShowMore;
  }

  @Override
  @NonNull
  public SlidingConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentRestaurantsListingBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentRestaurantsListingBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_restaurants_listing, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentRestaurantsListingBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.flShimmer;
      ShimmerFrameLayout flShimmer = rootView.findViewById(id);
      if (flShimmer == null) {
        break missingId;
      }

      id = R.id.groupRestaurantListView;
      Group groupRestaurantListView = rootView.findViewById(id);
      if (groupRestaurantListView == null) {
        break missingId;
      }

      id = R.id.ivCurrentLocation;
      ImageView ivCurrentLocation = rootView.findViewById(id);
      if (ivCurrentLocation == null) {
        break missingId;
      }

      id = R.id.llRestaurants;
      LinearLayout llRestaurants = rootView.findViewById(id);
      if (llRestaurants == null) {
        break missingId;
      }

      id = R.id.mapRestaurants;
      MapView mapRestaurants = rootView.findViewById(id);
      if (mapRestaurants == null) {
        break missingId;
      }

      id = R.id.recyclerView;
      RecyclerView recyclerView = rootView.findViewById(id);
      if (recyclerView == null) {
        break missingId;
      }

      id = R.id.rvSingleRestaurant;
      RecyclerView rvSingleRestaurant = rootView.findViewById(id);
      if (rvSingleRestaurant == null) {
        break missingId;
      }

      SlidingConstraintLayout sclRoot = (SlidingConstraintLayout) rootView;

      id = R.id.swipeRefreshLayout;
      SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(id);
      if (swipeRefreshLayout == null) {
        break missingId;
      }

      id = R.id.toolbar;
      View toolbar = rootView.findViewById(id);
      if (toolbar == null) {
        break missingId;
      }
      ToolbarBinding binding_toolbar = ToolbarBinding.bind(toolbar);

      id = R.id.tvNoData;
      TextView tvNoData = rootView.findViewById(id);
      if (tvNoData == null) {
        break missingId;
      }

      id = R.id.tvSearchHere;
      TextView tvSearchHere = rootView.findViewById(id);
      if (tvSearchHere == null) {
        break missingId;
      }

      id = R.id.tvShowMore;
      TextView tvShowMore = rootView.findViewById(id);
      if (tvShowMore == null) {
        break missingId;
      }

      return new FragmentRestaurantsListingBinding((SlidingConstraintLayout) rootView, flShimmer,
          groupRestaurantListView, ivCurrentLocation, llRestaurants, mapRestaurants, recyclerView,
          rvSingleRestaurant, sclRoot, swipeRefreshLayout, binding_toolbar, tvNoData, tvSearchHere,
          tvShowMore);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
