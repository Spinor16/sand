import utils.Array;
import utils.IO;

public class CollisionHeap {

    private CollisionEvent[] events;
    private final int[][] indexMap;
    private int heapSize = 0;
    int maxSize;

    /**
     * Constructor
     * @param size Maximal size of CollisionHeap, i.e. the total number of collisions.
     */
    public CollisionHeap(int size) {
        events = new CollisionEvent[size+1];
        indexMap = new int[size][size];
        maxSize = size;
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
        CollisionEvent deletedEvent;
        if (isEmpty()) {
            throw new HeapException("Heap is empty");
        }
        else if (nodeIndex > heapSize || nodeIndex <= 0) {
            throw new HeapException("Node with index " + nodeIndex + " does not exist");
        }
        else {
            deletedEvent = events[nodeIndex];
            events[nodeIndex] = events[heapSize];
            heapSize--;
            if (heapSize > 0) {
                if (nodeIndex > 1 && events[nodeIndex].compareTo(events[parentIndex(nodeIndex)]) < 0) {
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
            Array.swap(indexMap, events[nodeIndex].i(), events[nodeIndex].j(), events[minIndex].i(),
                    events[minIndex].j());
            siftDown(minIndex);
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
