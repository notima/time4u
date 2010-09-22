package de.objectcode.time4u.server.ejb.seam.api.report.parameter;

import de.objectcode.time4u.server.ejb.seam.api.filter.DayTagFilter;
import de.objectcode.time4u.server.ejb.seam.api.filter.IFilter;
import de.objectcode.time4u.server.ejb.seam.api.report.ReportParameterType;

public class DayTagParameterValue extends BaseParameterValue
{

  private static final long serialVersionUID = 2608379890086451900L;
  
  private String m_tag;
  
  public DayTagParameterValue(String name, String label)
  {
    super(name, label, ReportParameterType.DAYTAG);
  }
  
  public String getTag()
  {
    return m_tag;
  }

  public void setTag(String m_tag)
  {
    this.m_tag = m_tag;
  }

  @Override
  public IFilter getFilter()
  {
    return new DayTagFilter(m_tag);
  }

}
