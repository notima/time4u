package de.objectcode.time4u.server.ejb.seam.api.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import de.objectcode.time4u.server.api.data.TimeContingent;
import de.objectcode.time4u.server.entities.DayInfoEntity;
import de.objectcode.time4u.server.entities.DayTagEntity;
import de.objectcode.time4u.server.entities.TimePolicyEntity;
import de.objectcode.time4u.server.entities.WeekTimePolicyEntity;

@XmlEnum
@XmlType(name = "dayinfo-projection")
public enum DayInfoProjection implements IProjection
{
  DATE(ColumnType.DATE, "Date") {
    public Object project(final IRowDataAdapter rowData)
    {
      return rowData.getDayInfo().getDate();
    }
  },
  SUM_DURATIONS(ColumnType.TIME, "Sum durations") {
    public Object project(final IRowDataAdapter rowData)
    {
      final Map<TimeContingent, Integer> timeContingents = rowData.getDayInfo().getTimeContingents();
      int sumDurations = 0;

      for (final Integer duration : timeContingents.values()) {
        sumDurations += duration;
      }

      return sumDurations;
    }

    @Override
    public IAggregation createAggregation()
    {
      return new SumAggregation();
    }
  },
  REGULAR_TIME(ColumnType.TIME, "Effective regular time") {
    public Object project(final IRowDataAdapter rowData)
    {
      return rowData.getDayInfo().getEffectiveRegularTime();
    }

    @Override
    public IAggregation createAggregation()
    {
      return new SumAggregation();
    }
  },
  STANDARD_TIME(ColumnType.TIME, "Standard time") {
    public Object project(final IRowDataAdapter rowData)
    {
      List<TimePolicyEntity> timePolicies = rowData.getTimePolicies();
      java.sql.Date calendarDay = rowData.getDayInfo().getDate();
      int regularTime = 0;
      for (TimePolicyEntity timePolicyEntity : timePolicies) {
        regularTime = ((WeekTimePolicyEntity)timePolicyEntity).getRegularTime(calendarDay);
        if(regularTime > 0){
          break;
        }
      }

      if(regularTime <= 0){
        regularTime = DayInfoEntity.DEFAULT_REGULARTIME;
      }
      return regularTime;
    }

    @Override
    public IAggregation createAggregation()
    {
      return new SumAggregation();
    }
  },
  WORKTIME(ColumnType.TIME, "Work time") {
    public Object project(final IRowDataAdapter rowData)
    {
      final Map<TimeContingent, Integer> timeContingents = rowData.getDayInfo().getTimeContingents();

      return timeContingents.get(TimeContingent.WORKTIME);
    }

    @Override
    public IAggregation createAggregation()
    {
      return new SumAggregation();
    }
  },
  OVERTIME(ColumnType.TIME, "Overtime") {
    public Object project(final IRowDataAdapter rowData)
    {
      final Map<TimeContingent, Integer> timeContingents = rowData.getDayInfo().getTimeContingents();

      return timeContingents.get(TimeContingent.WORKTIME) - rowData.getDayInfo().getEffectiveRegularTime();
    }

    @Override
    public IAggregation createAggregation()
    {
      return new SumAggregation();
    }
  },
  MONTH(ColumnType.NAME, "Month")
  {
    public Object project(IRowDataAdapter rowData)
    {
      SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMMM");
      Date date = rowData.getDayInfo().getDate();

      String month = dateFormatter.format(date);
      return month;
    }
  },
  TAGS(ColumnType.NAME_ARRAY, "Tags") {
    public Object project(final IRowDataAdapter rowData)
    {
      final List<String> tags = new ArrayList<String>();

      for (final DayTagEntity dayTag : rowData.getDayInfo().getTags()) {
        tags.add(dayTag.getName());
      }
      return tags;
    }
    @Override
    public IAggregation createAggregation()
    {
      return new ListAggregation();
    }
  };

  ColumnType m_columnType;
  String m_header;

  private DayInfoProjection(final ColumnType columnType, final String header)
  {
    m_columnType = columnType;
    m_header = header;
  }

  public ColumnDefinition getColumnDefinition(final int index)
  {
    return new ColumnDefinition(m_columnType, m_header, index);
  }

  public IAggregation createAggregation()
  {
    return null;
  }
}
