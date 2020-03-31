package exceptions;

/**
 * @ClassName CopyPropertyException
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/3/16 13:29
 * @Version 1.0
 */
public class CopyPropertyException extends Exception {

    /*无参构造函数*/
    public CopyPropertyException(){
        super();
    }

    //用详细信息指定一个异常
    public CopyPropertyException(String message){
        super(message);
    }

    //用指定的详细信息和原因构造一个新的异常
    public CopyPropertyException(String message, Throwable cause){
        super(message,cause);
    }

    //用指定原因构造一个新的异常
    public CopyPropertyException(Throwable cause) {
        super(cause);
    }
}
