package zsy.androidtranstest.domain;

/**
 * Created by zsy on 16/5/22.
 */
public class Contact {
    public int id;
    public String name;
    public String image;

    public Contact(int id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public Contact() {
    }
}