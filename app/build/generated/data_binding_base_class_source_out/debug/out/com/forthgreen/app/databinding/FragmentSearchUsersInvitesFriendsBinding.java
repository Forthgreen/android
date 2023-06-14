// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.forthgreen.app.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentSearchUsersInvitesFriendsBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final LinearLayout etLinear;

  @NonNull
  public final EditText etSearchUsers;

  @NonNull
  public final ShimmerFrameLayout flShimmer;

  @NonNull
  public final LinearLayout llUsers;

  @NonNull
  public final RecyclerView recyclerView;

  @NonNull
  public final TextView tvCancelSearch;

  @NonNull
  public final TextView tvInvitesFriends;

  @NonNull
  public final TextView tvNoData;

  @NonNull
  public final TextView tvSuggestedUsers;

  @NonNull
  public final View viewDividerInvitesFriends;

  @NonNull
  public final View viewDividerSearch;

  private FragmentSearchUsersInvitesFriendsBinding(@NonNull LinearLayout rootView,
      @NonNull LinearLayout etLinear, @NonNull EditText etSearchUsers,
      @NonNull ShimmerFrameLayout flShimmer, @NonNull LinearLayout llUsers,
      @NonNull RecyclerView recyclerView, @NonNull TextView tvCancelSearch,
      @NonNull TextView tvInvitesFriends, @NonNull TextView tvNoData,
      @NonNull TextView tvSuggestedUsers, @NonNull View viewDividerInvitesFriends,
      @NonNull View viewDividerSearch) {
    this.rootView = rootView;
    this.etLinear = etLinear;
    this.etSearchUsers = etSearchUsers;
    this.flShimmer = flShimmer;
    this.llUsers = llUsers;
    this.recyclerView = recyclerView;
    this.tvCancelSearch = tvCancelSearch;
    this.tvInvitesFriends = tvInvitesFriends;
    this.tvNoData = tvNoData;
    this.tvSuggestedUsers = tvSuggestedUsers;
    this.viewDividerInvitesFriends = viewDividerInvitesFriends;
    this.viewDividerSearch = viewDividerSearch;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentSearchUsersInvitesFriendsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentSearchUsersInvitesFriendsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_search_users_invites_friends, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentSearchUsersInvitesFriendsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.etLinear;
      LinearLayout etLinear = rootView.findViewById(id);
      if (etLinear == null) {
        break missingId;
      }

      id = R.id.etSearchUsers;
      EditText etSearchUsers = rootView.findViewById(id);
      if (etSearchUsers == null) {
        break missingId;
      }

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

      id = R.id.tvCancelSearch;
      TextView tvCancelSearch = rootView.findViewById(id);
      if (tvCancelSearch == null) {
        break missingId;
      }

      id = R.id.tvInvitesFriends;
      TextView tvInvitesFriends = rootView.findViewById(id);
      if (tvInvitesFriends == null) {
        break missingId;
      }

      id = R.id.tvNoData;
      TextView tvNoData = rootView.findViewById(id);
      if (tvNoData == null) {
        break missingId;
      }

      id = R.id.tvSuggestedUsers;
      TextView tvSuggestedUsers = rootView.findViewById(id);
      if (tvSuggestedUsers == null) {
        break missingId;
      }

      id = R.id.viewDividerInvitesFriends;
      View viewDividerInvitesFriends = rootView.findViewById(id);
      if (viewDividerInvitesFriends == null) {
        break missingId;
      }

      id = R.id.viewDividerSearch;
      View viewDividerSearch = rootView.findViewById(id);
      if (viewDividerSearch == null) {
        break missingId;
      }

      return new FragmentSearchUsersInvitesFriendsBinding((LinearLayout) rootView, etLinear,
          etSearchUsers, flShimmer, llUsers, recyclerView, tvCancelSearch, tvInvitesFriends,
          tvNoData, tvSuggestedUsers, viewDividerInvitesFriends, viewDividerSearch);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
