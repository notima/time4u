package de.objectcode.time4u.client.ui.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;

import de.objectcode.time4u.client.store.api.RepositoryException;
import de.objectcode.time4u.client.store.api.RepositoryFactory;
import de.objectcode.time4u.client.ui.UIPlugin;
import de.objectcode.time4u.server.api.data.CalendarDay;
import de.objectcode.time4u.server.api.data.DayInfo;
import de.objectcode.time4u.server.api.data.DayTag;

public class DayInfoActionDelegate implements IWorkbenchWindowPulldownDelegate2
{
  IShellProvider m_shellProvider;
  IWorkbenchWindow m_window;
  IAdaptable m_selection;

  private Menu m_menubarMenu;
  private Menu m_toolbarMenu;

  /**
   * {@inheritDoc}
   */
  public void init(final IWorkbenchWindow window)
  {
    m_shellProvider = new SameShellProvider(window.getShell());
    m_window = window;
  }

  /**
   * {@inheritDoc}
   */
  public Menu getMenu(final Menu parent)
  {
    if (m_menubarMenu != null) {
      m_menubarMenu.dispose();
    }
    m_menubarMenu = new Menu(parent);
    initMenu(m_menubarMenu);
    return m_menubarMenu;
  }

  /**
   * {@inheritDoc}
   */
  public Menu getMenu(final Control parent)
  {
    if (m_toolbarMenu != null) {
      m_toolbarMenu.dispose();
    }
    m_toolbarMenu = new Menu(parent);
    initMenu(m_toolbarMenu);
    return m_toolbarMenu;
  }

  /**
   * {@inheritDoc}
   */
  public void run(final IAction action)
  {
  }

  /**
   * {@inheritDoc}
   */
  public void selectionChanged(final IAction action, final ISelection selection)
  {
    if (selection instanceof IAdaptable) {
      m_selection = (IAdaptable) selection;
    }
  }

  /**
   * {@inheritDoc}
   */
  public void dispose()
  {
    if (m_menubarMenu != null) {
      m_menubarMenu.dispose();
      m_menubarMenu = null;
    }
    if (m_toolbarMenu != null) {
      m_toolbarMenu.dispose();
      m_toolbarMenu = null;
    }
  }

  /**
   * Creates the menu for the action
   */
  private void initMenu(final Menu menu)
  {
    menu.addMenuListener(new MenuAdapter() {
      @Override
      public void menuShown(final MenuEvent e)
      {
        final Menu m = (Menu) e.widget;
        final MenuItem[] items = m.getItems();
        for (int i = 0; i < items.length; i++) {
          items[i].dispose();
        }
        fillMenu(m);
      }
    });
  }

  private void fillMenu(final Menu menu)
  {
    try {
      final CalendarDay selection = (CalendarDay) m_selection.getAdapter(CalendarDay.class);
      final DayInfo dayInfo = RepositoryFactory.getRepository().getWorkItemRepository().getDayInfo(selection);
      final List<DayTag> dayTags = RepositoryFactory.getRepository().getWorkItemRepository().getDayTags();
      final Set<String> currentTags = dayInfo != null ? dayInfo.getTags() : new HashSet<String>();


      if (!dayTags.isEmpty()) {
        for (final DayTag dayTag : dayTags) {
          final MenuItem item = new MenuItem(menu, SWT.CHECK);

          item.setText(dayTag.getLabel());
          item.setSelection(currentTags.contains(dayTag.getName()));

          item.addSelectionListener(createSelectionListener(dayTag, item, menu));
        }
      } 
    }catch (final Exception e) {
      UIPlugin.getDefault().log(e);
    }
  }

  private void handleTimePolicySelection(Menu menu) throws RepositoryException
  {
    final MenuItem[] items = menu.getItems();
    final CalendarDay selection = (CalendarDay) m_selection.getAdapter(CalendarDay.class);
    final DayInfo dayInfo = RepositoryFactory.getRepository().getWorkItemRepository().getDayInfo(selection);
    final boolean hasNoTags = dayInfo != null?!dayInfo.isHasTags():true;
    final String markFreeDay = UIPlugin.getDefault().getString("action.timepolicy.markfree.label");
    final String markRegularDay = UIPlugin.getDefault().getString("action.timepolicy.markregular.label");

    for (final MenuItem menuItem : items) {
      if(menuItem.getText().equals(UIPlugin.getDefault().getString("menu.workItem.label"))){

        for(MenuItem item : menuItem.getMenu().getItems()){
          String text = item.getText();

          if(text.equals(markFreeDay)){
            item.setEnabled(hasNoTags);
            item.setSelection(dayInfo != null && dayInfo.getRegularTime() == 0);
          } else if(text.equals(markRegularDay)){
            item.setEnabled(hasNoTags);
            item.setSelection(dayInfo != null && (dayInfo.getRegularTime() < 0 || dayInfo.getRegularTime() > 0));
          }
        }
      }

    }

  }

  private SelectionListener createSelectionListener( 
      final DayTag dayTag, final MenuItem item, final Menu menu)
  {
    return new SelectionAdapter() {
      @Override
      public void widgetSelected(final SelectionEvent e)
      {
        try {
          final Menu parentMenu = menu.getParent().getMenuBar();
          final CalendarDay selection = (CalendarDay) m_selection.getAdapter(CalendarDay.class);
          final DayInfo dayInfo = RepositoryFactory.getRepository().getWorkItemRepository().getDayInfo(selection);
          final Set<String> currentTags = dayInfo != null ? dayInfo.getTags() : new HashSet<String>();
          currentTags.clear();
          if(item.getSelection()){
            currentTags.add(dayTag.getName());
          }

          final Integer regularTime = getRegularTime(currentTags);
          RepositoryFactory.getRepository().getWorkItemRepository().setRegularTime(selection, selection,
              regularTime, currentTags);
          handleTimePolicySelection( parentMenu);
        } catch (final Exception ex) {
          UIPlugin.getDefault().log(ex);
        }
      }

      private Integer getRegularTime(Set<String> currentTags) throws RepositoryException
      {
        if(!currentTags.isEmpty()){
          return dayTag.getRegularTime();
        }
        final CalendarDay selection = (CalendarDay) m_selection.getAdapter(CalendarDay.class);
        final Integer regularTime = RepositoryFactory.getRepository().getWorkItemRepository().getRegularTimeFromTimePolicy(selection);

        return regularTime;
      }
    };
  }
}
