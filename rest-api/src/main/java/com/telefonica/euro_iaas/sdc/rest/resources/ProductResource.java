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

package com.telefonica.euro_iaas.sdc.rest.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.sdc.exception.ProductReleaseNotFoundException;
import com.telefonica.euro_iaas.sdc.exception.ProductReleaseStillInstalledException;
import com.telefonica.euro_iaas.sdc.model.Attribute;
import com.telefonica.euro_iaas.sdc.model.Metadata;
import com.telefonica.euro_iaas.sdc.model.Product;
import com.telefonica.euro_iaas.sdc.rest.exception.APIException;

/**
 * Provides a rest api to works with Product.
 * 
 * @author Sergio Arroyo
 */
public interface ProductResource {

    /**
     * Insert a product int SDC Database.
     * 
     * @throws AlreadyExistsEntityException
     * @throws InvalidEntityException
     * @param product
     * @return product
     */
    @POST
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    Product insert(Product product) throws APIException;

    /**
     * Retrieve all Products available created in the system.
     * 
     * @param page
     *            for pagination is 0 based number(<i>nullable</i>)
     * @param pageSize
     *            for pagination, the number of items retrieved in a query (<i>nullable</i>)
     * @param orderBy
     *            the file to order the search (id by default <i>nullable</i>)
     * @param orderType
     *            defines if the order is ascending or descending (asc by default <i>nullable</i>)
     * @return the created OS instances.
     */
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    List<Product> findAll(@QueryParam("page") Integer page, @QueryParam("pageSize") Integer pageSize,
            @QueryParam("orderBy") String orderBy, @QueryParam("orderType") String orderType);

    /**
     * Retrieve the selected Product.
     * 
     * @param name
     *            the product name
     * @return the product.
     * @throws EntityNotFoundException
     *             if the product does not exists
     */
    @GET
    @Path("/{pName}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    Product load(@PathParam("pName") String name) throws EntityNotFoundException;

    /**
     * Retrieve the common attributes for the selected product.
     * 
     * @param name
     *            the product name
     * @return the attributes.
     * @throws EntityNotFoundException
     *             if the product does not exists
     */
    @GET
    @Path("/{pName}/attributes/")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    List<Attribute> loadAttributes(@PathParam("pName") String name) throws EntityNotFoundException;

    /**
     * Retrieve the metadatas for the selected product.
     * 
     * @param name
     *            the product name
     * @return the attributes.
     * @throws EntityNotFoundException
     *             if the product does not exists
     */
    @GET
    @Path("/{pName}/metadatas/")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    List<Metadata> loadMetadatas(@PathParam("pName") String name) throws EntityNotFoundException;

    /**
     * Delete the Product in SDC Database.
     * 
     * @param name
     * @throws ProductReleaseNotFoundException
     * @throws ProductReleaseStillInstalledException
     */
    @DELETE
    @Path("/{pName}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    void delete(@PathParam("pName") String name) throws ProductReleaseNotFoundException,
            ProductReleaseStillInstalledException;
}
