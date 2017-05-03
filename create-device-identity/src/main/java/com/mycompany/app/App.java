package com.mycompany.app;

import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import com.microsoft.azure.sdk.iot.service.Device;
import com.microsoft.azure.sdk.iot.service.RegistryManager;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final String connectionString = "HostName=senzmate-test-hub.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=C501zOS2GC3FO7cCrDsWT9pCu+aDXf3+a+OkEfa1ePw=";
    private static final String deviceId = "test-1-device-1";
    public static void main( String[] args ) throws IOException, URISyntaxException, Exception
    {
        RegistryManager registryManager = RegistryManager.createFromConnectionString(connectionString);

        Device device = Device.createFromId(deviceId, null, null);
        try {
            device = registryManager.addDevice(device);
        } catch (IotHubException iote) {
            try {
                device = registryManager.getDevice(deviceId);
            } catch (IotHubException iotf) {
                iotf.printStackTrace();
            }
        }
        System.out.println("Device ID: " + device.getDeviceId());
        System.out.println("Device key: " + device.getPrimaryKey());
    }
}
