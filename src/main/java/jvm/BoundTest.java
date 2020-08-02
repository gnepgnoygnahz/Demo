package jvm;

interface Huntable {
    void hunt();
}

interface Func {
    public boolean func(String str);
}

/**
 * @ClassName Bound
 * @Description TODO
 * @Author zhangyp
 * @Date 2020/6/6 22:27
 * @Version 1.0
 */
class Animal {
    public void eat() {
        System.out.println("动物进食");
        b();//表现为：晚期绑定，invokevirtual指令
    }

    public void b() {
        System.out.println("动物进食");
    }
}

class Dog extends Animal implements Huntable {
    @Override
    public void eat() {
        System.out.println("狗吃骨头");
    }

    @Override
    public void hunt() {
        System.out.println("捕食耗子，多管闲事");
    }
}

class Cat extends Animal implements Huntable {

    public Cat() {
        super();//表现为：早期绑定，invokespecial指令
    }

    public Cat(String name) {
        this();//表现为：早期绑定，invokespecial指令
    }

    @Override
    public void eat() {
        super.eat();//表现为：早期绑定
        System.out.println("猫吃鱼");
    }

    @Override
    public void hunt() {
        System.out.println("捕食耗子，天经地义");
    }
}

class AnimalTest {
    public void showAnimal(Animal animal) {
        animal.eat();//表现为：晚期绑定，invokevirtual指令
    }

    public void showHunt(Huntable h) {
        h.hunt();//表现为：晚期绑定，invokeinterface指令
    }
}

class Lambda {
    public static void main(String[] args) {
        Lambda lambda = new Lambda();

        Func func = s -> {//invokedynamic指令
            return true;
        };

        lambda.lambda(func);

        lambda.lambda(s -> {
            return true;
        });
    }

    public void lambda(Func func) {
        return;
    }
}
