package com.event.eventbus;

/**
 * Created by zs on 2017/12/28.
 */

public class FriendBean {

    private String name;
    private int age;

    public FriendBean() {}

    public FriendBean(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"name\":\"")
                .append(name).append('\"');
        sb.append(",\"age\":")
                .append(age);
        sb.append('}');
        return sb.toString();
    }

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
}
