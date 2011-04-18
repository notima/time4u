package de.objectcode.time4u.client.ui.provider;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class WorkItemCopyAndDeleteDialogTableContentProvider implements IStructuredContentProvider
{

  public void dispose()
  {
  }

  public void inputChanged(final Viewer arg0, final Object arg1, final Object arg2)
  {
  }

  public Object[] getElements(final Object inputElement)
  {
    if (inputElement instanceof List) {
      return ((List<?>) inputElement).toArray();
    }
    return null;
  }

}
