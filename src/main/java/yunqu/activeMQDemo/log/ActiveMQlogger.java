package yunqu.activeMQDemo.log;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveMQlogger {
    public static final Logger logger = LoggerFactory.getLogger("ActiveMQlogger");
    public static Logger getLogger(){
        return logger;
    }
}
