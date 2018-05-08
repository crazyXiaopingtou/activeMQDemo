package yunqu.activeMQDemo;

import yunqu.activeMQDemo.mqclient.ProducerBroker;

import javax.jms.JMSException;

public class TestProducerBroker {

    public static void main(String[] args) throws Exception {
       ProducerBroker broker = new ProducerBroker();
        int i= 0;
        while (true){
            i++;
            broker.sendMessage("message"+i);
            Thread.sleep(3*1000);
        }

    }
}
