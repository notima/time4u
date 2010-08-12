package de.objectcode.time4u.client.ui.dialogs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.nebula.widgets.calendarcombo.CalendarCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.objectcode.time4u.client.ui.UIPlugin;
import de.objectcode.time4u.client.ui.controls.DateCombo;
import de.objectcode.time4u.server.api.data.CalendarDay;
import de.objectcode.time4u.server.api.data.WorkItem;

public class WorkitemCopyDialog extends Dialog
{

  private WorkItem m_workitemToCopy;
  private WorkItem m_copiedWorkitem;
  private DateCombo m_dateCombo;

  public WorkitemCopyDialog(final IShellProvider shellProvider,final WorkItem workitemToCopy)
  {
    super(shellProvider);

    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE | getDefaultOrientation());

    m_workitemToCopy = workitemToCopy;

    m_copiedWorkitem = new WorkItem();
    m_copiedWorkitem.setBegin(workitemToCopy.getBegin());
    m_copiedWorkitem.setEnd(workitemToCopy.getEnd());
    m_copiedWorkitem.setComment(workitemToCopy.getComment());
    m_copiedWorkitem.setProjectId(workitemToCopy.getProjectId());
    m_copiedWorkitem.setTaskId(workitemToCopy.getTaskId());
    m_copiedWorkitem.setTodoId(workitemToCopy.getTodoId());

    //    Button okButton = getButton(IDialogConstants.OK_ID);
    //    okButton.setEnabled(isSameDate()); 

  }

//  @Override
//  protected Control createContents(Composite parent)
//  {
//    Control contents = super.createContents(parent);
//
//    Button okButton = getButton(IDialogConstants.OK_ID);
//    okButton.setEnabled(!isSameDate()); 
//
//    return contents;
//  }

  public WorkItem getCopiedWorkitem()
  {
    return m_copiedWorkitem;
  }
  @Override
  protected Control createButtonBar(Composite parent)
  {
    Control buttonbar = super.createButtonBar(parent);

    Button okButton = getButton(IDialogConstants.OK_ID);
    okButton.setEnabled(!isSameDate()); 
    // TODO Auto-generated method stub
    return buttonbar;
  }
  @Override
  protected void configureShell(final Shell newShell)
  {
    super.configureShell(newShell);

    newShell.setText(UIPlugin.getDefault().getString("dialog.workitem.copy.title"));
  }

  @Override
  protected Control createDialogArea(final Composite parent)
  {
    final CalendarDay day = m_workitemToCopy.getDay();
    String workItemDate = String.format("%1$td.%1$tm.%1$tY", day.getDate());

    final Composite composite = (Composite) super.createDialogArea(parent);
    final Composite root = new Composite(composite, SWT.NONE);
    root.setLayout(new GridLayout(2, false));
    root.setLayoutData(new GridData(GridData.FILL_BOTH));

    final Label nameLabel = new Label(root, SWT.NONE);
    nameLabel.setText(UIPlugin.getDefault().getString("dialog.workitem.copy.workitem.date.name"));
    final Text nameText = new Text(root, SWT.BORDER);
    nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    nameText.setText(workItemDate);
    nameText.setEnabled(false);

    final Label parentLabel = new Label(root, SWT.NONE);
    parentLabel.setText(UIPlugin.getDefault().getString("dialog.workitem.copy.workitem.newdate.name"));
    m_dateCombo = new DateCombo(root, SWT.BORDER);
    m_dateCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    m_dateCombo.select(Calendar.getInstance());
    m_dateCombo.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(final SelectionEvent e)
      {
        Button button = getButton(IDialogConstants.OK_ID);
        button.setEnabled(!isSameDate());

      }
    });

    return composite;
  }

  private boolean isSameDate()
  {
    Calendar calendar = m_dateCombo.getSelection();

    String strNewDate = String.format("%1$tY-%1$tm-%1$td", calendar.getTime());
    String strWorkItemDate = String.format("%1$tY-%1$tm-%1$td", m_workitemToCopy.getDay().getDate());
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yy-MM-dd");

    Date newDate = null;
    Date workItemDate = null;
    try {
      workItemDate = dateFormatter.parse(strWorkItemDate);
      newDate = dateFormatter.parse(strNewDate);

    } catch (ParseException e) {
      UIPlugin.getDefault().log(e);
    }


    return workItemDate.equals(newDate);

  }
  @Override
  protected void okPressed()
  {
    Calendar newDate = m_dateCombo.getSelection();

    m_copiedWorkitem.setDay(new CalendarDay(newDate));

    super.okPressed();
  }
}
