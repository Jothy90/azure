/**
 * Created by john on 4/12/15.
 */


import org.eclipse.paho.client.mqttv3.*;

public class App
        implements MqttCallback {


    static MqttClient myClient;
    MqttConnectOptions connOpt;

    static final String BROKER_URL = "tcp://52.41.35.110:1883"; //"wss://senzmate-test-hub.azure-devices.net/$iothub/websocket:443";
    static final int SUB_QOS = 0;
    static final int PUB_QOS = 0;

    public static void main(String[] args) {
        App app = new App();
        app.runClient();
    }

    /**
     * connectionLost
     * This callback is invoked upon losing the MQTT connection.
     */
    @Override
    public void connectionLost(Throwable t) {
        System.out.println("MQTT Connection lost!");
        runClient();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        try {
            String message = new String(mqttMessage.getPayload());
            System.out.println("-------------------------------------------------");
            System.out.println("| Topic:" + topic); //SenzMate/D2S/{Device_Id}
            String[] messageDetails = topic.split("/");
            System.out.println("| Device:" + messageDetails[2]);
            System.out.println("| Message: " + message);
            System.out.println("-------------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    //Send Alert via MQTT Protocols
    public String sendAlert(String atMessage) {
        String dtResp = null;
        MqttMessage mqttMessage = null;
        mqttMessage = new MqttMessage(atMessage.getBytes());
        mqttMessage.setQos(PUB_QOS);
        mqttMessage.setRetained(false);

        String myTopic = "devices/test-1-device-1/messages/events/";
        MqttTopic mqttTopic = myClient.getTopic(myTopic);

        // Publish the message
        System.out.println("Publishing to topic \"" + mqttTopic + "\" qos " + PUB_QOS);

        try {
            // publish message to broker
            mqttTopic.publish(mqttMessage);
        } catch (Exception e) {
            System.out.println("Exception occurred in Perform publish(DTMessage) when publish" + e);
            return dtResp;
        }
        dtResp = "Success";

        return dtResp;
    }


    public void runClient() {
        // setup MQTT Client
        String clientID = "test-1-device-1";
        System.out.println(clientID);
        connOpt = new MqttConnectOptions();

        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(60);
        connOpt.setUserName("ubidev");
        connOpt.setPassword("senzmate".toCharArray());

        // Connect to Broker
        try {
            myClient = new MqttClient(BROKER_URL, clientID);
            myClient.connect(connOpt);
            myClient.setCallback(this);
        } catch (MqttException e) {
            System.out.println("Exception occurred in Perform runClient() Callback" + e);
            System.exit(-1);
        }

        System.out.println("Connected to " + BROKER_URL);
        try {
            myClient.subscribe("SenzMate/#", SUB_QOS);
        } catch (Exception e) {
            System.out.println("Exception occurred in Perform runClient() Subscribe" + e);
        }
    }
}