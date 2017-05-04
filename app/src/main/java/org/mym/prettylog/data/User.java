package org.mym.prettylog.data;

import java.util.List;

/**
 *
 * @author muyangmin
 * @since V1.3.0
 */
@SuppressWarnings("FieldCanBeLocal")
public class User {

    private String name;
    private int age;
    private List<Cat> cats;
    private Cat favCat;

    public User(String name, int age, List<Cat> cats) {
        this.name = name;
        this.age = age;
        this.cats = cats;
        if (cats != null && cats.size() > 0) {
            this.favCat = cats.get(0);
        }
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
