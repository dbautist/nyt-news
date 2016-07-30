package com.codepath.nytnews.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.codepath.nytnews.utils.AppConstants;

public class DatePickerFragment extends DialogFragment {
  DatePickerDialog.OnDateSetListener onDateSetListener;
  private int mYear, mMonth, mDay;

  public DatePickerFragment() {
  }

  public void setListener(DatePickerDialog.OnDateSetListener listener) {
    onDateSetListener = listener;
  }

  @Override
  public void setArguments(Bundle args) {
    super.setArguments(args);
    mYear = args.getInt(AppConstants.YEAR_EXTRA);
    mMonth = args.getInt(AppConstants.MONTH_EXTRA);
    mDay = args.getInt(AppConstants.DAY_EXTRA);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new DatePickerDialog(getActivity(), onDateSetListener, mYear, mMonth, mDay);
  }
}
