package de.objectcode.time4u.client.connection.impl.common.up;

import java.util.List;

import de.objectcode.time4u.client.connection.api.ConnectionException;
import de.objectcode.time4u.client.connection.impl.common.SynchronizationContext;
import de.objectcode.time4u.client.store.api.RepositoryException;
import de.objectcode.time4u.server.api.data.EntityType;
import de.objectcode.time4u.server.api.data.Project;
import de.objectcode.time4u.server.api.filter.ProjectFilter;

/**
 * Send project changes to the server.
 * 
 * @author junglas
 */
public class SendProjectChangesCommand extends BaseSendCommand<Project>
{
  public SendProjectChangesCommand()
  {
    super(EntityType.PROJECT);
  }

  @Override
  protected List<Project> queryEntities(final SynchronizationContext context, final long minRevision,
      final long maxRevision) throws RepositoryException
  {
    final ProjectFilter filter = new ProjectFilter();
    // Only send changes made by myself
    filter.setLastModifiedByClient(context.getRepository().getClientId());
    filter.setMinRevision(minRevision);
    filter.setMaxRevision(maxRevision);
    filter.setOrder(ProjectFilter.Order.ID);

    return context.getRepository().getProjectRepository().getProjects(filter);
  }

  @Override
  protected void sendEntity(final SynchronizationContext context, final Project entity) throws ConnectionException
  {
    context.getProjectService().storeProject(entity);
  }
}