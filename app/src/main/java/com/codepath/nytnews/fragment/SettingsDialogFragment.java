package com.codepath.nytnews.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.codepath.nytnews.R;
import com.codepath.nytnews.models.FilterSettings;
import com.codepath.nytnews.utils.AppConstants;
import com.codepath.nytnews.utils.errors.ErrorHandler;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsDialogFragment extends DialogFragment {
  private static final String TAG = SettingsDialogFragment.class.getSimpleName();
  private FilterSettings mFilterSettings;

  @BindView(R.id.beginDateTextView)
  TextView beginDateTextView;
  @BindView(R.id.sortSpinner)
  Spinner sortSpinner;
  @BindView(R.id.artsCheckbox)
  CheckBox artsCheckbox;
  @BindView(R.id.fashionStyleCheckbox)
  CheckBox fashionStyleCheckbox;
  @BindView(R.id.sportsCheckbox)
  CheckBox sportsCheckbox;
  @BindView(R.id.saveButton)
  Button saveButton;

  public interface SettingsDialogListener {
    void onFinishSettingsDialog(FilterSettings filterSettings);
  }


  public SettingsDialogFragment() {
    // Empty constructor is required for DialogFragment
    // Make sure not to add arguments to the constructor
    // Use `newInstance` instead as shown below
  }

  public static SettingsDialogFragment newInstance(FilterSettings settings) {
    SettingsDialogFragment frag = new SettingsDialogFragment();
    Bundle args = new Bundle();
    args.putParcelable(AppConstants.FILTER_SETTINGS_EXTRA, Parcels.wrap(settings));
    frag.setArguments(args);
    return frag;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_settings, container);
    ButterKnife.bind(this, view);
    return view;
  }


  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // Fetch arguments from bundle and set title
    mFilterSettings = Parcels.unwrap(getArguments().getParcelable(AppConstants.FILTER_SETTINGS_EXTRA));
    if (mFilterSettings != null) {
      initDialog();
    } else {
      mFilterSettings = new FilterSettings();
    }
  }

  private void initDialog() {
    setDateText();
    setSpinnerToValue(mFilterSettings.sortOrder);
    if (mFilterSettings.newsDeskValues != null) {
      if (mFilterSettings.newsDeskValues.contains(artsCheckbox.getText().toString())) {
        artsCheckbox.setChecked(true);
      }

      if (mFilterSettings.newsDeskValues.contains(sportsCheckbox.getText().toString())) {
        sportsCheckbox.setChecked(true);
      }

      if (mFilterSettings.newsDeskValues.contains(fashionStyleCheckbox.getText().toString())) {
        fashionStyleCheckbox.setChecked(true);
      }
    }
  }

  public void setSpinnerToValue(String value) {
    int index = 0;
    SpinnerAdapter adapter = sortSpinner.getAdapter();
    for (int i = 0; i < adapter.getCount(); i++) {
      if (adapter.getItem(i).equals(value)) {
        index = i;
        break; // terminate loop
      }
    }
    sortSpinner.setSelection(index);
  }

  @OnClick(R.id.beginDateTextView)
  public void showDatePicker() {
    // http://stackoverflow.com/a/20673756
    Calendar calendar = Calendar.getInstance();
    if (!TextUtils.isEmpty(mFilterSettings.beginDate)) {
      try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
        Date date = sdf.parse(mFilterSettings.beginDate);
        calendar.setTime(date);
      } catch (ParseException e) {
        ErrorHandler.handleAppException(e, "Error from showDatePicker()");
      }
    }

    DatePickerFragment datePickerFragment = new DatePickerFragment();
    Bundle args = new Bundle();
    args.putInt(AppConstants.YEAR_EXTRA, calendar.get(Calendar.YEAR));
    args.putInt(AppConstants.MONTH_EXTRA, calendar.get(Calendar.MONTH));
    args.putInt(AppConstants.DAY_EXTRA, calendar.get(Calendar.DAY_OF_MONTH));
    datePickerFragment.setArguments(args);
    datePickerFragment.setListener(new DatePickerDialog.OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Log.d(TAG, "y=" + year + ",m=" + monthOfYear + ",d=" + dayOfMonth);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        Date date = calendar.getTime();
        String dateString = getDate(date);
        mFilterSettings.beginDate = dateString;
        setDateText();
      }
    });
    datePickerFragment.show(getFragmentManager(), "Date Picker");
  }

  @OnClick(R.id.saveButton)
  public void save(View view) {
    Log.d(TAG, "----- save");

    SettingsDialogListener listener = (SettingsDialogListener) getActivity();
    if (listener != null) {
      mFilterSettings.sortOrder = sortSpinner.getSelectedItem().toString();
      List<String> newsDeskArrayList = new ArrayList<>();
      if (artsCheckbox.isChecked()) {
        newsDeskArrayList.add(artsCheckbox.getText().toString());
      }
      if (sportsCheckbox.isChecked()) {
        newsDeskArrayList.add(sportsCheckbox.getText().toString());
      }
      if (fashionStyleCheckbox.isChecked()) {
        newsDeskArrayList.add(fashionStyleCheckbox.getText().toString());
      }
      mFilterSettings.newsDeskValues = newsDeskArrayList;
      listener.onFinishSettingsDialog(mFilterSettings);
    }

    dismiss();
  }

  private String getDate(Date date) {
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    String dateToStr = format.format(date);

    return dateToStr;
  }

  private void setDateText() {
    if (!TextUtils.isEmpty(mFilterSettings.beginDate)) {
      try {
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = oldDateFormat.parse(mFilterSettings.beginDate);

        SimpleDateFormat newDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String newDateString = newDateFormat.format(date);
        beginDateTextView.setText(newDateString);
        Log.d(TAG, "---------- newDateString: " + newDateString);
      } catch (ParseException e) {
        ErrorHandler.handleAppException(e, TAG+":setDateText -- " + mFilterSettings.beginDate);
      }
    }
  }
}
