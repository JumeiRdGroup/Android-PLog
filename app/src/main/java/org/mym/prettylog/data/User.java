package org.mym.prettylog.data;

import java.util.List;

/**
 *
 * @author muyangmin
 * @since V1.3.0
 */
public class User {

    private String name;
    private int age;
    private List<Cat> cats;

    public User(String name, int age, List<Cat> cats) {
        this.name = name;
        this.age = age;
        this.cats = cats;
    }

    //toString method is not overridden

    public static final class Cat {
        public String name;
        public String color;

        public Cat(String name, String color) {
            this.name = name;
            this.color = color;
        }
    }
}
