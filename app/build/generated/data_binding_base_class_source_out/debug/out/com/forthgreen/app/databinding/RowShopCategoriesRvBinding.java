// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import com.chauthai.overscroll.RecyclerViewBouncy;
import com.forthgreen.app.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class RowShopCategoriesRvBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final RecyclerViewBouncy recyclerViewProducts;

  @NonNull
  public final TextView tvCategoryName;

  @NonNull
  public final TextView tvViewAll;

  private RowShopCategoriesRvBinding(@NonNull ConstraintLayout rootView,
      @NonNull RecyclerViewBouncy recyclerViewProducts, @NonNull TextView tvCategoryName,
      @NonNull TextView tvViewAll) {
    this.rootView = rootView;
    this.recyclerViewProducts = recyclerViewProducts;
    this.tvCategoryName = tvCategoryName;
    this.tvViewAll = tvViewAll;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RowShopCategoriesRvBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RowShopCategoriesRvBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.row_shop_categories_rv, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RowShopCategoriesRvBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.recyclerViewProducts;
      RecyclerViewBouncy recyclerViewProducts = rootView.findViewById(id);
      if (recyclerViewProducts == null) {
        break missingId;
      }

      id = R.id.tvCategoryName;
      TextView tvCategoryName = rootView.findViewById(id);
      if (tvCategoryName == null) {
        break missingId;
      }

      id = R.id.tvViewAll;
      TextView tvViewAll = rootView.findViewById(id);
      if (tvViewAll == null) {
        break missingId;
      }

      return new RowShopCategoriesRvBinding((ConstraintLayout) rootView, recyclerViewProducts,
          tvCategoryName, tvViewAll);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
