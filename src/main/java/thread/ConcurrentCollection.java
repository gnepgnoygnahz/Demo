package thread;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 并发集合实现同步
 * <p>
 * 迭代时抛异常原理
 *
 * @author zhang
 */
public class ConcurrentCollection {
    public static void main(String[] args) {


        //同步集合实现同步锁住的是整个集合,读写都同步
        //vector
        //HashTable锁整个表
        Collections.synchronizedList(new ArrayList<>());
        Map<String, String> stringStringMap = Collections.synchronizedMap(new HashMap<>());
        Collections.synchronizedSet(new HashSet<>());


        //同步的ArrayList 读不同步，写同步。
        //写时复制一个副本（add时候扩容1个长度,remove的时候减小一个长度），操作改变副本的值，将原来的指引指向副本，
        //写效率非常低，因为需要复制。但读非常高，因为写是在副本上操作，因此读时，不需要加锁
        CopyOnWriteArrayList<Person> list = new CopyOnWriteArrayList<>();
        list.add(new Person("张三", 23));
        list.add(new Person("李四", 24));
        list.add(new Person("王五", 25));
        list.remove(1);
        Iterator<Person> i = list.iterator();
        /**
         *所有的遍历方法都适用
         *for循环实现原理就是Iterator迭代器遍历
         *cursor 下一个要访问元素的索引
         *size 集合的元素个数
         *expectedModCount 遍历前的modCount
         *modCount 初始值为0，每对集合中元素做出修改，modCount++
         *cursor和size不相等，hasNext为true
         *expectedModCount和modCount不相等，next方法抛异常ConcurrentModificationException
         *使用迭代器自身的remove或add等方法，或者使用同步集合(不推荐，效率低)，或者使用并发集合时，迭代过程中修改数据不会报异常
         */

        //cursor 	size		expectedModCount modCount
        //删除张三异常
        //while 1
        //0         3                	3       	3
        //remove
        //1			2					3			4	//抛异常

        //删除李四不异常
        //while1
        //循环前
        //0         3                	3       	3
        //循环后
        //1			3					3       	3
        //while2
        //remove
        //2  	    2					3	  	    4   //hasNext为false则不再循环，因此不抛异常

        //删除王五
        //while1循环前
        //0         3                	3       	3
        //while1循环后，while2循环前
        //1			3					3       	3
        //while2循环后，while3循环前
        //2  	    3					3	  	    3
        //remove
        //while3循环后
        //3			2					3			4  //抛异常
        while (i.hasNext()) {
            Person p = i.next();
            if ("张三".equals(p.getName())) {
                i.remove();
            } else {
                System.out.println(p);
            }
        }
		/*for (Person p : c) {
			if("张三".equals(p.getName())){
				c.remove(p);
			}else{
				System.out.println(p);
			}
		}*/

        //基于CopyOnWriteArrayList实现的，只是不允许重复，add重复数据会插入失败
        CopyOnWriteArraySet<Person> set = new CopyOnWriteArraySet<Person>();

        //HashMap线程不安全，HashTable线程安全，但效率低，锁是锁整个hash表
        //ConcurrentHashMap 同步的HashMap，不允许空值，读不同步，写同步 采用的是锁分段机制，
        //默认的并发级别（concurrentLevel）是将表分为16段（segment）每一段都是一个独立的hash表，
        //当访问到来时只锁访问的数据所在的那一段，但jdk1.8后锁分段机制也变成了CAS算法
        Map<String, Person> hashMap = new ConcurrentHashMap<String, Person>();

        //跳表map，高并发并排序，插入效率较低，因为需要排序，但查询效率较高
        Map skipListMap = new ConcurrentSkipListMap<>();


    }
}

class Person {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Person(String name, int age) {
        super();
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person [name=" + name + ", age=" + age + "]";
    }

}