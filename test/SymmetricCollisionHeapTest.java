import data_structures.CollisionEvent;
import data_structures.SymmetricCollisionHeap;
import exceptions.HeapException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SymmetricCollisionHeapTest {

    SymmetricCollisionHeap heap;
    int nParticles = 1000;
    int nEvents = (int)(0.1*(nParticles*nParticles));
    Random random = new Random();

    /**
     * Set up heap with random entries.
     * @throws HeapException
     */
    @BeforeEach
    void setUp() throws HeapException {
        heap = new SymmetricCollisionHeap(nParticles);
        // Fill heap with random events.
        for (int _ = 0; _ < nEvents; _++) {
            int i = ((int) (random.nextDouble() * (nParticles))); // Get random integer between 0 and nParticles-1.
            int j = ((int) (random.nextDouble() * (nParticles))); // nParticles because nextDouble yields [0,1)
            while (i == j) {
                j = ((int) (random.nextDouble() * (nParticles)));
            }
            heap.insert(new CollisionEvent(0.5 - random.nextDouble(), i, j));
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
        ArrayList<CollisionEvent> events = new ArrayList<>();
        for (int _ = 0; _ < nParticles / 2; _++) {
            int i = ((int) (random.nextDouble() * (nParticles)));
            try {
                heap.removeEventsContainingIndexSE(i,events);
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
//        assertEquals(nEvents - events.size(), counter);
    }

    /**
     * Test that heap throws exception when trying to insert event into full heap.
     * @throws HeapException
     */
    @Test
    void insert() throws HeapException{
        SymmetricCollisionHeap heap = new SymmetricCollisionHeap(1);
        heap.insert(new CollisionEvent(1,0,0));
        assertThrows(HeapException.class, () -> heap.insert(new CollisionEvent(2, 0, 0)));
    }

    @Test
    void isEmpty() {
        SymmetricCollisionHeap heap = new SymmetricCollisionHeap(1);
        assertTrue(heap.isEmpty());
    }

    @Test
    void clear() throws HeapException {
        heap.clear();
        assertTrue(heap.isEmpty());
    }
}