package de.objectcode.time4u.client.ui.dialogs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.IShellProvider;
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
import org.eclipse.swt.widgets.TableColumn;

import de.objectcode.time4u.client.store.api.RepositoryFactory;
import de.objectcode.time4u.client.ui.UIPlugin;
import de.objectcode.time4u.client.ui.controls.DateCombo;
import de.objectcode.time4u.client.ui.provider.WorkItemCopyAndDeleteDialogTableContentProvider;
import de.objectcode.time4u.client.ui.provider.WorkItemCopyAndDeleteDialogTableLabelProvider;
import de.objectcode.time4u.server.api.data.CalendarDay;

import de.objectcode.time4u.server.api.data.WorkItem;

public class WorkitemCopyDialog extends Dialog
{

  private List m_workitemsToCopy;
  private List<WorkItem> m_copiedWorkitems;
  private DateCombo m_toDateCombo;

  public WorkitemCopyDialog(final IShellProvider shellProvider,final List workitemsToCopy)
  {
    super(shellProvider);

    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | getDefaultOrientation());

    m_copiedWorkitems = new ArrayList<WorkItem>();
    m_workitemsToCopy = workitemsToCopy;

    for(Object obj : workitemsToCopy){
      WorkItem workItemToCopy = (WorkItem)obj; 
      WorkItem copiedWorkitem = new WorkItem();

      copiedWorkitem.setBegin(workItemToCopy.getBegin());
      copiedWorkitem.setEnd(workItemToCopy.getEnd());
      copiedWorkitem.setComment(workItemToCopy.getComment());
      copiedWorkitem.setProjectId(workItemToCopy.getProjectId());
      copiedWorkitem.setTaskId(workItemToCopy.getTaskId());
      copiedWorkitem.setTodoId(workItemToCopy.getTodoId());
      m_copiedWorkitems.add(copiedWorkitem);
    }
  }
  public List<WorkItem> getCopiedWorkitem()
  {
    return m_copiedWorkitems;
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
    final CalendarDay day = ((WorkItem)m_workitemsToCopy.get(0)).getDay();

    final Composite composite = (Composite) super.createDialogArea(parent);
    final Composite root = new Composite(composite, SWT.NONE);
    root.setLayout(new GridLayout(4, false));

    final Label fromDateLbl = new Label(root, SWT.NONE);
    fromDateLbl.setText(UIPlugin.getDefault().getString("dialog.workitem.copy.workitem.date.name"));
    DateCombo fromDateCombo = new DateCombo(root, SWT.BORDER | SWT.READ_ONLY);
    fromDateCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    fromDateCombo.select(day.getCalendar());

    final Label toDateLbl = new Label(root, SWT.NONE);
    toDateLbl.setText(UIPlugin.getDefault().getString("dialog.workitem.copy.workitem.newdate.name"));
    m_toDateCombo = new DateCombo(root, SWT.BORDER );
    m_toDateCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    m_toDateCombo.select(Calendar.getInstance());
    m_toDateCombo.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(final SelectionEvent e)
      {
        Button button = getButton(IDialogConstants.OK_ID);
        button.setEnabled(!isSameDate());

      }
    });

    TableViewer tableViewer = new TableViewer(root, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.READ_ONLY);

    final TableLayout layout = new TableLayout();
    layout.addColumnData(new ColumnWeightData(10, 50, true));
    layout.addColumnData(new ColumnWeightData(10, 50, true));
    layout.addColumnData(new ColumnWeightData(10, 50, true));
    layout.addColumnData(new ColumnWeightData(15, 50, true));
    layout.addColumnData(new ColumnWeightData(15, 50, true));
    layout.addColumnData(new ColumnWeightData(30, 100, true));

    tableViewer.getTable().setHeaderVisible(true);
    tableViewer.getTable().setLinesVisible(true);
    tableViewer.getTable().setLayout(layout);

    GridData gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 4;
    gridData.widthHint = convertWidthInCharsToPixels(100);
    tableViewer.getTable().setLayoutData(gridData);

    final TableColumn beginColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
    beginColumn.setText("Begin");
    beginColumn.setMoveable(false);
    final TableColumn endColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
    endColumn.setText("End");
    endColumn.setMoveable(false);
    final TableColumn durationColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
    durationColumn.setText("Duration");
    durationColumn.setMoveable(false);
    final TableColumn projectColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
    projectColumn.setText("Project");
    projectColumn.setMoveable(false);
    final TableColumn taskColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
    taskColumn.setText("Task");
    taskColumn.setMoveable(false);
    final TableColumn commentColumn = new TableColumn(tableViewer.getTable(), SWT.LEFT);
    commentColumn.setText("Comment");
    commentColumn.setMoveable(false);
    
    tableViewer.setContentProvider(new WorkItemCopyAndDeleteDialogTableContentProvider());
    tableViewer.setLabelProvider(new WorkItemCopyAndDeleteDialogTableLabelProvider(RepositoryFactory.getRepository()));
    
    tableViewer.setInput(m_workitemsToCopy);
  
    return composite;
  }

  private boolean isSameDate()
  {
    Calendar calendar = m_toDateCombo.getSelection();

    String strNewDate = String.format("%1$tY-%1$tm-%1$td", calendar.getTime());
    String strWorkItemDate = String.format("%1$tY-%1$tm-%1$td", ((WorkItem)m_workitemsToCopy.get(0)).getDay().getDate());
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
    Calendar newDate = m_toDateCombo.getSelection();
    for(WorkItem workitem :  m_copiedWorkitems){
      workitem.setDay(new CalendarDay(newDate));
    }

    super.okPressed();
  }
}
