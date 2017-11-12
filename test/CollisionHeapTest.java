import org.junit.jupiter.api.Test;
import utils.IO;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CollisionHeapTest {
    /**
     * Test that CollisionEvents can be inserted and are always minimal when removed.
     * @throws HeapException
     */
    @Test
    void insertAndRemoveMin() throws HeapException{
        int nEvents = 10000;
        CollisionHeap heap = new CollisionHeap(nEvents);
        Random random = new Random();
        for (int _ = 0; _ < nEvents; _++) {
            int i = ((int) (random.nextDouble() * (nEvents))); // Get random integer between 0 and nEvents-1.
            int j = ((int) (random.nextDouble() * (nEvents))); // nEvents because nextDouble yields [0,1)
            while (i == j) {
                j = ((int) (random.nextDouble() * (nEvents)));
            }
            heap.insert(new CollisionEvent(random.nextDouble(), i, j));
        }

        CollisionEvent min  = heap.removeMin();
        CollisionEvent nextMin;
        int counter = 1;

        while (!heap.isEmpty()) {
            counter++;
            nextMin = heap.removeMin();
            assertTrue(min.compareTo(nextMin) < 0);
            min = nextMin;
        }
        assertEquals(counter, nEvents);
    }

    /**
     * Test that heap throws exception when trying to insert event into full heap.
     * @throws HeapException
     */
    @Test
    void insertThrowsErrorWhenFull() throws HeapException{
        CollisionHeap heap = new CollisionHeap(1);
        heap.insert(new CollisionEvent(1,0,0));
        assertThrows(HeapException.class, () -> heap.insert(new CollisionEvent(2, 0, 0)));
    }

    @Test
    void isEmpty() {
        CollisionHeap heap = new CollisionHeap(1);
        assertTrue(heap.isEmpty());
    }
}