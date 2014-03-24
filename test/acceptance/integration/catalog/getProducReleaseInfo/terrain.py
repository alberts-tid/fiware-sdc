# -*- coding: utf-8 -*-
from lettuce import world, after, before
from tools import catalogue_request
from tools.catalogue_request import CatalogueRequest


@before.each_scenario
def before_each_scenario(scenario):
    world.env_requests = CatalogueRequest(world.config['paas']['keystone_url'],
                                            world.config['paas']['paasmanager_url'],
                                            world.config['paas']['tenant'],
                                            world.config['paas']['user'],
                                            world.config['paas']['password'],
                                            world.config['paas']['vdc'],
                                            world.config['paas']['sdc_url'])
NOT_ERROR = ""

@after.each_scenario
def after_each_scenario(scenario):
    #Delete the product release and the product created in the catalogue.
    world.env_requests.catalogue_deleteProduct(world.product, NOT_ERROR)
    pass
