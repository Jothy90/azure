package com.mycompany.app;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.MessageCallback;
import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


public class App {
    //private static String connString = "HostName=senzmate-test-hub.azure-devices.net;DeviceId=test-1-device-1;SharedAccessKey=1QzCJmAY1MDCHDCQKPAjpw==";
    private static String connString = "HostName=jkcs-cg-iotadb55.azure-devices.net;DeviceId=SenzMate-DEV-C;SharedAccessKey=HKQFwb7o8uPppcKyTUqlzRPjad7lQqjcKYAbHytSgB8=";
    private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
    private static String deviceId = "SenzMate-DEV-C";
    //private static String deviceId = "test-1-device-1";
    private static DeviceClient client;

    public static void main( String[] args ) throws IOException, URISyntaxException {
        client = new DeviceClient(connString, protocol);
        client.open();

        MessageSender sender = new MessageSender();

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(sender);

        System.out.println("Press ENTER to exit.");
        System.in.read();
        executor.shutdownNow();
        client.close();
    }

    private static class TelemetryDataPoint {
        public String deviceId;
        public double temperature;
        public double humidity;

        public String serialize() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }

    private static class EventCallback implements IotHubEventCallback
    {
        public void execute(IotHubStatusCode status, Object context) {
            System.out.println("IoT Hub responded to message with status: " + status.name());

            if (context != null) {
                synchronized (context) {
                    context.notify();
                }
            }
        }
    }

    private static class MessageSender implements Runnable {

        public void run()  {
            try {
                double minTemperature = 20;
                double minHumidity = 60;
                Random rand = new Random();

                while (true) {
                    String msgStr;
                    Message msg;
                    if (new Random().nextDouble() > 0.7) {
                        msgStr = "This is a critical message.";
                        msg = new Message(msgStr);
                        msg.setProperty("level", "critical");
                    } else {
                        double currentTemperature = minTemperature + rand.nextDouble() * 15;
                        double currentHumidity = minHumidity + rand.nextDouble() * 20;
                        TelemetryDataPoint telemetryDataPoint = new TelemetryDataPoint();
                        telemetryDataPoint.deviceId = deviceId;
                        telemetryDataPoint.temperature = currentTemperature;
                        telemetryDataPoint.humidity = currentHumidity;

                        msgStr = telemetryDataPoint.serialize();
                        msg = new Message(msgStr);
                    }

                    System.out.println("Sending: " + msgStr);

                    Object lockobj = new Object();
                    EventCallback callback = new EventCallback();
                    client.sendEventAsync(msg, callback, lockobj);

                    synchronized (lockobj) {
                        lockobj.wait();
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                System.out.println("Finished.");
            }
        }
    }


}
