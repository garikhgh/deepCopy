package deep.copy.utils;

import deep.copy.domain.Man;
import deep.copy.domain.RandomEmptyClass;
import deep.copy.domain.Woman;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CopyUtilsTest {

    @Test
    void deepCopyWithConstructor() {
        Man mockMan = getMockMan();
        Man man = CopyUtils.deepCopy(mockMan);
        assertNotEquals(mockMan, man);
        List<String> favoriteBooks = mockMan.getFavoriteBooks();
        favoriteBooks.add("Java");
        List<String> favoriteBooks1 = man.getFavoriteBooks();
        // the size should be different, as the references are not the same
        assertNotEquals(favoriteBooks.size(), favoriteBooks1.size());
    }

    @Test
    void deepCopyWithDefaultConstructor() {
        Woman mockWoman = getMockWoman();
        Woman woman = CopyUtils.deepCopy(mockWoman);
        assertNotEquals(mockWoman, woman);

        List<String> favoriteBooks = mockWoman.getFavoriteBooks();
        favoriteBooks.add("Java");
        List<String> favoriteBooks1 = woman.getFavoriteBooks();
        // the size should be different, as the references are not the same
        assertNotEquals(favoriteBooks.size(), favoriteBooks1.size());
    }

    @Test
    void deepCopyWithoutFields() {
        RandomEmptyClass randomClass = new RandomEmptyClass();
        RandomEmptyClass rc = CopyUtils.deepCopy(randomClass);
        // checking the references. they should be the same
        assertEquals(rc, randomClass);
    }

    private Man getMockMan() {
        List<String> favoriteBooks = getFavoriteBooks();
        return new Man("Joe", 22, favoriteBooks);
    }

    private Woman getMockWoman() {
        List<String> favoriteBooks = getFavoriteBooks();
        Woman woman = new Woman();
        woman.setAge(22);
        woman.setName("Nataly");
        woman.setFavoriteBooks(favoriteBooks);
        return woman;
    }
    private List<String> getFavoriteBooks() {
        List<String> favoriteBooks = new ArrayList<>();
        favoriteBooks.add("Think and grow rich!");
        favoriteBooks.add("Effective Java.");
        favoriteBooks.add("Kafka!");
        return favoriteBooks;
    }
}