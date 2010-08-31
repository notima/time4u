package de.objectcode.time4u.client.ui.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class WorkItemCopyAndDeleteDialogTableContentProvider implements IStructuredContentProvider
{

  @Override
  public void dispose()
  {
  }

  @Override
  public void inputChanged(Viewer arg0, Object arg1, Object arg2)
  {
  }

  @Override
  public Object[] getElements(Object inputElement)
  {
    List list = new ArrayList();
    if(inputElement instanceof List) {
      return ((List)inputElement).toArray();
    }
    return null;
  }

}
