package de.objectcode.time4u.client.ui.adapter;

import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.viewers.IStructuredSelection;

import de.objectcode.time4u.client.store.api.RepositoryException;
import de.objectcode.time4u.client.store.api.RepositoryFactory;
import de.objectcode.time4u.client.ui.UIPlugin;
import de.objectcode.time4u.client.ui.util.CompoundSelection;
import de.objectcode.time4u.client.ui.util.CompoundSelectionEntityType;
import de.objectcode.time4u.server.api.data.Project;
import de.objectcode.time4u.server.api.data.ProjectSummary;

public class CompoundProjectAdapterFactory implements IAdapterFactory
{
  @SuppressWarnings("unchecked")
  public Object getAdapter(final Object adaptableObject, final Class adapterType)
  {
    if (!(adaptableObject instanceof CompoundSelection)) {
      return null;
    }

    final CompoundSelection selection = (CompoundSelection) adaptableObject;

    final List<Object> selections = (List<Object>)selection.getSelection(CompoundSelectionEntityType.PROJECT);

    if(selections != null && !selections.isEmpty()){
      if (Project.class.isAssignableFrom(adapterType)) {
        final Object sel = selections.get(0);

        if (sel instanceof Project) {
          return sel;
        } else if (sel instanceof ProjectSummary) {
          try {
            return RepositoryFactory.getRepository().getProjectRepository().getProject(((ProjectSummary) sel).getId());
          } catch (final RepositoryException e) {
            UIPlugin.getDefault().log(e);
          }
        }
      } else if (ProjectSummary.class.isAssignableFrom(adapterType)) {
        final Object sel = selections.get(0);

        if (sel instanceof ProjectSummary) {
          return sel;
        }
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public Class[] getAdapterList()
  {
    return new Class[] { Project.class, ProjectSummary.class };
  }
}
