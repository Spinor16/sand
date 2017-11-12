import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CollisionHeapTest {
    /**
     * Test that CollisionEvents can be inserted and are always minimal when removed.
     * @throws HeapException
     */
    @Test
    void insertAndRemoveMin() throws HeapException{
        int nEvents = 50;
        CollisionHeap heap = new CollisionHeap(nEvents);
        Random random = new Random();
        for (int _ = 0; _ < nEvents; _++) {
            int i = ((int) (random.nextDouble() * (nEvents + 1))); // Get random integer between 0 and nEvents.
            int j = ((int) (random.nextDouble() * (nEvents + 1))); // nEvents + 1 because nextDouble [0,1)
            while (i == j) {
                j = ((int) (random.nextDouble() * (nEvents + 1)));
            }
            heap.insert(new CollisionEvent(random.nextDouble(), i, j));
        }

        CollisionEvent min  = heap.removeMin();
        CollisionEvent next_min;
        int counter = 1;
        while (!heap.isEmpty()) {
            counter++;
            next_min = heap.removeMin();
            assertTrue(min.compareTo(next_min) < 0);
        }
        assertEquals(counter, nEvents);
    }

    /**
     * Test that heap throws exception
     * @throws HeapException
     */
    @Test
    void insertThrowsErrorWhenFull() throws HeapException{
        CollisionHeap heap = new CollisionHeap(1);
        heap.insert(new CollisionEvent(1,1,2));
        assertThrows(HeapException.class, () -> heap.insert(new CollisionEvent(2, 3, 1)));
    }

    @Test
    void isEmpty() {
        CollisionHeap heap = new CollisionHeap(1);
        assertTrue(heap.isEmpty());
    }
}