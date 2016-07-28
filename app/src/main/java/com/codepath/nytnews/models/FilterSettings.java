package com.codepath.nytnews.models;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class FilterSettings {
  public String beginDate;
  public String sortOrder;
  public List<String> newsDeskValues;

  public FilterSettings() {
  }
}
