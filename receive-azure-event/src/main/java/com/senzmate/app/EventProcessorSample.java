package com.senzmate.app;

import com.microsoft.azure.eventprocessorhost.*;
import com.microsoft.azure.servicebus.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;

import java.util.concurrent.ExecutionException;

public class EventProcessorSample
{
    public static void main(String args[])
    {
        final String consumerGroupName = "$Default";
        final String namespaceName = "senzmate-event-test.servicebus.windows.net/";
        final String eventHubName = "eventhubdemo";
        final String sasKeyName = "RootManageSharedAccessKey";
        final String sasKey = "Vjd9tj+UqJ9P2D4R4yK5GcLfwS0Ej5hexf7lfjiywls=";

        final String storageAccountName = "senz";
        final String storageAccountKey = "2ha4P58Yeb7Zx1BWp2oiC+wEWXWiMEBsyI09rZ5CoKd11IIEZhBTFOR5/RLaB0vxK+0l0JJ7pR8waw48y0R2Rg==";
        final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=" + storageAccountName + ";AccountKey=" + storageAccountKey;

        ConnectionStringBuilder eventHubConnectionString = new ConnectionStringBuilder(namespaceName, eventHubName, sasKeyName, sasKey);

        EventProcessorHost host = new EventProcessorHost(eventHubName, consumerGroupName, eventHubConnectionString.toString(), storageConnectionString);

        System.out.println("Registering host named " + host.getHostName());
        EventProcessorOptions options = new EventProcessorOptions();
        options.setExceptionNotification(new ErrorNotificationHandler());
        try
        {
            host.registerEventProcessor(EventProcessor.class, options).get();
        }
        catch (Exception e)
        {
            System.out.print("Failure while registering: ");
            if (e instanceof ExecutionException)
            {
                Throwable inner = e.getCause();
                System.out.println(inner.toString());
            }
            else
            {
                System.out.println(e.toString());
            }
        }

        System.out.println("Press enter to stop");
        try
        {
            System.in.read();
            host.unregisterEventProcessor();

            System.out.println("Calling forceExecutorShutdown");
            EventProcessorHost.forceExecutorShutdown(120);
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
            e.printStackTrace();
        }

        System.out.println("End of sample");
    }
}