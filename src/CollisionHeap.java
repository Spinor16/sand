import utils.Array;
import utils.IO;

/**
 * Collision Heap can contain N^2 number of CollisionEvents which have to corespond to N different particles,
 * i.e. the indices i and j of the events cannot be bigger than N.
 */
public class CollisionHeap {

    private CollisionEvent[] events;
    private final int[][] indexMap;
    private int heapSize = 0;
    int maxSize;

    /**
     * Constructor
     * @param N Square root of maximal size of CollisionHeap, i.e. the square root of total number of collisions.
     */
    public CollisionHeap(int N) {
        events = new CollisionEvent[N*N + 1];
        indexMap = new int[N][N];
        maxSize = N*N;
    }

    public void insert(CollisionEvent event) throws HeapException{
        if (heapSize == events.length - 1) {
            throw new HeapException("Heap's underlying storage is overflow");
        }
        else {
            heapSize++;
            events[heapSize] = event;
            try {
                indexMap[event.i()][event.j()] = heapSize;
            }
            catch (IndexOutOfBoundsException e) {
                throw new IndexOutOfBoundsException(
                        "Indices (i,j)=(" + event.i() + "," + event.j() + ") of event might be bigger than max index "
                        + (maxSize-1) + " of heap."
                );
            }
            siftUp(heapSize);
        }
    }


    public CollisionEvent removeMin() throws HeapException {
        // FixMe: Could be optimized, because some asumptions can be made for remove(1) that then don't have to be checked in remove().
        return remove(1);
    }

    public CollisionEvent remove(int nodeIndex) throws HeapException {
        CollisionEvent deletedEvent, swappedEvent;
        int parentIndex = parentIndex(nodeIndex);
        if (isEmpty()) {
            throw new HeapException("Heap is empty");
        }
        else if (nodeIndex > heapSize || nodeIndex <= 0) {
            throw new HeapException("Node with index " + nodeIndex + " does not exist");
        }
        else {
            deletedEvent = events[nodeIndex];
            swappedEvent = events[heapSize];
            events[nodeIndex] = swappedEvent;
            indexMap[deletedEvent.i()][deletedEvent.j()] = 0;
            indexMap[swappedEvent.i()][swappedEvent.j()] = nodeIndex;
            heapSize--;
            if (heapSize > 0) {
                if (nodeIndex > 1 && events[nodeIndex].compareTo(events[parentIndex]) < 0) {
                    siftUp(nodeIndex);
                }
                else {
                    siftDown(nodeIndex);
                }
            }
        }
        return deletedEvent;
    }

    private void siftUp(int nodeIndex) {
        int parentIndex;
        if (nodeIndex != 1) {
            parentIndex = parentIndex(nodeIndex);
            if (events[nodeIndex].compareTo(events[parentIndex]) < 0) {
                Array.swap(events, nodeIndex, parentIndex);
                Array.swap(indexMap, events[nodeIndex].i(), events[nodeIndex].j(), events[parentIndex].i(),
                        events[parentIndex].j());
                siftUp(parentIndex);
            }
        }
    }

    private void siftDown(int nodeIndex) {
        int lChildIndex, rChildIndex, smallerChildIndex;
        lChildIndex = lChildIndex(nodeIndex);
        rChildIndex = rChildIndex(nodeIndex);

        // Find which child is smaller.
        if (rChildIndex > heapSize) {
            if (lChildIndex > heapSize)
                return;
            else
                smallerChildIndex = lChildIndex;
        } else {
            if (events[lChildIndex].compareTo(events[rChildIndex]) < 0)
                smallerChildIndex = lChildIndex;
            else
                smallerChildIndex = rChildIndex;
        }

        // Swap parent and smaller child if child is smaller than parent and recurse.
        if (events[nodeIndex].compareTo(events[smallerChildIndex]) > 0) {
            Array.swap(events, smallerChildIndex, nodeIndex);
            Array.swap(indexMap, events[nodeIndex].i(), events[nodeIndex].j(), events[smallerChildIndex].i(),
                    events[smallerChildIndex].j());
            siftDown(smallerChildIndex);
        }
    }

    public boolean isEmpty() {
        return heapSize <= 0;
    }

    public void clear() {heapSize = 0;}

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
