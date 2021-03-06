package de.objectcode.time4u.client.ui.actions;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import de.objectcode.time4u.client.store.api.RepositoryException;
import de.objectcode.time4u.client.store.api.RepositoryFactory;
import de.objectcode.time4u.client.ui.UIPlugin;
import de.objectcode.time4u.client.ui.dialogs.WorkItemDeleteDialog;
import de.objectcode.time4u.client.ui.dialogs.WorkItemDialog;
import de.objectcode.time4u.client.ui.dialogs.WorkitemCopyDialog;
import de.objectcode.time4u.client.ui.preferences.PreferenceConstants;
import de.objectcode.time4u.client.ui.util.CompoundSelection;
import de.objectcode.time4u.client.ui.util.CompoundSelectionEntityType;
import de.objectcode.time4u.client.ui.util.DateFormat;
import de.objectcode.time4u.client.ui.util.TimeFormat;
import de.objectcode.time4u.client.ui.views.WorkItemView;
import de.objectcode.time4u.server.api.data.CalendarDay;
import de.objectcode.time4u.server.api.data.ProjectSummary;
import de.objectcode.time4u.server.api.data.TaskSummary;
import de.objectcode.time4u.server.api.data.TodoSummary;
import de.objectcode.time4u.server.api.data.WorkItem;

public class WorkItemActionDelegate implements IWorkbenchWindowActionDelegate, IViewActionDelegate
{
  private IShellProvider m_shellProvider;
  private WorkItemView m_view;

  private IAdaptable m_selection;

  /**
   * {@inheritDoc}
   */
  public void init(final IWorkbenchWindow window)
  {
    m_shellProvider = new SameShellProvider(window.getShell());
    m_view = (WorkItemView) window.getActivePage().findView(WorkItemView.ID);
  }

  /**
   * {@inheritDoc}
   */
  public void init(final IViewPart view)
  {
    m_shellProvider = view.getSite();
    if (view instanceof WorkItemView) {
      m_view = (WorkItemView) view;
    }
  }

  /**
   * {@inheritDoc}
   */
  public void dispose()
  {
  }

