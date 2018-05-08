package yunqu.activeMQDemo.mqclient.message;

import org.apache.activemq.BlobMessage;
import yunqu.activeMQDemo.log.ActiveMQlogger;
import yunqu.activeMQDemo.util.CommonUtil;

import javax.jms.*;

/**
 * 消息处理
 */
public class ConsumerMessage implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            if(message instanceof TextMessage){
                TextMessage textMessage = (TextMessage) message;
                ActiveMQlogger.logger.info (CommonUtil.getClassNameAndMethod(this)+"<<<<<<<messageType:TextMessage,content:" + textMessage.getText() );
            }else if(message instanceof MapMessage){
                MapMessage mapMessage = (MapMessage) message;
                ActiveMQlogger.logger.info (CommonUtil.getClassNameAndMethod(this)+"<<<<<<<messageType:MapMessage,content:" + mapMessage.toString());
            }else if(message instanceof ObjectMessage) {
                ObjectMessage objectMessage = (ObjectMessage) message;
                ActiveMQlogger.logger.info (CommonUtil.getClassNameAndMethod(this)+"<<<<<<<messageType:ObjectMessage,content:" + objectMessage.toString());
            }else if(message instanceof BlobMessage){
                BlobMessage blobMessage = (BlobMessage) message;
                ActiveMQlogger.logger.info (CommonUtil.getClassNameAndMethod(this)+"<<<<<<<messageType:BlobMessage,content:" + blobMessage.toString());
            }else if(message instanceof  BytesMessage){
                BytesMessage bytesMessage = (BytesMessage) message;
                ActiveMQlogger.logger.info(CommonUtil.getClassNameAndMethod(this) + "<<<<<<<messageType:BytesMessage,content:" + bytesMessage.toString());
            }else if(message instanceof StreamMessage){
                StreamMessage streamMessage = (StreamMessage) message;
                ActiveMQlogger.logger.info(CommonUtil.getClassNameAndMethod(this) + "<<<<<<<messageType:StreamMessage,content:" + streamMessage.toString());
            }
        } catch (JMSException e) {
            e.printStackTrace();
            ActiveMQlogger.logger.error("error {}", e);
        }
    }

}
