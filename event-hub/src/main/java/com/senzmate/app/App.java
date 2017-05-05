package com.senzmate.app;

import java.io.IOException;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.microsoft.azure.eventhubs.*;
import com.microsoft.azure.servicebus.*;

public class App {
    public static void main(String[] args)
            throws ServiceBusException, ExecutionException, InterruptedException, IOException {
        final String namespaceName = "senzmate-event-test.servicebus.windows.net/";
        final String eventHubName = "eventhubdemo";
        final String sasKeyName = "RootManageSharedAccessKey";
        final String sasKey = "Vjd9tj+UqJ9P2D4R4yK5GcLfwS0Ej5hexf7lfjiywls=";
        ConnectionStringBuilder connStr = new ConnectionStringBuilder(namespaceName, eventHubName, sasKeyName, sasKey);

        byte[] payloadBytes = "Test AMQP message from JMS".getBytes("UTF-8");
        EventData sendEvent = new EventData(payloadBytes);

        EventHubClient ehClient = EventHubClient.createFromConnectionStringSync(connStr.toString());
        ehClient.sendSync(sendEvent);
    }
}
