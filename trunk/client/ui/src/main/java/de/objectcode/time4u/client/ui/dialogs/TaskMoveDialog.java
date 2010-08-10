package de.objectcode.time4u.client.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.objectcode.time4u.client.store.api.RepositoryFactory;
import de.objectcode.time4u.client.ui.UIPlugin;
import de.objectcode.time4u.client.ui.controls.ComboTreeViewer;
import de.objectcode.time4u.client.ui.provider.ProjectContentProvider;
import de.objectcode.time4u.client.ui.provider.ProjectLabelProvider;
import de.objectcode.time4u.server.api.data.ProjectSummary;
import de.objectcode.time4u.server.api.data.Task;

public class TaskMoveDialog extends Dialog
{
  private Task m_task;
  private ProjectSummary m_newParent;
  
  private ComboTreeViewer m_parentTreeViewer;
  
  public TaskMoveDialog(IShellProvider parentShell, Task task, ProjectSummary newParent)
  {
    super(parentShell);
    
    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE | getDefaultOrientation());
    
    this.m_task = task;
    this.m_newParent = newParent;
  }

  public Task getTask()
  {
    return m_task;
  }

  public ProjectSummary getNewParent()
  {
    return m_newParent;
  }
  
  @Override
  protected void configureShell(final Shell newShell)
  {
    super.configureShell(newShell);

    newShell.setText(UIPlugin.getDefault().getString("dialog.task.move.title"));
  }  
  
  @Override
  protected Control createDialogArea(final Composite parent)
  {
    final Composite composite = (Composite) super.createDialogArea(parent);
    final Composite root = new Composite(composite, SWT.NONE);
    root.setLayout(new GridLayout(2, false));
    root.setLayoutData(new GridData(GridData.FILL_BOTH));

    final Label nameLabel = new Label(root, SWT.NONE);
    nameLabel.setText(UIPlugin.getDefault().getString("task.label"));
    final Text nameText = new Text(root, SWT.BORDER);
    nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    nameText.setText(m_task.getName());
    nameText.setEnabled(false);

    final Label parentLabel = new Label(root, SWT.NONE);
    parentLabel.setText(UIPlugin.getDefault().getString("dialog.task.move.project.label"));
    m_parentTreeViewer = new ComboTreeViewer(root, SWT.BORDER | SWT.DROP_DOWN);
    m_parentTreeViewer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    m_parentTreeViewer.setContentProvider(new ProjectContentProvider(RepositoryFactory.getRepository()
        .getProjectRepository(), false, true));
    m_parentTreeViewer.setLabelProvider(new ProjectLabelProvider());
    m_parentTreeViewer.setInput(new Object());
    if (m_newParent != null) {
      m_parentTreeViewer.setSelection(new StructuredSelection(m_newParent));
    } else {
      m_parentTreeViewer.setSelection(new StructuredSelection(ProjectContentProvider.ROOT));
    }

    return composite;
  }
  
  @Override
  protected void okPressed()
  {
    m_newParent = null;
    final ISelection selection = m_parentTreeViewer.getSelection();
    if (selection instanceof IStructuredSelection) {
      final Object sel = ((IStructuredSelection) selection).getFirstElement();

      if (sel != null && sel instanceof ProjectSummary) {
        m_newParent = (ProjectSummary) sel;
      }
    }

    super.okPressed();
  }
  

}
