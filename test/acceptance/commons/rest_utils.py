__author__ = 'arobres'

from json import JSONEncoder
from configuration import SDC_IP, SDC_PORT
from constants import CONTENT_TYPE, CONTENT_TYPE_JSON, PRODUCT, PRODUCT_NAME, VERSION, PRODUCT_RELEASE

import requests

SDC_SERVER = 'http://{}:{}'.format(SDC_IP, SDC_PORT)
PRODUCT_PATTERN_ROOT = '{url_root}/sdc/rest/catalog/product/'
PRODUCT_PATTERN = '{url_root}/sdc/rest/catalog/product/{product_id}'
PRODUCT_RELEASE_PATTERN = '{url_root}/sdc/rest/catalog/product/{product_id}/release'
VERSION_RELEASE_PATTERN = '{url_root}/sdc/rest/catalog/product/{product_id}/release/{version}'
PRODUCT_ATTRIBUTES_PATTERN = '{url_root}/sdc/rest/catalog/product/{product_id}/attributes'
PRODUCT_METADATA_PATTERN = '{url_root}/sdc/rest/catalog/product/{product_id}/metadatas'


class RestUtils(object):

    def __init__(self):
        """Initialization method
        """

        self.api_url = SDC_SERVER
        self.encoder = JSONEncoder()

    def _call_api(self, pattern, method, body=None, headers=None, payload=None, **kwargs):

        """Launch HTTP request to Policy Manager API with given arguments
        :param pattern: string pattern of API url with keyword arguments (format string syntax)
        :param method: HTTP method to execute (string)
        :param body: JSON body content (dict)
        :param headers: HTTP header request (dict)
        :param payload: Query parameters for the URL
        :param **kwargs: URL parameters (without url_root) to fill the patters
        :returns: REST API response
        """

        kwargs['url_root'] = self.api_url

        url = pattern.format(**kwargs)

        print 'METHOD: {}\nURL: {} \nHEADERS: {} \nBODY: {}'.format(method, url, headers, self.encoder.encode(body))

        try:
            if headers[CONTENT_TYPE] == CONTENT_TYPE_JSON:
                r = requests.request(method=method, url=url, data=self.encoder.encode(body), headers=headers,
                                     params=payload)
            else:
                r = requests.request(method=method, url=url, data=body, headers=headers, params=payload)

        except Exception, e:
            print "Request {} to {} crashed: {}".format(method, url, str(e))
            return None

        return r

    def add_new_product(self, headers=None, body=None):

        return self._call_api(pattern=PRODUCT_PATTERN_ROOT, method='post', headers=headers, body=body)

    def retrieve_product(self, headers=None, product_id=None):

        return self._call_api(pattern=PRODUCT_PATTERN, method='get', headers=headers, product_id=product_id)

    def retrieve_product_attributes(self, headers=None, product_id=None):

        return self._call_api(pattern=PRODUCT_ATTRIBUTES_PATTERN, method='get', headers=headers, product_id=product_id)

    def retrieve_product_metadatas(self, headers=None, product_id=None):

        return self._call_api(pattern=PRODUCT_METADATA_PATTERN, method='get', headers=headers, product_id=product_id)

    def delete_product(self, headers=None, product_id=None):

        return self._call_api(pattern=PRODUCT_PATTERN, method='delete', headers=headers, product_id=product_id)

    def retrieve_product_list(self, headers=None):

        return self._call_api(pattern=PRODUCT_PATTERN_ROOT, method='get', headers=headers)

    def add_product_release(self, headers=None, body=None, product_id=None):

        return self._call_api(pattern=PRODUCT_RELEASE_PATTERN, method='post', headers=headers, product_id=product_id,
                              body=body)

    def delete_product_release(self, headers=None, product_id=None, version=None):

        return self._call_api(pattern=VERSION_RELEASE_PATTERN, method='delete', headers=headers, product_id=product_id,
                              version=version)

    def retrieve_product_release_information(self, headers=None, product_id=None, version=None):

        return self._call_api(pattern=VERSION_RELEASE_PATTERN, method='get', headers=headers, product_id=product_id,
                              version=version)

    def retrieve_product_release_list(self, headers=None, product_id=None):

        return self._call_api(pattern=PRODUCT_RELEASE_PATTERN, method='get', headers=headers, product_id=product_id)

    def delete_all_testing_products(self, headers=None):

        response = self.retrieve_product_list(headers=headers)
        assert response.ok
        for product in response.json()[PRODUCT]:
            if 'testing' in product[PRODUCT_NAME]:

                delete_response = self.delete_product(headers=headers, product_id=product[PRODUCT_NAME])

                if not delete_response.ok:
                    release_list = self.retrieve_product_release_list(headers=headers, product_id=product[PRODUCT_NAME])
                    release_list = release_list.json()
                    delete_release = self.delete_product_release(headers=headers, product_id=product[PRODUCT_NAME],
                                                                 version=release_list[PRODUCT_RELEASE][VERSION])
                    assert delete_release.ok
                    delete_response = self.delete_product(headers=headers, product_id=product[PRODUCT_NAME])
                    assert delete_response.ok
