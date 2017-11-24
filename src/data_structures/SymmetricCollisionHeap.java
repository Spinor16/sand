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
public class SymmetricCollisionHeap extends CollisionHeap{

    /**
     * Constructor
     * @param N Square root of maximal size of data_structures.SymmetricCollisionHeap, i.e. the square root of total number of collisions.
     */
    public SymmetricCollisionHeap(int N) {
        super(N,N);
    }

    /**
     * Removes events containing index from heap and returns them.
     * @param index CollisionEvents containing index will be removed.
     * @param events Removed CollisionEvents will be gathered in this array.
     * @throws HeapException
     */
    public void removeEventsContainingIndexSE(int index, ArrayList<CollisionEvent> events) throws HeapException {
        // Remove column where j=index.
        removeEventsInRowSE(index, events);

        // Remove row where i=index.
        removeEventsInColSE(index, events);
    }
}
