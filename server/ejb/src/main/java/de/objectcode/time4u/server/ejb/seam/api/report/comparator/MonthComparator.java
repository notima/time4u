package de.objectcode.time4u.server.ejb.seam.api.report.comparator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class MonthComparator implements Comparator<String>
{

  private List<Integer> months = new ArrayList<Integer>();
  
  public MonthComparator(){
    months.add(Calendar.JANUARY);
    months.add(Calendar.FEBRUARY);
    months.add(Calendar.MARCH);
    months.add(Calendar.APRIL);
    months.add(Calendar.MAY);
    months.add(Calendar.JUNE);
    months.add(Calendar.JULY);
    months.add(Calendar.AUGUST);
    months.add(Calendar.SEPTEMBER);
    months.add(Calendar.OCTOBER);
    months.add(Calendar.NOVEMBER);
    months.add(Calendar.DECEMBER);
  }
  
  @Override
  public int compare(String dateStr1, String dateStr2)
  {
    SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM");
    Date date1 = null;
    Date date2 = null;
    Calendar tempDate = Calendar.getInstance();
    
    String temp = null;
    
    for(Integer month : months){
      tempDate.set(Calendar.MONTH, month);
      temp = dateFormatter.format(tempDate.getTime());
      if(dateStr1.equals(temp)){
        date1 = tempDate.getTime();
      }
      
      if(dateStr2.equals(temp)){
        date2 = tempDate.getTime();
      }
    }
    
    return date1.compareTo(date2);
  }



}
