__author__ = 'arobres'

# -*- coding: utf-8 -*-
from lettuce import step, world, before, after
from commons.authentication import get_token
from commons.rest_utils import RestUtils
from commons.product_body import simple_product_body, product_with_attributes, product_with_metadata, \
    product_with_all_parameters
from commons.utils import dict_to_xml, xml_to_dict, set_default_headers
from commons.constants import CONTENT_TYPE, CONTENT_TYPE_JSON, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT, \
    ACCEPT_HEADER, LONG_ID, AUTH_TOKEN_HEADER, PRODUCT_ATTRIBUTES, PRODUCT_METADATAS, KEY, DESCRIPTION, VALUE
from nose.tools import assert_equals, assert_true, assert_in

api_utils = RestUtils()

@before.each_feature
def setup_feature(feature):

    world.token_id, world.tenant_id = get_token()


@before.each_scenario
def setup_scenario(scenario):

    world.headers = set_default_headers(world.token_id, world.tenant_id)
    api_utils.delete_all_testing_products(world.headers)
    world.attributes = None
    world.metadatas = None


@step(u'Given a product with name "([^"]*)" with description "([^"]*)"')
def given_a_product_with_name_group1_with_description_group2(step, product_name, product_description):

    if product_name == 'LONG_ID':
        world.product_name = LONG_ID
    else:
        world.product_name = product_name

    world.product_description = product_description


@step(u'When I add the new product with accept parameter "([^"]*)" response')
def when_i_add_the_new_product_with_group1_response(step, accept_content):

    world.headers[ACCEPT_HEADER] = accept_content
    body = dict_to_xml(simple_product_body(name=world.product_name, description=world.product_description))
    world.response = api_utils.add_new_product(headers=world.headers, body=body)


@step(u'Then the product is created')
def then_the_product_is_created_with_group1_response(step):

    assert_true(world.response.ok, 'RESPONSE: {}'.format(world.response.content))
    response_headers = world.response.headers

    if response_headers[CONTENT_TYPE] == CONTENT_TYPE_JSON:
        try:
            response_body = world.response.json()
        except Exception, e:
            print str(e)

    else:
        response_body = xml_to_dict(world.response.content)[PRODUCT]

    assert_equals(response_body[PRODUCT_NAME], world.product_name)
    assert_equals(response_body[PRODUCT_DESCRIPTION], world.product_description)

    if world.attributes is not None:
        if len(world.attributes) == 1:
            assert_equals(world.attributes[0], response_body[PRODUCT_ATTRIBUTES])
        else:
            assert_equals(world.attributes, response_body[PRODUCT_ATTRIBUTES])
        world.attributes = None

    if world.metadatas is not None:
        for metadata in world.metadatas:
            assert_in(metadata, response_body[PRODUCT_METADATAS])
        world.metadatas = None

@step(u'And the following attributes')
def and_the_following_attributes(step):

    world.attributes = []

    for examples in step.hashes:
        attribute = {}
        attribute['key'] = examples['key']
        attribute['description'] = examples['description']
        attribute['value'] = examples['value']
        world.attributes.append(attribute)


@step(u'When I add the new product with attributes, with accept parameter "([^"]*)" response')
def when_i_add_the_new_product_with_attributes_with_accept_parameter_group1_response(step, accept_content):
    world.headers[ACCEPT_HEADER] = accept_content

    body = dict_to_xml(product_with_attributes(name=world.product_name, description=world.product_description,
                                               attributes=world.attributes))
    world.response = api_utils.add_new_product(headers=world.headers, body=body)


@step(u'And the following metadatas')
def and_the_following_metadatas(step):

    world.metadatas = []

    for examples in step.hashes:
        metadata = {}
        metadata[KEY] = examples[KEY]
        metadata[VALUE] = examples[VALUE]
        if examples[DESCRIPTION] != 'None':
            metadata[DESCRIPTION] = examples[DESCRIPTION]

        world.metadatas.append(metadata)


@step(u'When I add the new product with metadatas, with accept parameter "([^"]*)" response')
def when_i_add_the_new_product_with_metadatas_with_accept_parameter_group1_response(step, accept_content):

    world.headers[ACCEPT_HEADER] = accept_content

    body = dict_to_xml(product_with_metadata(name=world.product_name, description=world.product_description,
                                             metadata=world.metadatas))
    world.response = api_utils.add_new_product(headers=world.headers, body=body)


@step(u'When I add the new product with all the parameters, with accept parameter "([^"]*)" response')
def when_i_add_the_new_product_with_all_the_parameters_with_accept_parameter_group1_response(step, accept_content):

    world.headers[ACCEPT_HEADER] = accept_content

    body = dict_to_xml(product_with_all_parameters(name=world.product_name, description=world.product_description,
                                                   metadata=world.metadatas, attributes=world.attributes))

    world.response = api_utils.add_new_product(headers=world.headers, body=body)


@step(u'Then I obtain an "([^"]*)"')
def then_i_obtain_an_group1(step, error_code):

    print assert_equals(str(world.response.status_code), error_code)


@step(u'And incorrect "([^"]*)" header')
def and_incorrect_content_type_header(step, content_type):
    world.headers[CONTENT_TYPE] = content_type


@step(u'And incorrect "([^"]*)" authentication')
def incorrect_token(step, new_token):
    world.headers[AUTH_TOKEN_HEADER] = new_token


@after.all
def tear_down(scenario):

    world.token_id, world.tenant_id = get_token()
    world.headers = set_default_headers(world.token_id, world.tenant_id)
    api_utils.delete_all_testing_products(world.headers)
