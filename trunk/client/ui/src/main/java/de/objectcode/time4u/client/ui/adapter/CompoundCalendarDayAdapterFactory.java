package de.objectcode.time4u.client.ui.adapter;

import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;

import de.objectcode.time4u.client.ui.util.CompoundSelection;
import de.objectcode.time4u.client.ui.util.CompoundSelectionEntityType;
import de.objectcode.time4u.server.api.data.CalendarDay;

public class CompoundCalendarDayAdapterFactory implements IAdapterFactory
{
  @SuppressWarnings("unchecked")
  public Object getAdapter(final Object adaptableObject, final Class adapterType)
  {
    if (!(adaptableObject instanceof CompoundSelection)) {
      return null;
    }

    final CompoundSelection selection = (CompoundSelection) adaptableObject;

    if (CalendarDay.class.isAssignableFrom(adapterType)) {
      List<Object> selectionList = (List)selection.getSelection(CompoundSelectionEntityType.CALENDARDAY);
      final Object sel = selectionList.get(0);

      if (sel instanceof CalendarDay) {
        return sel;
      }
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  public Class[] getAdapterList()
  {
    return new Class[] { CalendarDay.class };
  }

}