package com.telefonica.euro_iaas.sdc.client.services.impl;

import java.util.List;

import com.telefonica.euro_iaas.sdc.client.exception.InvalidExecutionException;
import com.telefonica.euro_iaas.sdc.client.exception.MaxTimeWaitingExceedException;
import com.telefonica.euro_iaas.sdc.client.exception.ResourceNotFoundException;
import com.telefonica.euro_iaas.sdc.client.services.ProductInstanceService;
import com.telefonica.euro_iaas.sdc.client.services.ProductInstanceSyncService;
import com.telefonica.euro_iaas.sdc.client.services.TaskService;
import com.telefonica.euro_iaas.sdc.model.Attribute;
import com.telefonica.euro_iaas.sdc.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.sdc.model.ProductInstance;
import com.telefonica.euro_iaas.sdc.model.Task;
import com.telefonica.euro_iaas.sdc.model.Task.TaskStates;
import com.telefonica.euro_iaas.sdc.model.dto.ProductInstanceDto;

/**
 * Default @link ProductInsatnceSyncService implementation using active waiting
 *
 * @author Sergio Arroyo
 *
 */
public class ProductInstanceSyncServiceImpl implements
        ProductInstanceSyncService {

    private ProductInstanceService productInstanceService;
    private TaskService taskService;

    /**
     * @param productInstanceService
     * @param taskService
     */
    public ProductInstanceSyncServiceImpl(
            ProductInstanceService productInstanceService,
            TaskService taskService) {
        this.productInstanceService = productInstanceService;
        this.taskService = taskService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductInstance upgrade(String vdc, Long id, String version)
            throws MaxTimeWaitingExceedException, InvalidExecutionException {
        Task task = productInstanceService.upgrade(vdc, id, version, null);
        return this.waitForTask(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductInstance configure(String vdc, Long id,
            List<Attribute> arguments)
            throws MaxTimeWaitingExceedException, InvalidExecutionException {
        Task task = productInstanceService.configure(vdc, id, null, arguments);
        return this.waitForTask(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductInstance uninstall(String vdc, Long id)
            throws MaxTimeWaitingExceedException, InvalidExecutionException {
        Task task = productInstanceService.uninstall(vdc, id, null);
        return this.waitForTask(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductInstance load(String url) throws ResourceNotFoundException {
        return productInstanceService.load(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductInstance install(String vdc, ProductInstanceDto product)
            throws MaxTimeWaitingExceedException, InvalidExecutionException {
        Task task = productInstanceService.install(vdc, product, null);
        return this.waitForTask(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductInstance> findAll(String hostname, String domain,
            String ip, String fqn, Integer page, Integer pageSize, String orderBy,
            String orderType, Status status, String vdc, String productName) {
        return productInstanceService.findAll(hostname, domain, ip, fqn, page,
                pageSize, orderBy, orderType, status, vdc, productName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductInstance load(String vdc, Long id)
            throws ResourceNotFoundException {
        return productInstanceService.load(vdc, id);
    }

    private ProductInstance waitForTask(Task task)
            throws MaxTimeWaitingExceedException, InvalidExecutionException {
        task = taskService.waitForTask(task.getHref());
        if (!task.getStatus().equals(TaskStates.SUCCESS)) {
            throw new InvalidExecutionException(task.getError().getMessage());
        }
        try {
            return productInstanceService.load(task.getResult().getHref());
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}