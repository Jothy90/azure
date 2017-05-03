package com.mycompany.app;

import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.*;
import com.microsoft.windowsazure.services.servicebus.models.*;
import com.microsoft.windowsazure.core.*;
import javax.xml.datatype.*;

public class App 
{
    public static void main( String[] args )
    {
        Configuration config =
                ServiceBusConfiguration.configureWithSASAuthentication(
                        "HowToSample",
                        "RootManageSharedAccessKey",
                        "RJ6Ra5m2D+lXxGTaPBoVdiHuzMcnYbWbEkkbPygw+68=",
                        ".servicebus.windows.net"
                );

        ServiceBusContract service = ServiceBusService.create(config);
        QueueInfo queueInfo = new QueueInfo("TestQueue");
        try
        {
            CreateQueueResult result = service.createQueue(queueInfo);
        }
        catch (ServiceException e)
        {
            System.out.print("ServiceException encountered: ");
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
}
