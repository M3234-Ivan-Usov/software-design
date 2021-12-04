package ru.ifmo.rain.sub;

public class HelloHell {
    public void greet() {
        System.out.println("Hello Hell!");
    }

    public void greet(String name) {
        System.out.println("Hello from Hell, " + name + "!");
    }

    public int hellos(String[] names) { return names.length; }
}
