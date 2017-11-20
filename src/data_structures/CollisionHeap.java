package data_structures;

import data_structures.CollisionEvent;
import exceptions.HeapException;
import utils.Array;
import utils.IO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collision Heap can contain N^2 number of CollisionEvents which have to corespond to N different particles,
 * i.e. the indices i and j of the events cannot be bigger than N.
 */
public class CollisionHeap {

    private List<CollisionEvent> events;
    private final int[][] indexMap;
    private int heapSize = 0;
    int maxSize;

    /**
     * Constructor
     * @param N Square root of maximal size of data_structures.CollisionHeap, i.e. the square root of total number of collisions.
     */
    public CollisionHeap(int N) {
        events = new ArrayList<CollisionEvent>(N+1);
        events.add(null); // First element of arraylist is not used.
        indexMap = new int[N][N];
        maxSize = N*N;
    }

    public void insert(CollisionEvent event) throws HeapException {
        if (heapSize == maxSize) { // events.length - 1) {
            throw new HeapException("Heap's underlying storage is overflow");
        }
        else {
            heapSize++;
            events.add(event);
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

    /**
     * Returns pointer to minimal event in heap.
     * @return min of heap
     */
    public CollisionEvent min() {
        return events.get(1);
    }

    public CollisionEvent removeMin() throws HeapException {
        // FixMe: Could be optimized, because some asumptions can be made for remove(1) that then don't have to be checked in remove().
        return remove(1);
    }

    private CollisionEvent remove(int nodeIndex) throws HeapException {
        CollisionEvent deletedEvent, swappedEvent;
        int parentIndex = parentIndex(nodeIndex);
        if (isEmpty()) {
            throw new HeapException("Heap is empty");
        }
        else if (nodeIndex > heapSize || nodeIndex <= 0) {
            throw new HeapException("Node with index " + nodeIndex + " does not exist");
        }
        else {
            deletedEvent = events.get(nodeIndex);
            swappedEvent = events.get(heapSize);
            events.set(nodeIndex, swappedEvent);
            indexMap[deletedEvent.i()][deletedEvent.j()] = 0;
            indexMap[swappedEvent.i()][swappedEvent.j()] = nodeIndex;
            heapSize--;
            if (heapSize > 0) {
                if (nodeIndex > 1 && events.get(nodeIndex).compareTo(events.get(parentIndex)) < 0) {
                    siftUp(nodeIndex);
                }
                else {
                    siftDown(nodeIndex);
                }
            }
        }
        return deletedEvent;
    }

    /**
     * Removes events containing index from heap and returns them.
     * @param index CollisionEvents containing index will be removed.
     * @param events Removed CollisionEvents will be gathered in this array.
     * @throws HeapException
     */
    public void removeEventsContainingIndexSE(int index, ArrayList<CollisionEvent> events) throws HeapException {
        // Remove column where j=index.
        for (int i = 0; i < index; i++) {
            if (indexMap[i][index] != 0) {
                events.add(remove(indexMap[i][index]));
            }
        }

        // Remove row where i=index.
        for (int j = index + 1; j < indexMap.length; j++) {
            if (indexMap[index][j] != 0) {
                events.add(remove(indexMap[index][j]));
            }
        }
    }

    private void siftUp(int nodeIndex) {
        int parentIndex;
        if (nodeIndex != 1) {
            parentIndex = parentIndex(nodeIndex);
            if (events.get(nodeIndex).compareTo(events.get(parentIndex)) < 0) {
                Collections.swap(events, nodeIndex, parentIndex);
                Array.swap(indexMap, events.get(nodeIndex).i(), events.get(nodeIndex).j(), events.get(parentIndex).i(),
                        events.get(parentIndex).j());
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
            if (events.get(lChildIndex).compareTo(events.get(rChildIndex)) < 0)
                smallerChildIndex = lChildIndex;
            else
                smallerChildIndex = rChildIndex;
        }

        // Swap parent and smaller child if child is smaller than parent and recurse.
        if (events.get(nodeIndex).compareTo(events.get(smallerChildIndex)) > 0) {
            Collections.swap(events, smallerChildIndex, nodeIndex);
            Array.swap(indexMap,
                    events.get(nodeIndex).i(), events.get(nodeIndex).j(),
                    events.get(smallerChildIndex).i(), events.get(smallerChildIndex).j()
            );
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
        return IO.toString(events.toArray(), 1, events.size());
    }
}
