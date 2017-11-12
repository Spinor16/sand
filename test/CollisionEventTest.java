import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollisionEventTest {
    @Test
    void compareTo() {
        Double t1 = 1.2;
        Double t2 = t1 + 2;
        CollisionEvent node1 = new CollisionEvent(t1, 2, 3);
        CollisionEvent node2 = new CollisionEvent(t2, 6, 2);


        assertEquals(node1.compareTo(node2), t1.compareTo(t2));
        assertEquals(node2.compareTo(node1), t2.compareTo(t1));
    }

    @Test
    void equals() {
        Double t1 = 1.2;
        Double t2 = t1;
        Double t3 = t2 + 2.5;
        CollisionEvent node1 = new CollisionEvent(t1, 2, 3);
        CollisionEvent node2 = new CollisionEvent(t2, 6, 2);
        CollisionEvent node3 = new CollisionEvent(t3, 6, 2);
        assertEquals(node1, node1);
        assertEquals(node1, node2);
        assertNotEquals(node2, node3);
    }
}