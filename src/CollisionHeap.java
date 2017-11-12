import utils.Array;
import utils.IO;

public class CollisionHeap {

    CollisionEvent[] events;
    CollisionEvent[][] eventMatrix;
    int heapSize = 0;

    /**
     * Constructor
     * @param size Maximal size of CollisionHeap, i.e. the total number of collisions.
     */
    public CollisionHeap(int size) {
        events = new CollisionEvent[size+1];
        eventMatrix = new CollisionEvent[size][size];
    }

    public void insert(CollisionEvent event) throws HeapException{
        if (heapSize == events.length - 1) {
            throw new HeapException("Heap's underlying storage is overflow");
        }
        else {
            heapSize++;
            events[heapSize] = event;
            siftUp(heapSize);
        }

    }

    public CollisionEvent removeMin() throws HeapException {
        CollisionEvent min;
        if (isEmpty()) {
            throw new HeapException("Heap is empty");
        } else {
            min = events[1];
            events[1] = events[heapSize];
            heapSize--;
            if (heapSize > 0) {
                siftDown(1);
            }
        }
        return min;
    }

    private void siftUp(int nodeIndex) {
        int parentIndex;
        if (nodeIndex != 1) {
            parentIndex = parentIndex(nodeIndex);
            if (events[nodeIndex].compareTo(events[parentIndex]) < 0) {
                Array.swap(events, nodeIndex, parentIndex);
                siftUp(parentIndex);
            }
        }
    }

    private void siftDown(int nodeIndex) {
        int lChildIndex, rChildIndex, minIndex;
        lChildIndex = lChildIndex(nodeIndex);
        rChildIndex = rChildIndex(nodeIndex);
        if (rChildIndex > heapSize) {
            if (lChildIndex > heapSize)
                return;
            else
                minIndex = lChildIndex;
        } else {
            if (events[lChildIndex].compareTo(events[rChildIndex]) < 0)
                minIndex = lChildIndex;
            else
                minIndex = rChildIndex;
        }
        if (events[nodeIndex].compareTo(events[minIndex]) > 0) {
            Array.swap(events, minIndex, nodeIndex);
            siftDown(minIndex);
        }
    }

    public boolean isEmpty() {
        return heapSize <= 0;
    }

    private int lChildIndex(int i) {
        return i*2;
    }

    private int rChildIndex(int i) {
        return i*2 + 1;
    }

    private int parentIndex(int i) {
        return i/2;
    }

    public String toString() {
        return IO.toString(events, 1, events.length);
    }
}
