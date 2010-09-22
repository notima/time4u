package de.objectcode.time4u.server.ejb.seam.api.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.persistence.Query;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import de.objectcode.time4u.server.api.data.EntityType;
import de.objectcode.time4u.server.ejb.seam.api.report.parameter.BaseParameterValue;
import de.objectcode.time4u.server.ejb.util.ReportEL;

@XmlType(name = "team")
@XmlRootElement(name = "team")
public class TeamFilter implements IFilter
{

  private static final long serialVersionUID = -8073517392782514895L;
  
  private String m_teamExpression;
  private List<String> m_teams;

  public TeamFilter(){ }  
  
  public TeamFilter(final List<String> teams)
  {
    m_teams = teams;
  }

  public TeamFilter(final String team){
    m_teams = new ArrayList<String>();
    m_teams.add(team);
  }
  
  @XmlAttribute(name = "team-expression")  
  public String getTeamExpression()
  {
    return m_teamExpression;
  }

  public void setTeamExpression(String m_teamExpression)
  {
    this.m_teamExpression = m_teamExpression;
  }

  @XmlElement(name = "team", namespace = "http://objectcode.de/time4u/ejb/seam/filter")
  public List<String> getTeams()
  {
    return m_teams;
  }
  
  public void setTeam(List<String> teams)
  {
    this.m_teams = teams;
  }

  @Override
  public String getWhereClause(EntityType entityType, Map<String, BaseParameterValue> parameters, ELContext context)
  {
    switch (entityType) {
      case WORKITEM:
        return "(team.name in (:teams))";
      case DAYINFO:
        return "(team.name in (:teams))";
      default:
        throw new RuntimeException("TeamFilter not applicable for entity type: " + entityType);
    }
  }

  @Override
  public void setQueryParameters(EntityType entityType, Query query, Map<String, BaseParameterValue> parameters,
      ELContext context)
  {
    if(m_teamExpression != null){
      final ExpressionFactory factory = ReportEL.getExpressionFactory();
      factory.createValueExpression(context, m_teamExpression, String.class)
      .getValue(context);
    } else {
      query.setParameter("teams", m_teams);
    }

  }

}
