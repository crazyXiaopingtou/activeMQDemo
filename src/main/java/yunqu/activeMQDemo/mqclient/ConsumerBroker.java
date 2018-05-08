package yunqu.activeMQDemo.mqclient;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import yunqu.activeMQDemo.base.Constants;
import yunqu.activeMQDemo.listener.GolbalThreadState;
import yunqu.activeMQDemo.log.ActiveMQlogger;
import yunqu.activeMQDemo.util.CommonUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 消费者线程
 */
public class ConsumerBroker implements Runnable {

    /**
     * mq服务器地址
     */
	public static final String BROKER_URL = ActiveMQConnection.DEFAULT_BROKER_URL;

    /**
     * 消息消费对象
     */
	private MessageConsumer consumer;

	private boolean isRunning = false;
	private BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();

    /**
     * 队列名称
     */
	private String queueName ;

	private ThreadGroup threadGroup;

	private MessageListener messageListener;

	private GolbalThreadState threadsState;

	public ConsumerBroker(String queueName,MessageListener messageListener,GolbalThreadState threadsState){
		this.queueName = queueName;
		this.messageListener = messageListener;
		this.threadsState = threadsState;
	}

	public  void closeBroker(){
		try {
			if(this.consumer!=null){
				this.consumer.close();
			}
		} catch (Exception ex) {
			ActiveMQlogger.logger.error(ex.getMessage());
		}
		this.consumer = null;
	}


	@Override
	public void run() {
		//启动的时候等待系统启动完成
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.initThread();

		MessageConsumer consumer = null;

		ActiveMQlogger.getLogger().info("threadsState.isRunning() << "+ threadsState.isRunning());

		while(threadsState.isRunning()){
			if(!isRunning){
				try {
					if(consumer==null){
						consumer =  this.getConsumer();
					}
					isRunning = true;
				} catch (Exception ex) {
					ActiveMQlogger.logger.info("ConsumerBroker.run() error,cause:"+ex.getMessage(),ex);
					ex.printStackTrace();
					isRunning = false;
					this.closeBroker();
				}finally{
					try {
						Thread.sleep(3*1000);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				continue;
			}

			if(consumer == null){
				this.closeBroker();
				isRunning = false;
				try {
					Thread.sleep(3*1000);
				} catch (Exception ex) {
					ActiveMQlogger.logger.error(CommonUtil.getClassNameAndMethod(this)+",erro:"+ex.getMessage());
				}
				continue;
			}

			try {
				Message message = consumer.receive(3*1000);

				ActiveMQlogger.getLogger().info("ConsumerBroker.recevie(messageObj) << "+ message);

				if(message == null){
					continue;
				}
				//设定消息保护。
				if(messageQueue.size()>10000){
					continue;
				}
				messageQueue.put(message);
			} catch (Exception ex) {
				ActiveMQlogger.logger.error(CommonUtil.getClassNameAndMethod(this)+",erro:"+ex.getMessage());
				this.closeBroker();
				isRunning = false;
				continue;
			}
		}

		ActiveMQlogger.getLogger().info("this.threadGroup.interrupt()");
		//关闭消息处理线程
		this.threadGroup.interrupt();
		//关闭消息队列
		this.closeBroker();
	}

	/**
	 * 初始化线程
	 * 线程数量应视服务器性能而定
	 */
	private void initThread(){
		threadGroup = new ThreadGroup(this.queueName);
		String threadName = "MessageListenerThread";
		for (int i = 0; i < 5; i++) {
			Thread  client = new Thread(this.threadGroup,new MessageListenerThread(i),threadName+i);
			client.start();
		}
	}

	/**
	 * 消息处理线程
	 * @author ljp
	 *
	 */
	private  class MessageListenerThread implements Runnable{
		/**
		 * 线程ID
		 */
		private int threadId;

		public MessageListenerThread(int threadId){
			this.threadId = threadId;

		}
		@Override
		public void run() {
			while(true){
				try {
					//从队列中获取通知的数据
					Message message = messageQueue.take();
					ActiveMQlogger.getLogger().info(CommonUtil.getClassNameAndMethod(this)+",threadId: "+threadId+",message: "+ message.toString());

					//处理消息
					messageListener.onMessage(message);

				} catch (Exception ex) {
					ex.printStackTrace();
					//防止死循环
					try {
						Thread.sleep(3000);
					} catch (InterruptedException ex1) {
					}
				}
			}
		}
	}

	/**
	 * 获取队列
	 * @return
	 */
	public MessageConsumer getConsumer(){
		try {
			//初始化ConnectionFactory
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
			//创建mq连接
			Connection conn = connectionFactory.createConnection();
			//启动连接
			conn.start();
			//创建会话
			Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			//通过会话创建目标
			Destination dest = session.createQueue(this.queueName);
			//创建mq消息的消费者
			MessageConsumer consumer = session.createConsumer(dest);
            ActiveMQlogger.logger.info(CommonUtil.getClassNameAndMethod(this)+",created MessageConsumer succuss. queueName:"+this.queueName+",BROKER_URL:"+BROKER_URL);
			//给消费者队列设定消费对象监听，单线程下使用
//			consumer.setMessageListener(messageListener);
			return consumer;
		} catch (JMSException e) {
			ActiveMQlogger.logger.error(CommonUtil.getClassNameAndMethod(this)+",erro:"+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}
