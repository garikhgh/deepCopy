package deep.copy.domain;

import java.time.Instant;
import java.util.List;

public class Woman {
    private String name;
    private int age;
    private List<String> favoriteBooks;
    private Instant instant = Instant.now();
    // this case is not handled
//    private Man man = new Man("Gve", 33, List.of("Java"));


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

    public List<String> getFavoriteBooks() {
        return favoriteBooks;
    }

    public void setFavoriteBooks(List<String> favoriteBooks) {
        this.favoriteBooks = favoriteBooks;
    }
}
