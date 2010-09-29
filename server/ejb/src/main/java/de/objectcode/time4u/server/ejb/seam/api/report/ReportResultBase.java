package de.objectcode.time4u.server.ejb.seam.api.report;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.objectcode.time4u.server.ejb.seam.api.report.comparator.MonthComparator;

/**
 * Base class of a report result.
 * 
 * Note that report may contain hierarchic group-by results. I.e. the ReportResult may not contain any rows but a map of
 * ReportResultGroup instead. Each of these ReportResultGroup may contain rows or a map of sub-groups.
 * 
 * @author junglas
 */
public class ReportResultBase
{
  /** List of all column definitions. */
  protected List<ColumnDefinition> m_columns;
  /** List of all column definitions that have been used for group by. */
  protected List<ColumnDefinition> m_groupByColumns;
  /** List of report rows if there are no group-by. */
  protected List<ReportRow> m_rows;
  /** Aggregated values to be displayed in the footer. */
  protected Object[] m_aggregates;
  /** Map of group-by sub-reports. */
  protected Map<Object, ReportResultGroup> m_groups;
  /** Comparators to sort the mao */
  protected Map<Object,Comparator> m_comparators;

  protected ReportResultBase(final List<ColumnDefinition> columns, final List<ColumnDefinition> groupByColumns)
  {
    m_columns = columns;
    m_groupByColumns = groupByColumns;
    m_rows = new ArrayList<ReportRow>();
    m_groups = new TreeMap<Object, ReportResultGroup>();
    registerComparators();
  }

  private void registerComparators(){
    m_comparators = new HashMap<Object,Comparator>();
    m_comparators.put("Month", new MonthComparator());
  }
  public void addColumn(final ColumnType columnType, final String header)
  {
    m_columns.add(new ColumnDefinition(columnType, header, m_columns.size()));
  }

  public List<ColumnDefinition> getColumns()
  {
    return m_columns;
  }

  public void addGroupByColumn(final ColumnType columnType, final String header)
  {
    m_groupByColumns.add(new ColumnDefinition(columnType, header, m_groupByColumns.size()));
  }

  public List<ColumnDefinition> getGroupByColumns()
  {
    return m_groupByColumns;
  }

  public List<ReportRow> getRows()
  {
    return m_rows;
  }

  public boolean isHasRows()
  {
    return m_rows != null && m_rows.size() > 0;
  }

  public boolean isHasGroups()
  {
    return !m_groupByColumns.isEmpty();
  }

  public boolean isHasAggregates()
  {
    return m_aggregates != null;
  }

  public Object[] getAggregates()
  {
    return m_aggregates;
  }

  public void setAggregates(final List<Object> groupKey, final Object[] aggregates)
  {
    if (groupKey == null) {
      m_aggregates = aggregates;
    } else {
      ReportResultBase current = this;

      for (final Object key : groupKey) {
        if (current == null) {
          break;
        }
        current = current.m_groups.get(key);
      }

      if (current != null) {
        current.m_aggregates = aggregates;
      }
    }
  }

  public List<ReportResultGroup> getGroups()
  {
    Map<Object, ReportResultGroup> sortedMap = null;
    Comparator comparator = null;
    
    if(m_groupByColumns != null && !m_groupByColumns.isEmpty()){
      String grouping = m_groupByColumns.get(0).getHeader();
      comparator = m_comparators.get(grouping);
    }

    sortedMap = new TreeMap<Object, ReportResultGroup>(comparator);
    sortedMap.putAll(m_groups);

    return new ArrayList<ReportResultGroup>(sortedMap.values());
  }

  public void addRow(final List<ValueLabelPair> groups, final Object[] row)
  {
    addRow(0, groups, row);
  }

  protected void addRow(final int depth, final List<ValueLabelPair> groups, final Object[] row)
  {
    if (depth >= groups.size()) {
      if (row != null) {
        m_rows.add(new ReportRow(m_rows.size(), row));
      }
    } else {
      final ValueLabelPair top = groups.get(depth);
      ReportResultGroup group = m_groups.get(top.getValue());

      if (group == null) {
        final List<ColumnDefinition> groupByColumns = new ArrayList<ColumnDefinition>();

        if (m_groupByColumns.size() > 1) {
          groupByColumns.addAll(m_groupByColumns.subList(1, m_groupByColumns.size()));
        }
        group = new ReportResultGroup(top, m_columns, groupByColumns);

        m_groups.put(group.getValue(), group);
      }

      if (row != null) {
        group.addRow(depth + 1, groups, row);
      }
    }
  }
}
