package deep.copy.utils;

import deep.copy.domain.Man;
import deep.copy.domain.Woman;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CopyUtilsTest {

    @Test
    void deepCopyWithConstructor() {
        Man mockMan = getMockMan();
        Man man = CopyUtils.deepCopy(mockMan);
    }

    @Test
    void deepCopyWithDefaultConstructor() {
        Woman mockWoman = getMockWoman();
        Woman woman = CopyUtils.deepCopy(mockWoman);
    }

    @Test
    void deepCopyWithoutFields() {
        RandomClass randomClass = new RandomClass();
        RandomClass rc = CopyUtils.deepCopy(randomClass);
        // checking the references. they should be the same
        assertEquals(rc, randomClass);
    }

    private Man getMockMan() {
        return new Man("Joe", 22, List.of("Think and grow rich!", "Effective Java.", "Kafka."));
    }

    private Woman getMockWoman() {
        Woman woman = new Woman();
        woman.setAge(22);
        woman.setName("Nataly");
        woman.setFavoriteBooks(List.of("Think and grow rich!", "Effective Java.", "Kafka."));
        return woman;
    }

    private class RandomClass{

    }
}