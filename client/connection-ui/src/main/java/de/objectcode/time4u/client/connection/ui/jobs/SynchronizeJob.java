package de.objectcode.time4u.client.connection.ui.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.objectcode.time4u.client.connection.api.ConnectionFactory;
import de.objectcode.time4u.client.connection.api.IConnection;
import de.objectcode.time4u.client.connection.ui.ConnectionUIPlugin;
import de.objectcode.time4u.client.store.api.IWorkItemRepository;
import de.objectcode.time4u.client.store.api.RepositoryException;
import de.objectcode.time4u.client.store.api.RepositoryFactory;
import de.objectcode.time4u.server.api.data.CalendarDay;
import de.objectcode.time4u.server.api.data.DayInfo;
import de.objectcode.time4u.server.api.data.ServerConnection;
import de.objectcode.time4u.server.api.filter.DayInfoFilter;

public class SynchronizeJob extends Job
{
  private final ServerConnection m_serverConnection;
  private Date m_lastRun;
  private String m_lastError;
  private SynchronizationStatus m_status;

  public SynchronizeJob(final ServerConnection serverConnection)
  {
    super("Synchronize Job");

    m_serverConnection = serverConnection;
    m_status = SynchronizationStatus.NONE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor)
  {
    try {
      this.createMissingDayInfo();
      
      final IConnection connection = ConnectionFactory.openConnection(m_serverConnection);

      connection.sychronizeNow(monitor);

      m_status = SynchronizationStatus.OK;

      RepositoryFactory.getRepository().getServerConnectionRepository().markSynchronized(m_serverConnection.getId());
    } catch (final Throwable e) {
      e.printStackTrace();
      ConnectionUIPlugin.getDefault().log(e);
      m_status = SynchronizationStatus.FAILURE;
      m_lastError = e.getMessage();
    } finally {
      monitor.done();
    }
    m_lastRun = new Date();

    reschedule();

    return Status.OK_STATUS;
  }

  private void createMissingDayInfo() throws RepositoryException
  {
      final Date lastSynchronize = getLastSynchronize();
      final IWorkItemRepository workItemRepository = RepositoryFactory.getRepository().getWorkItemRepository();

      long tomorrow = System.currentTimeMillis() + (1000 * 60 * 60 * 24);
      final DayInfoFilter filter = new DayInfoFilter();
      filter.setFrom(new CalendarDay(lastSynchronize));
      filter.setTo(new CalendarDay(new Date(tomorrow)));

      final List<DayInfo> dayInfos = workItemRepository.getDayInfos(filter);

      long dayCount = (System.currentTimeMillis() - lastSynchronize.getTime()) / 1000 /60/60/24;
      for(int i = 0; i<=dayCount;i++){
        boolean found = false;
        final Calendar cal = Calendar.getInstance();
        cal.setTime(lastSynchronize);
        cal.add(Calendar.DAY_OF_MONTH, i);
        final CalendarDay day = new CalendarDay(cal);
        for (final DayInfo dayInfo : dayInfos) {
          if(dayInfo.getDay().equals(day)){
            found = true;
            break;
          }
        }
        if(!found){
          Integer regularTime = workItemRepository.getRegularTimeFromTimePolicy(day);
          if(regularTime > 0 || regularTime < 0){
            workItemRepository.setRegularTime(day, day, regularTime, new HashSet<String>());
          }
        }
      }
  }

  public Date getLastSynchronize() throws RepositoryException{
    Date lastSynchronize = null;
      List<ServerConnection> serverConnections = RepositoryFactory.getRepository().getServerConnectionRepository().getServerConnections();
      for (ServerConnection serverConnection : serverConnections) {
        if(serverConnection.getId() == m_serverConnection.getId()){
          lastSynchronize = serverConnection.getLastSynchronize();
        }
      }
      
      if(lastSynchronize == null){
        List<DayInfo> dayInfos = RepositoryFactory.getRepository().getWorkItemRepository().getDayInfos(new DayInfoFilter());
        
        if(dayInfos != null && !dayInfos.isEmpty()){
          DayInfo dayInfo = dayInfos.get(0);
          lastSynchronize = dayInfo.getDay().getDate();
        } else {
          lastSynchronize = new Date();
        }
      }
      
    return lastSynchronize;
  }

  public Date getLastRun()
  {
    return m_lastRun;
  }

  public String getLastError()
  {
    return m_lastError;
  }

  public SynchronizationStatus getStatus()
  {
    return m_status;
  }

  public void reschedule()
  {
    if (m_status == SynchronizationStatus.NONE) {
      m_status = SynchronizationStatus.SCHEDULED;
    }
    if (m_serverConnection.getSynchronizeInterval() > 0) {
      schedule(m_serverConnection.getSynchronizeInterval() * 1000L);
    }
  }
}