  /**
   * {@inheritDoc}
   */
  public void run(final IAction action)
  {
    final String id = action.getId();

    if (m_selection == null) {
      m_selection = (IAdaptable) m_view.getSite().getSelectionProvider().getSelection();
    }

    final ProjectSummary selectedProject = (ProjectSummary) m_selection.getAdapter(ProjectSummary.class);
    final TaskSummary selectedTask = (TaskSummary) m_selection.getAdapter(TaskSummary.class);
    final CalendarDay selectedDay = (CalendarDay) m_selection.getAdapter(CalendarDay.class);
    final WorkItem selectedWorkItem = (WorkItem) m_selection.getAdapter(WorkItem.class);

    final List selectedWorkItems = (List)((CompoundSelection)m_selection).getSelection(CompoundSelectionEntityType.WORKITEM);

    if ("de.objectcode.time4u.client.ui.workitem.new".equals(id)) {
      if (selectedDay != null) {
        final WorkItemDialog dialog = new WorkItemDialog(m_view.getSite(), RepositoryFactory.getRepository(),
            selectedProject, selectedTask, selectedDay);

        if (dialog.open() == WorkItemDialog.OK) {
          try {
            RepositoryFactory.getRepository().getWorkItemRepository().storeWorkItem(dialog.getWorkItem(), true);
          } catch (final Exception e) {
            UIPlugin.getDefault().log(e);
          }
        }
      }
    } else if ("de.objectcode.time4u.client.ui.workitem.edit".equals(id)) {
      if (selectedWorkItem != null && selectedDay != null) {
        final WorkItemDialog dialog = new WorkItemDialog(m_view.getSite(), RepositoryFactory.getRepository(),
            selectedWorkItem);

        if (dialog.open() == WorkItemDialog.OK) {
          try {
            RepositoryFactory.getRepository().getWorkItemRepository().storeWorkItem(dialog.getWorkItem(), true);
          } catch (final Exception e) {
            UIPlugin.getDefault().log(e);
          }
        }
      }
    } else if ("de.objectcode.time4u.client.ui.workitem.delete".equals(id)) {
      if (selectedWorkItems != null) {

        final WorkItemDeleteDialog deleteDialog = new WorkItemDeleteDialog(m_view.getSite(), selectedWorkItems);

        if(deleteDialog.open() == WorkItemDeleteDialog.OK){
          try {
            for(Object obj : selectedWorkItems){
              RepositoryFactory.getRepository().getWorkItemRepository().deleteWorkItem((WorkItem)obj, true);
            }
          } catch (final Exception e) {
            UIPlugin.getDefault().log(e);
          }
        }
      }
    } else if ("de.objectcode.time4u.client.ui.workitem.continue".equals(id)) {
      if (selectedWorkItem != null) {
        if (UIPlugin.getDefault().isPunchedIn()) {
          UIPlugin.getDefault().punchOut();
        }

        try {
          final ProjectSummary project = RepositoryFactory.getRepository().getProjectRepository().getProjectSummary(
              selectedWorkItem.getProjectId());
          final TaskSummary task = RepositoryFactory.getRepository().getTaskRepository().getTaskSummary(
              selectedWorkItem.getTaskId());

          TodoSummary todo = null;
          if (selectedWorkItem.getTodoId() != null) {
            todo = RepositoryFactory.getRepository().getTodoRepository().getTodoSummary(selectedWorkItem.getTodoId());
          }
          UIPlugin.getDefault().punchIn(project, task, todo, selectedWorkItem.getComment());
        } catch (final Exception e) {
          UIPlugin.getDefault().log(e);
        }
      }
      //    } else if ("de.objectcode.time4u.client.workItem.tableView".equals(id)) {
      //      if (action.isChecked() && m_view != null) {
      //        m_view.setViewType(WorkItemView.ViewType.FLAT);
      //      }
      //    } else if ("de.objectcode.time4u.client.workItem.tableTreeView".equals(id)) {
      //      if (action.isChecked() && m_view != null) {
      //        m_view.setViewType(WorkItemView.ViewType.HIERARCHICAL);
      //      }
      //    } else if ("de.objectcode.time4u.client.workItem.dayGraphView".equals(id)) {
      //      if (action.isChecked() && m_view != null) {
      //        m_view.setViewType(WorkItemView.ViewType.DAYGRAPH);
      //      }
      //    } else if ("de.objectcode.time4u.client.workItem.search".equals(id)) {
      //      final WorkItemSearchDialog dialog = new WorkItemSearchDialog(m_shellProvider);
      //
      //      dialog.open();
    } else if("de.objectcode.time4u.client.ui.workitem.copy".equals(id)){
      if(selectedWorkItems != null && !selectedWorkItems.isEmpty()){
        final WorkitemCopyDialog workitemCopyDialog = new WorkitemCopyDialog(m_view.getSite(), selectedWorkItems);

        if(workitemCopyDialog.open() == WorkitemCopyDialog.OK){

          final List<WorkItem> copiedWorkIems = workitemCopyDialog.getCopiedWorkitem();

          if(selectedWorkItem.getDay().equals(copiedWorkIems.get(0).getDay())){
            return;
          }
          try {
            for(WorkItem workitem : copiedWorkIems){
              RepositoryFactory.getRepository().getWorkItemRepository().storeWorkItem(workitem, true);
            }
          } catch (RepositoryException e) {
            UIPlugin.getDefault().log(e);        }

        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public void selectionChanged(final IAction action, final ISelection selection)
  {
    if (selection instanceof IAdaptable) {
      m_selection = (IAdaptable) selection;
    } else if (selection instanceof IStructuredSelection) {
      final Object obj = ((IStructuredSelection) selection).getFirstElement();

      if (obj != null && obj instanceof IAdaptable) {
        m_selection = (IAdaptable) obj;
      }
    }
  }
}
