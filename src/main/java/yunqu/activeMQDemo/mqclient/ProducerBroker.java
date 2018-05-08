package yunqu.activeMQDemo.mqclient;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.omg.PortableInterceptor.ACTIVE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yunqu.activeMQDemo.base.Constants;
import yunqu.activeMQDemo.log.ActiveMQlogger;
import yunqu.activeMQDemo.util.CommonUtil;

/**
 * activemq 消息生产者
 * @author ljp
 *
 */
public class ProducerBroker {
	private static final String BROKER_URL = ActiveMQConnection.DEFAULT_BROKER_URL;
	private static ConnectionFactory connectionFactory;
	private static Connection conn;
	private static Session session;
	private static Destination dest;
	private static MessageProducer producer;
	private String queueName = Constants.QUEUE_NAME;

	public ProducerBroker(){

	}
	public ProducerBroker(String queueName){
		if(null != queueName && !"".equals(queueName)){
			this.queueName = queueName;
		}
	}

	private void init(){
		try {
			//初始化连接工厂
			connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
			//获得连接
			conn = connectionFactory.createConnection();
			//启动连接
			conn.start();
			//创建Session，此方法第一个参数表示会话是否在事务中执行，第二个参数设定会话的应答模式
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			//创建队列
			dest = session.createQueue(this.queueName);
			//通过session可以创建消息的生产者
			producer = session.createProducer(dest);
		}catch (JMSException e){
			e.printStackTrace();
			ActiveMQlogger.logger.error("init ProducerBroker erro:"+e.getMessage());
		}
	}
	/**
	 *  发送消息
	 * @param msg
	 */
	public void sendMessage(String msg) throws JMSException{
		//初始化mq消息
//		MapMessage mapMsg1 = session.createMapMessage();
//		mapMsg1.setString("name", "xiaoming");
//		mapMsg1.setIntProperty("age", 18);
		if(producer == null){
			init();
		}
		TextMessage textMessage = session.createTextMessage();
		textMessage.setText(msg);
		//发送消息
		producer.send(textMessage, DeliveryMode.NON_PERSISTENT, 4, 10*60*1000L);
		ActiveMQlogger.logger.info(CommonUtil.getClassNameAndMethod(new ProducerBroker())+",send massege{}>>>>>>");
	}

	/**
	 * 关闭连接
	 */
	public void closeConn(){
		try {
			if(producer != null){
				conn.close();
				session.close();
				producer.close();
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ProducerBroker producerBroker = new ProducerBroker("test-queue");
		try {
			producerBroker.sendMessage("你好！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
