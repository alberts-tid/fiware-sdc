package com.telefonica.euro_iaas.sdc.util;

import static com.telefonica.euro_iaas.sdc.util.SystemPropertiesProvider.CHEF_DATE_FORMAT;
import static com.telefonica.euro_iaas.sdc.util.SystemPropertiesProvider.CHEF_TIME_ZONE;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Date;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for MixlibAuthenticationDigester
 *
 * @author Sergio Arroyo
 *
 */
public class MixlibAuthenticationDigesterTest {

    private SystemPropertiesProvider propertiesProvider;
    private Signer signer;

    @Before
    public void setUp() {
        propertiesProvider = mock(SystemPropertiesProvider.class);
        when(propertiesProvider.getProperty(CHEF_DATE_FORMAT)).thenReturn(
            "yyyy-MM-dd'T'HH:mm:ss'Z'");
        when(propertiesProvider.getProperty(CHEF_TIME_ZONE))
            .thenReturn("GMT");

        signer = mock(Signer.class);
        when(signer.sign(anyString(), (File) anyObject())).thenReturn("blahblahblah");
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testDigest() throws Exception {
        MixlibAuthenticationDigesterImpl digester = new MixlibAuthenticationDigesterImpl();
        digester.setPropertiesProvider(propertiesProvider);
        digester.setSigner(signer);

        Map<String, String> headers = digester.digest(
                "GET", "/nodes", "", new Date(), "serch",
                this.getClass().getResource("/private.pem").getPath());
        //make assertions
        Assert.assertEquals(5, headers.size());


        verify(propertiesProvider, times(1)).getProperty(CHEF_DATE_FORMAT);
        verify(propertiesProvider, times(1)).getProperty(CHEF_TIME_ZONE);


    }


}