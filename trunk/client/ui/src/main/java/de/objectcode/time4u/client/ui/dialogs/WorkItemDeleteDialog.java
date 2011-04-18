package de.objectcode.time4u.client.ui.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import de.objectcode.time4u.client.store.api.RepositoryFactory;
import de.objectcode.time4u.client.ui.UIPlugin;
import de.objectcode.time4u.client.ui.provider.WorkItemCopyAndDeleteDialogTableContentProvider;
import de.objectcode.time4u.client.ui.provider.WorkItemCopyAndDeleteDialogTableLabelProvider;

public class WorkItemDeleteDialog extends Dialog
{

  private final List<?> m_workitemsToDelete;

  public WorkItemDeleteDialog(final IShellProvider parentShell, final List<?> workitemsToDelete)
  {
    super(parentShell);

    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | getDefaultOrientation());

    m_workitemsToDelete = workitemsToDelete;
  }

  @Override
  protected void configureShell(final Shell newShell)
  {
    super.configureShell(newShell);

    newShell.setText(UIPlugin.getDefault().getString("dialog.workitem.delete.title"));
  }

  @Override
  protected Control createDialogArea(final Composite parent)
  {
    final Composite composite = (Composite) super.createDialogArea(parent);
    final Composite root = new Composite(composite, SWT.NONE);
    root.setLayout(new GridLayout(1, false));

    final TableViewer tableViewer = new TableViewer(root, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.READ_ONLY);

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

    final GridData gridData = new GridData(GridData.FILL_BOTH);
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

    tableViewer.setInput(m_workitemsToDelete);

    return composite;
  }

}
