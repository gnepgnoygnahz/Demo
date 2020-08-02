package jvm;

import sun.misc.Launcher;

import java.net.URL;

/**
 * @ClassName ClassLoaderTest
 * @Description TODO BootstrapClassLoader、ExtClassLoader、AppClassLoader都是虚拟机自带的类加载器
 * @Author zhangyp
 * @Date 2020/5/31 19:50
 * @Version 1.0
 */
public class ClassLoaderTest {
    public static void main(String[] args) {
        //获取系统类加载器
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println(systemClassLoader);//sun.misc.Launcher$AppClassLoader@18b4aac2

        //获取其上层类加载器：扩展类加载器
        ClassLoader extClassLoader = systemClassLoader.getParent();
        System.out.println(extClassLoader);//sun.misc.Launcher$ExtClassLoader@14ae5a5

        //获取其上层类加载器：引导类加载器，但是获取不到，为null
        ClassLoader bootstrapClassLoader = extClassLoader.getParent();
        System.out.println(bootstrapClassLoader);//null

        //获取用户自定义类的加载器:与系统类加载器一样，这些类加载器都是单实例的
        ClassLoader userDefinedClass = ClassLoaderTest.class.getClassLoader();
        System.out.println(userDefinedClass);//sun.misc.Launcher$AppClassLoader@18b4aac2

        //获取java的String类的类加载器：引导类加载器，java核心类库都是由引导类加载器加载的
        ClassLoader classLoader = String.class.getClassLoader();
        System.out.println(classLoader);//null

        //获取BootstrapClassLoader加载的路径
        //file:/D:/Program%20Files/Java/jdk1.8.0_171/jre/lib/resources.jar
        //file:/D:/Program%20Files/Java/jdk1.8.0_171/jre/lib/rt.jar
        //file:/D:/Program%20Files/Java/jdk1.8.0_171/jre/lib/sunrsasign.jar
        //file:/D:/Program%20Files/Java/jdk1.8.0_171/jre/lib/jsse.jar
        //file:/D:/Program%20Files/Java/jdk1.8.0_171/jre/lib/jce.jar
        //file:/D:/Program%20Files/Java/jdk1.8.0_171/jre/lib/charsets.jar
        //file:/D:/Program%20Files/Java/jdk1.8.0_171/jre/lib/jfr.jar
        //file:/D:/Program%20Files/Java/jdk1.8.0_171/jre/classes
        URL[] urLs = Launcher.getBootstrapClassPath().getURLs();
        for (URL urL : urLs) {
            System.out.println(urL);
        }

        //获取ExtClassLoader加载的路径
        //D:\Program Files\Java\jdk1.8.0_171\jre\lib\ext;C:\Windows\Sun\Java\lib\ext
        String extDirs = System.getProperty("java.ext.dirs");
        System.out.println(extDirs);
    }
}