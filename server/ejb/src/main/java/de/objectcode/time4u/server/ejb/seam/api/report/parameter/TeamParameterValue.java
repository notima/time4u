package de.objectcode.time4u.server.ejb.seam.api.report.parameter;

import de.objectcode.time4u.server.ejb.seam.api.filter.IFilter;
import de.objectcode.time4u.server.ejb.seam.api.filter.TeamFilter;
import de.objectcode.time4u.server.ejb.seam.api.report.ReportParameterType;

public class TeamParameterValue extends BaseParameterValue
{

  private static final long serialVersionUID = -1860685083885224704L;
  
  private String m_team;
  
  public TeamParameterValue(String name, String label)
  {
    super(name, label, ReportParameterType.TEAM);
    
  }

  public String getTeam()
  {
    return m_team;
  }

  public void setTeam(String m_team)
  {
    this.m_team = m_team;
  }

  @Override
  public IFilter getFilter()
  {
    return new TeamFilter(m_team);
  }

}
