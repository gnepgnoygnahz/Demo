package dataStructure.linkedList;

import java.util.Stack;

/**
 * @ClassName SingleLinkedList
 * @Description TODO 单链表
 * @Author zhangyp
 * @Date 2020/6/14 18:25
 * @Version 1.0
 */
public class SingleLinkedList {
    private static MyNode head = new MyNode(0, "");

    public static void main(String[] args) {
        MyNode m1 = new MyNode(1, "a");
        MyNode m11 = new MyNode(11, "a");
        MyNode m2 = new MyNode(2, "b");
        MyNode m3 = new MyNode(3, "c");
        MyNode m4 = new MyNode(4, "d");
        SingleLinkedList s = new SingleLinkedList();
        /*s.add(m1);
        s.add(m2);
        s.add(m3);
        s.list();*/
        s.addByOrder(m4);
        s.addByOrder(m1);
        s.addByOrder(m11);
        s.addByOrder(m3);
        s.addByOrder(m2);
        s.list();
        s.update(new MyNode(11, "aa"));
        s.list();
        s.delete(3);
        s.list();
        System.out.println(s.size());
        System.out.println(s.findLastIndexNode(3));
        s.reverseList();
        s.list();
        s.reversePrint();
    }

    public MyNode getHead() {
        return head;
    }

    public void list() {
        if (head.next == null) {
            System.out.println("列表为空");
            return;
        }
        MyNode temp = head.next;
        while (temp != null) {
            System.out.println(temp);
            temp = temp.next;
        }
    }

    public void add(MyNode node) {
        MyNode temp = head;
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = node;
    }

    public void addByOrder(MyNode node) {
        MyNode temp = head;
        boolean canAdd = true;
        do {
            if (temp.next == null || temp.next.id > node.id) {
                break;
            } else if (temp.next.id == node.id) {
                System.out.println("节点已存在，不允许加入");
                canAdd = false;
                break;
            }
        } while ((temp = temp.next) != null);

        if (canAdd) {
            node.next = temp.next;
            temp.next = node;
        }
    }

    public void update(MyNode node) {
        MyNode temp = head;
        while ((temp = temp.next) != null && temp.id != node.id) {
        }
        if (temp != null) {
            temp.name = node.name;
        } else {
            System.out.println("没找到");
        }
    }

    public void delete(int id) {
        MyNode temp = head;
        do {
            if (temp.next != null && temp.next.id == id) {
                break;
            }
        } while ((temp = temp.next) != null);
        if (temp != null) {
            temp.next = temp.next.next;
        } else {
            System.out.println("没找到");
        }
    }

    public int size() {
        int size = 0;
        MyNode temp = head;
        while ((temp = temp.next) != null) {
            size++;
        }
        return size;
    }

    public MyNode findLastIndexNode(int index) {
        int size = size();
        if (index > size || index < 0) {
            System.out.println("不对");
            return null;
        }
        int start = 0;
        MyNode temp = head.next;
        while (start != size - index) {
            temp = temp.next;
            start++;
        }
        return temp;
    }

    public void reverseList() {
        if (head.next == null || head.next.next == null) {
            return;
        }
        MyNode reverseHead = new MyNode(0, "");
        MyNode cur = head.next;
        MyNode next;
        while (cur != null) {
            next = cur.next;
            cur.next = reverseHead.next;
            reverseHead.next = cur;
            cur = next;
        }
        head.next = reverseHead.next;
    }

    public static void reversePrint() {
        Stack<MyNode> stack = new Stack<MyNode>();
        MyNode cur = head;
        while ((cur = cur.next) != null) {
            stack.push(cur);
        }
        while (stack.size() > 0) {
            System.out.println(stack.pop()); //stack的特点是先进后出
        }
    }
}

class MyNode {
    public int id;
    public String name;
    public MyNode next; //指向下一个节点

    public MyNode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    //为了显示方法，我们重新toString
    @Override
    public String toString() {
        return "MyNode [id=" + id + ", name=" + name + "]";
    }
}
