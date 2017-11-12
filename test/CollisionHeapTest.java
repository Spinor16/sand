import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.IO;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CollisionHeapTest {

    CollisionHeap heap;
    int nEvents = 10000;
    Random random = new Random();

    /**
     * Set up heap with random entries.
     * @throws HeapException
     */
    @BeforeEach
    void init() throws HeapException {
        heap = new CollisionHeap(nEvents);
        // Fill heap with random events.
        for (int _ = 0; _ < nEvents; _++) {
            int i = ((int) (random.nextDouble() * (nEvents))); // Get random integer between 0 and nEvents-1.
            int j = ((int) (random.nextDouble() * (nEvents))); // nEvents because nextDouble yields [0,1)
            while (i == j) {
                j = ((int) (random.nextDouble() * (nEvents)));
            }
            heap.insert(new CollisionEvent(random.nextDouble(), i, j));
        }
    }

    @AfterEach
    void tearDown() {
        heap = null;
    }

    /**
     * Test that CollisionEvents are always minimal when removed.
     * @throws HeapException
     */
    @Test
    void removeMin() throws HeapException{
        // Check that condition min_n < min_n+1 holds until heap is empty.
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
     * Test that nodes can be deleted at random indices.
     * @throws HeapException
     */
    @Test
    void remove() throws HeapException {
        // Remove some random nodes from heap.
        int counter = 0;
        for (int _ = 0; _ < nEvents/2; _++) {
            int i = ((int) (random.nextDouble() * (nEvents)));
            try {
                heap.remove(i);
                counter++;
            }
            catch (HeapException e) {
            }
        }
        // Check that condition min_n < min_n+1 holds until heap is empty.
        CollisionEvent min  = heap.removeMin();
        counter++;
        CollisionEvent nextMin;
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
    void insert() throws HeapException{
        CollisionHeap heap = new CollisionHeap(1);
        heap.insert(new CollisionEvent(1,0,0));
        assertThrows(HeapException.class, () -> heap.insert(new CollisionEvent(2, 0, 0)));
    }

    @Test
    void isEmpty() {
        CollisionHeap heap = new CollisionHeap(1);
        assertTrue(heap.isEmpty());
    }

    @Test
    void clear() {
        heap.clear();
        assertTrue(heap.isEmpty());
    }
}