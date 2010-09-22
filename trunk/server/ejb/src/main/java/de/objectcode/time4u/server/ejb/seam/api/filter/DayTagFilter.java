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

@XmlType(name = "day-tag")
@XmlRootElement(name = "day-tag")
public class DayTagFilter implements IFilter
{
  private static final long serialVersionUID = 3376791571696379467L;

  /** All tags of this day */
  private List<String> m_tags;
  private String m_tagExpression;

  public DayTagFilter()
  {
  }

  public DayTagFilter(final List<String> tags)
  {
    m_tags = tags;
  }

  public DayTagFilter(final String tag){
    m_tags = new ArrayList<String>();
    m_tags.add(tag);
  }
  
  @XmlAttribute(name = "tag-expression")  
  public String getTagExpression()
  {
    return m_tagExpression;
  }

  public void setTagExpression(String m_tagExpression)
  {
    this.m_tagExpression = m_tagExpression;
  }

  @XmlElement(name = "tag", namespace = "http://objectcode.de/time4u/ejb/seam/filter")
  public List<String> getTags()
  {
    return m_tags;
  }

  public void setTags(final List<String> tags)
  {
    m_tags = tags;
  }

  /**
   * {@inheritDoc}
   */
  public String getWhereClause(final EntityType entityType, final Map<String, BaseParameterValue> parameters, ELContext context)
  {
    switch (entityType) {
      case WORKITEM:
        return "(tag.name in (:dayTags))";
      case DAYINFO:
        return "(0 < (select count(*) from d.tags tag where tag.name in (:dayTags)))";
      default:
        throw new RuntimeException("DayTagFilter not applicable for entity type: " + entityType);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void setQueryParameters(final EntityType entityType, final Query query,
      final Map<String, BaseParameterValue> parameters, ELContext context)
  {
    if(m_tagExpression != null){
      final ExpressionFactory factory = ReportEL.getExpressionFactory();
      factory.createValueExpression(context, m_tagExpression, String.class)
      .getValue(context);
    } else {
      query.setParameter("dayTags", m_tags);
    }
  }
}
