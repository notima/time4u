package de.objectcode.time4u.client.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IActionFilter;

import de.objectcode.time4u.client.store.api.RepositoryException;
import de.objectcode.time4u.client.store.api.RepositoryFactory;
import de.objectcode.time4u.client.ui.UIPlugin;
import de.objectcode.time4u.client.ui.util.CompoundSelection;
import de.objectcode.time4u.client.ui.util.CompoundSelectionEntityType;
import de.objectcode.time4u.server.api.data.CalendarDay;
import de.objectcode.time4u.server.api.data.DayInfo;
import de.objectcode.time4u.server.api.data.Project;
import de.objectcode.time4u.server.api.data.ProjectSummary;
import de.objectcode.time4u.server.api.data.Task;
import de.objectcode.time4u.server.api.data.TaskSummary;
import de.objectcode.time4u.server.api.data.Todo;
import de.objectcode.time4u.server.api.data.TodoSummary;
import de.objectcode.time4u.server.api.data.WorkItem;

public class CompoundSelectionAdapterFactory implements IAdapterFactory
{
  @SuppressWarnings("unchecked")
  public Object getAdapter(final Object adaptableObject, final Class adapterType)
  {
    if (!(adaptableObject instanceof CompoundSelection)) {
      return null;
    }

    if (IActionFilter.class.isAssignableFrom(adapterType)) {
      return new MultiEntitySelectionActionFilter();
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  public Class[] getAdapterList()
  {
    return new Class[] { IActionFilter.class, Project.class, ProjectSummary.class, Task.class, TaskSummary.class };
  }

  static class MultiEntitySelectionActionFilter implements IActionFilter
  {
    public boolean testAttribute(final Object target, final String name, final String value)
    {
      final CompoundSelection selection = (CompoundSelection) target;

      if ("has".equals(name)) {
        return selection.getSelection(CompoundSelectionEntityType.valueOf(value)) != null;
      } else if ("hasNoTags".equals(name)) {
        final CalendarDay day = ((List<CalendarDay>) selection.getSelection(CompoundSelectionEntityType.CALENDARDAY)).get(0);
        try {
          final DayInfo dayInfo = RepositoryFactory.getRepository().getWorkItemRepository().getDayInfo(day);
          return dayInfo.getTags() != null && dayInfo.getTags().isEmpty();
        } catch (RepositoryException e) {
          UIPlugin.getDefault().log(e);
        }
      }
        else if ("PROJECT.active".equals(name)) {
        List projectSelection = (List)selection.getSelection(CompoundSelectionEntityType.PROJECT);
        ProjectSummary project = null; 

        if(projectSelection != null && !projectSelection.isEmpty()){

          project = (ProjectSummary) projectSelection.get(0);
        }

        if (project != null) {
          return Boolean.parseBoolean(value) == project.isActive();
        }


      } else if ("TASK.active".equals(name)) {
        List taskSelection = (List)selection.getSelection(CompoundSelectionEntityType.TASK);

        if(taskSelection != null && !taskSelection.isEmpty()){
          final TaskSummary task = (TaskSummary)taskSelection.get(0);

          if (task != null) {
            return Boolean.parseBoolean(value) == task.isActive();
          }
        }
      } else if ("WORKITEM.active".equals(name)) {

        List workItemSelections = (List)selection.getSelection(CompoundSelectionEntityType.WORKITEM);

        WorkItem workItem = null;
        boolean containsActiveWorkitem = false;

        if (workItemSelections != null && !workItemSelections.isEmpty()) {
          try {
            final WorkItem activeWorkItem = RepositoryFactory.getRepository().getWorkItemRepository()
            .getActiveWorkItem();

            if(activeWorkItem != null){
              for(Object sel : workItemSelections){
                workItem = (WorkItem)sel;
                if(workItem.getId().equals(
                    activeWorkItem.getId())){
                  containsActiveWorkitem = true;
                  break;
                }
              }
            }
          } catch (final Exception e) {
            UIPlugin.getDefault().log(e);
          }
        }
        return Boolean.parseBoolean(value) == containsActiveWorkitem;

      } else if("WORKITEM.useOnlyOneElement".equals(name)){ 
        List workItemSelections = (List)selection.getSelection(CompoundSelectionEntityType.WORKITEM);

        boolean onlyOneElement = Boolean.parseBoolean(value);
        return (workItemSelections != null && !workItemSelections.isEmpty()) 
        && ((onlyOneElement == false && workItemSelections.size() >= 1) || (onlyOneElement == true && workItemSelections.size() == 1));

      } else if (name != null && name.matches("TODO")){ 

        final List todoSelections = (List)selection.getSelection(CompoundSelectionEntityType.TODO);

        if(todoSelections != null && !todoSelections.isEmpty()){
          TodoSummary todoSummary = (TodoSummary)todoSelections.get(0);

          if (todoSummary != null) {
            if ("TODO.hasTask".equals(name)) {

              try {
                final Todo todo = RepositoryFactory.getRepository().getTodoRepository().getTodo(todoSummary.getId());

                return Boolean.parseBoolean(value) == (todo != null && todo.getTaskId() != null);
              } catch (final Exception e) {
                UIPlugin.getDefault().log(e);
              }
            } else if ("TODO.group".equals(name)) {
              return Boolean.parseBoolean(value) == todoSummary.isGroup();
            }
          } else if ("TODO.completed".equals(name)) {
            return Boolean.parseBoolean(value) == todoSummary.isCompleted();
          }
        }
      }
      return false;
    }
  }
}


