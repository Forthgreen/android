// Generated by view binder compiler. Do not edit!
package com.forthgreen.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.viewbinding.ViewBinding;
import com.forthgreen.app.R;
import com.forthgreen.app.views.utils.SlidingConstraintLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentReportProfileBinding implements ViewBinding {
  @NonNull
  private final SlidingConstraintLayout rootView;

  @NonNull
  public final Button btnReport;

  @NonNull
  public final ConstraintLayout containerView;

  @NonNull
  public final TextInputEditText etOtherReport;

  @NonNull
  public final Guideline guidelineEnd;

  @NonNull
  public final Guideline guidelineStart;

  @NonNull
  public final RadioGroup rbContainer;

  @NonNull
  public final RadioButton rbReportContent;

  @NonNull
  public final RadioButton rbReportNonVegan;

  @NonNull
  public final RadioButton rbReportOther;

  @NonNull
  public final RadioButton rbReportPretend;

  @NonNull
  public final ScrollView svReport;

  @NonNull
  public final TextInputLayout tilOtherReview;

  @NonNull
  public final TextView tvReport;

  @NonNull
  public final TextView tvReportTitle;

  private FragmentReportProfileBinding(@NonNull SlidingConstraintLayout rootView,
      @NonNull Button btnReport, @NonNull ConstraintLayout containerView,
      @NonNull TextInputEditText etOtherReport, @NonNull Guideline guidelineEnd,
      @NonNull Guideline guidelineStart, @NonNull RadioGroup rbContainer,
      @NonNull RadioButton rbReportContent, @NonNull RadioButton rbReportNonVegan,
      @NonNull RadioButton rbReportOther, @NonNull RadioButton rbReportPretend,
      @NonNull ScrollView svReport, @NonNull TextInputLayout tilOtherReview,
      @NonNull TextView tvReport, @NonNull TextView tvReportTitle) {
    this.rootView = rootView;
    this.btnReport = btnReport;
    this.containerView = containerView;
    this.etOtherReport = etOtherReport;
    this.guidelineEnd = guidelineEnd;
    this.guidelineStart = guidelineStart;
    this.rbContainer = rbContainer;
    this.rbReportContent = rbReportContent;
    this.rbReportNonVegan = rbReportNonVegan;
    this.rbReportOther = rbReportOther;
    this.rbReportPretend = rbReportPretend;
    this.svReport = svReport;
    this.tilOtherReview = tilOtherReview;
    this.tvReport = tvReport;
    this.tvReportTitle = tvReportTitle;
  }

  @Override
  @NonNull
  public SlidingConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentReportProfileBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentReportProfileBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_report_profile, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentReportProfileBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnReport;
      Button btnReport = rootView.findViewById(id);
      if (btnReport == null) {
        break missingId;
      }

      id = R.id.containerView;
      ConstraintLayout containerView = rootView.findViewById(id);
      if (containerView == null) {
        break missingId;
      }

      id = R.id.etOtherReport;
      TextInputEditText etOtherReport = rootView.findViewById(id);
      if (etOtherReport == null) {
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

      id = R.id.rbContainer;
      RadioGroup rbContainer = rootView.findViewById(id);
      if (rbContainer == null) {
        break missingId;
      }

      id = R.id.rbReportContent;
      RadioButton rbReportContent = rootView.findViewById(id);
      if (rbReportContent == null) {
        break missingId;
      }

      id = R.id.rbReportNonVegan;
      RadioButton rbReportNonVegan = rootView.findViewById(id);
      if (rbReportNonVegan == null) {
        break missingId;
      }

      id = R.id.rbReportOther;
      RadioButton rbReportOther = rootView.findViewById(id);
      if (rbReportOther == null) {
        break missingId;
      }

      id = R.id.rbReportPretend;
      RadioButton rbReportPretend = rootView.findViewById(id);
      if (rbReportPretend == null) {
        break missingId;
      }

      id = R.id.svReport;
      ScrollView svReport = rootView.findViewById(id);
      if (svReport == null) {
        break missingId;
      }

      id = R.id.tilOtherReview;
      TextInputLayout tilOtherReview = rootView.findViewById(id);
      if (tilOtherReview == null) {
        break missingId;
      }

      id = R.id.tvReport;
      TextView tvReport = rootView.findViewById(id);
      if (tvReport == null) {
        break missingId;
      }

      id = R.id.tvReportTitle;
      TextView tvReportTitle = rootView.findViewById(id);
      if (tvReportTitle == null) {
        break missingId;
      }

      return new FragmentReportProfileBinding((SlidingConstraintLayout) rootView, btnReport,
          containerView, etOtherReport, guidelineEnd, guidelineStart, rbContainer, rbReportContent,
          rbReportNonVegan, rbReportOther, rbReportPretend, svReport, tilOtherReview, tvReport,
          tvReportTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}