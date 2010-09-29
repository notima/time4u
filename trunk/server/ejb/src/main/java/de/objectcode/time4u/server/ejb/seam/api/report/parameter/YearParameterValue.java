package de.objectcode.time4u.server.ejb.seam.api.report.parameter;

import java.util.Calendar;

import de.objectcode.time4u.server.ejb.seam.api.filter.DateRangeFilter;
import de.objectcode.time4u.server.ejb.seam.api.filter.IFilter;
import de.objectcode.time4u.server.ejb.seam.api.report.ReportParameterType;

public class YearParameterValue extends BaseParameterValue
{

  private static final long serialVersionUID = 8660362909666796631L;
  private int m_year;
  
  public YearParameterValue(String name, String label)
  {
    super(name, label, ReportParameterType.YEAR);
    
    final Calendar calendar = Calendar.getInstance();
    
    m_year = calendar.get(Calendar.YEAR);
  }

  
  public int getYear()
  {
    return m_year;
  }


  public void setYear(int year)
  {
    this.m_year = year;
  }


  @Override
  public IFilter getFilter()
  {
    return DateRangeFilter.filterYear(m_year);
  }

}
