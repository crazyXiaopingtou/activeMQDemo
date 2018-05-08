package yunqu.activeMQDemo.listener;

import yunqu.activeMQDemo.base.Constants;
import yunqu.activeMQDemo.log.ActiveMQlogger;
import yunqu.activeMQDemo.mqclient.ConsumerBroker;
import yunqu.activeMQDemo.mqclient.message.ConsumerMessage;
import yunqu.activeMQDemo.util.CommonUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class GlobalContextListener implements ServletContextListener ,GolbalThreadState  {

    private  Thread consumerBrokerThread = null;
    private boolean runState = true;
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ActiveMQlogger.logger.info(CommonUtil.getClassNameAndMethod(this)+">>>>>>>>server start.........");
        consumerBrokerThread = new Thread(new ConsumerBroker(Constants.QUEUE_NAME,new ConsumerMessage(),this));
        consumerBrokerThread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        runState = false;
        consumerBrokerThread.interrupt();//通知线程应该中断了，由线程自己中断
    }

    @Override
    public boolean isRunning() {
        return runState;
    }
}
