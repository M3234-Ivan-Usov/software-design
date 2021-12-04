package ru.ifmo.rain;

import aspects.AnalyzerAspect;
import ru.ifmo.rain.sub.HelloHell;

public class HelloWorld {
    public void greet() {
        System.out.println("Hello World!");
    }

    public void greet(String name) {
        System.out.println("Hello, " + name + "!");
    }


    public static void main(String[] args) {
        HelloWorld world = new HelloWorld();
        world.greet();
        HelloWorld another = new HelloWorld();
        another.greet();
        world.greet("Software Design");
        world.greet();
        HelloHell hell = new HelloHell();
        hell.greet();
        int three = new HelloHell().hellos(new String[]{"one", "two", "three"});
        hell.greet("...");
        AnalyzerAspect.show();
    }
}
