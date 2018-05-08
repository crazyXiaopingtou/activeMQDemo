package yunqu.activeMQDemo.util;

public class CommonUtil {
    /**
     * 打印出类名和方法名
     * @param obj
     * @return
     */
    public static String getClassNameAndMethod(Object obj){
        StringBuilder sb=new StringBuilder();
        sb.append("[" ).append(obj.getClass().getSimpleName()).append(".").append(new Throwable().getStackTrace()[1].getMethodName()).append("]  ");
        return sb.toString();
    }
}
