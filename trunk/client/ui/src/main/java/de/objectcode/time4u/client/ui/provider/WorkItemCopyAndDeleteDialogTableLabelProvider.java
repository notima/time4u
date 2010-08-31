package de.objectcode.time4u.client.ui.provider;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.objectcode.time4u.client.store.api.IProjectRepository;
import de.objectcode.time4u.client.store.api.IRepository;
import de.objectcode.time4u.client.store.api.ITaskRepository;
import de.objectcode.time4u.client.store.api.IWorkItemRepository;
import de.objectcode.time4u.client.ui.UIPlugin;
import de.objectcode.time4u.client.ui.util.DateFormat;
import de.objectcode.time4u.client.ui.util.TimeFormat;
import de.objectcode.time4u.server.api.data.ProjectSummary;
import de.objectcode.time4u.server.api.data.TaskSummary;
import de.objectcode.time4u.server.api.data.WorkItem;

public class WorkItemCopyAndDeleteDialogTableLabelProvider  extends LabelProvider implements ITableLabelProvider
{

  private final IProjectRepository m_projectRepository;
  private final ITaskRepository m_taskRepository;
  
  public WorkItemCopyAndDeleteDialogTableLabelProvider(final IRepository repository)
  {
    super();
    m_projectRepository = repository.getProjectRepository();
    m_taskRepository = repository.getTaskRepository();
  }

  @Override
  public Image getColumnImage(final Object element, final int columnIndex)
  {
    return null;
  }

  @Override
  public String getColumnText(final Object element, int columnIndex)
  {
    
    if (element instanceof WorkItem) {
      final WorkItem workItem = (WorkItem) element;

      switch (columnIndex) {
        case 0:
          return String.valueOf(TimeFormat.format(workItem.getBegin()));
        case 1:
          return String.valueOf(TimeFormat.format(workItem.getEnd()));
        case 2:
          return String.valueOf(TimeFormat.format(workItem.getDuration()));
        case 3:
          try {
            final ProjectSummary project = m_projectRepository.getProjectSummary(workItem.getProjectId());
            return String.valueOf(project != null ? project.getName() : "");
          } catch (final Exception e) {
            UIPlugin.getDefault().log(e);
          }
        case 4:
          try {
            final TaskSummary todo = m_taskRepository.getTaskSummary(workItem.getTaskId());
            return String.valueOf(todo != null ? todo.getName() : "");
          } catch (final Exception e) {
            UIPlugin.getDefault().log(e);
          }
        case 5:
          if (workItem.getComment() == null) {
            return null;
          }
          return String.valueOf(workItem.getComment());
      }
    }

    return element.toString();
  }



}
