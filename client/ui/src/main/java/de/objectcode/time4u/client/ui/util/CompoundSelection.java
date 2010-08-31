package de.objectcode.time4u.client.ui.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

public class CompoundSelection implements ISelection, IAdaptable
{
  Map<CompoundSelectionEntityType, Object> m_selections = new HashMap<CompoundSelectionEntityType, Object>();

  public synchronized Object getSelection(final CompoundSelectionEntityType type)
  {
    return m_selections.get(type);
  }

  public synchronized void setSelection(final CompoundSelectionEntityType type, final Object selection)
  {
    List<Object> selections = new ArrayList<Object>();
    if (selection == null) {
      m_selections.remove(type);
    } else {
      if(selection instanceof IStructuredSelection){
        selections.addAll(((IStructuredSelection)selection).toList());
      } else if(selection instanceof Serializable){
        selections.add(selection);
      }
      m_selections.put(type, selections);
    }
  }

  public boolean isEmpty()
  {
    return m_selections.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Object getAdapter(final Class adapter)
  {
    return Platform.getAdapterManager().getAdapter(this, adapter);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    final StringBuffer buffer = new StringBuffer("MultiEntitySelection(");
    buffer.append(m_selections).append(")");
    return buffer.toString();
  }

}
