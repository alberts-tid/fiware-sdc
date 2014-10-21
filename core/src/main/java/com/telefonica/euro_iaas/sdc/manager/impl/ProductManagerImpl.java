/**
 * Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U <br>
 * This file is part of FI-WARE project.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 * </p>
 * <p>
 * You may obtain a copy of the License at:<br>
 * <br>
 * http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * </p>
 * <p>
 * See the License for the specific language governing permissions and limitations under the License.
 * </p>
 * <p>
 * For those usages not covered by the Apache version 2.0 License please contact with opensource@tid.es
 * </p>
 */

package com.telefonica.euro_iaas.sdc.manager.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.sdc.dao.ProductDao;
import com.telefonica.euro_iaas.sdc.manager.ProductManager;
import com.telefonica.euro_iaas.sdc.manager.ProductReleaseManager;
import com.telefonica.euro_iaas.sdc.model.Metadata;
import com.telefonica.euro_iaas.sdc.model.Product;
import com.telefonica.euro_iaas.sdc.model.ProductRelease;
import com.telefonica.euro_iaas.sdc.model.dto.ProductAndReleaseDto;
import com.telefonica.euro_iaas.sdc.model.searchcriteria.ProductReleaseSearchCriteria;
import com.telefonica.euro_iaas.sdc.model.searchcriteria.ProductSearchCriteria;
import com.xmlsolutions.annotation.UseCase;

/**
 * Default ProductManager implementation.
 * 
 * @author Sergio Arroyo, Jesus M. Movilla
 */
@UseCase(traceTo = "UC_101", status = "partially implemented")
public class ProductManagerImpl extends BaseInstallableManager implements ProductManager {

    private ProductDao productDao;
    private ProductReleaseManager productReleaseManager;
    private static Logger log = Logger.getLogger("ProductManagerImpl");

    public Product insert(Product product, String tenantId) throws AlreadyExistsEntityException, InvalidEntityException {

        Product productOut;
        try {
            productOut = productDao.load(product.getName());
            log.log(Level.INFO, "Product " + productOut.getName() + " LOADED");
        } catch (EntityNotFoundException e) {

            List<Metadata> metadatas = new ArrayList<Metadata>();
            metadatas.add(new Metadata("image", "df44f62d-9d66-4dc5-b084-2d6c7bc4cfe4")); // centos6.3_sdc
            metadatas.add(new Metadata("cookbook_url", ""));
            metadatas.add(new Metadata("cloud", "yes"));
            metadatas.add(new Metadata("installator", "chef"));
            metadatas.add(new Metadata("open_ports", "80 22"));
            metadatas.add(new Metadata("tenant_id", tenantId));

            List<Metadata> defaultmetadatas = new ArrayList<Metadata>();
            defaultmetadatas.add(new Metadata("image", "df44f62d-9d66-4dc5-b084-2d6c7bc4cfe4"));
            defaultmetadatas.add(new Metadata("cookbook_url", ""));
            defaultmetadatas.add(new Metadata("cloud", "yes"));
            defaultmetadatas.add(new Metadata("installator", "chef"));
            defaultmetadatas.add(new Metadata("open_ports", "80 22"));
            defaultmetadatas.add(new Metadata("tenant_id", tenantId));

            for (Metadata external_metadata : product.getMetadatas()) {
                boolean defaultmetadata = false;
                for (Metadata default_metadata : defaultmetadatas) {
                    if (external_metadata.getKey().equals(default_metadata.getKey())) {
                        metadatas.get(metadatas.indexOf(default_metadata)).setValue(external_metadata.getValue());
                        defaultmetadata = true;
                    }
                }
                if (!defaultmetadata) {
                    metadatas.add(external_metadata);
                }
            }
            product.setMetadatas(metadatas);
            productOut = productDao.create(product);
        }
        return productOut;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public List<Product> findAll() {
        return productDao.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findByCriteria(ProductSearchCriteria criteria) {
        return productDao.findByCriteria(criteria);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductAndReleaseDto> findProductAndReleaseByCriteria(ProductSearchCriteria criteria) {
        List<Product> productList = productDao.findByCriteria(criteria);

        ProductReleaseSearchCriteria prCriteria = new ProductReleaseSearchCriteria();

        prCriteria.setPage(criteria.getPage());
        prCriteria.setPageSize(criteria.getPageSize());
        prCriteria.setOrderBy(criteria.getOrderBy());
        prCriteria.setOrderType(criteria.getOrderType());

        List<ProductAndReleaseDto> result = new ArrayList<ProductAndReleaseDto>();
        for (Product p : productList) {
            if (!StringUtils.isEmpty(p.getName())) {
                prCriteria.setProduct(p);
                List<ProductRelease> productReleaseList = productReleaseManager.findReleasesByCriteria(prCriteria);

                for (ProductRelease pr : productReleaseList) {

                    ProductAndReleaseDto productAndRelease = new ProductAndReleaseDto();
                    productAndRelease.setProduct(p);
                    productAndRelease.setVersion(pr.getVersion());

                    result.add(productAndRelease);
                }
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Product load(String name) throws EntityNotFoundException {
        return productDao.load(name);
    }

    public boolean exist(String name) {
        try {
            load(name);
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }

    @Override
    public void delete(Product product) {
        productDao.remove(product);
    }

    /**
     * @param productDao
     *            the productDao to set
     */
    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    public void setProductReleaseManager(ProductReleaseManager productReleaseManager) {
        this.productReleaseManager = productReleaseManager;
    }

}
