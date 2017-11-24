package data_structures;

import calc.BoxDist2;
import generators.IGenerator;
import utils.Array;

import java.awt.*;

public class BinaryTree {
    public final Node root;
    public final Particle[] particles;
    private boolean isBuilt = false;
    public final int dimensions;
//    int swaps = 0;
//    int comparisons = 0;
//    int operations = 0;
//    int partitions = 0;

    public BinaryTree(Particle[] particles) {
        this.particles = particles;
        dimensions = particles[0].position.length;

        double[] posMin = new double[dimensions];
        double[] posMax = new double[dimensions];

        for (int i = 0; i < dimensions; i++) {
            posMin[i] = 0;
            posMax[i] = 1;
        }
        root = new Node(posMin, posMax, 0, particles.length - 1, null, this);
        buildTree(root);
    }

    public boolean isBuilt() {
        return isBuilt;
    }

    /**
     * Partitions the particle array.
     * @post indices[lo<=x<i] < pivot && indices[i<=x<=hi] >= pivot
     * @param particles Array of indices.
     * @param lo Lower index of partition.
     * @param hi Higher index of partition.
     * @param pivot Value to sort indices by. (All lower ones and all higher ones.)
     * @param dimension Dimension (0, 1, 2, ...) to partition by.
     * @return index i such that the after condition is fulfilled.
     */
    private int partition(Particle[] particles, int lo, int hi, double pivot, int dimension) {
//        partitions++;
        int i = lo;
        int j = hi;
        while (true) { // comparisons++;
            while (i <= hi && particles[i].position(dimension) < pivot) i++; // comparisons += 2; operations++;
            while (j >= lo && particles[j].position(dimension) > pivot) j--; // comparisons += 2; operations++;
            if (i >= j) { // comparisons++;
                return i;
            }
            Array.swap(particles, i, j); // swaps++;
        }
    }

    public void buildTree() {
        buildTree(this.root);
    }

    public void buildTree(Node root) {
        root.lChild = null;
        root.rChild = null;
        isBuilt = false;
        buildTree(0, root);
        isBuilt = true;
    }

    /**
     * Recursively builds the BinaryTree with Nodes based on indices.
     * @param dimension Indicates the dimension at which to partition at this level of the recursion.
     * @param currentNode Node on which the algorithm currently acts upon.
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    public void buildTree(int dimension, Node currentNode) {
        if (currentNode.end - currentNode.start < 8) {
            for (int j = currentNode.start; j <= currentNode.end; j++) {
                particles[j].index = j;
            }
            return;
        }

        double pivot = (currentNode.posMax[dimension] + currentNode.posMin[dimension]) / 2;
        int i = partition(particles, currentNode.start, currentNode.end, pivot, dimension);
        int nextDimension = (dimension + 1) % dimensions();

        // Set left node parameters
        double[] lPosMin = currentNode.posMin.clone();
        double[] lPosMax = currentNode.posMax.clone();
        lPosMax[dimension] = pivot;
        int lStart = currentNode.start;
        int lEnd = i - 1;

        // Set right node parameters
        double[] rPosMin = currentNode.posMin.clone();
        double[] rPosMax = currentNode.posMax.clone();
        rPosMin[dimension] = pivot;
        int rStart = i;
        int rEnd = currentNode.end;

        // Recurse
        if (lEnd - lStart >= 0) {
            currentNode.lChild = new Node(lPosMin, lPosMax, lStart, lEnd, currentNode, this);
            buildTree(nextDimension, currentNode.lChild);
        }
        if (rEnd - rStart >= 0) {
            currentNode.rChild = new Node(rPosMin, rPosMax, rStart, rEnd, currentNode, this);
            buildTree(nextDimension, currentNode.rChild);
        }
    }

    public int dimensions() {
        return particles[0].dimensions();
    }


    public double[] posMin() {
        return root.posMin.clone();
    }
    public double posMin(int index) {
        return root.posMin[index];
    }

    public double[] posMax() {
        return root.posMax.clone();
    }
    public double posMax(int index) {
        return root.posMax[index];
    }

    /**
     * Paint the tree.
     * @param g Graphic from JPanel in which to paint.
     * @param scale Scale for drawing.
     */
    /**
    public void paint(Graphics g, double scale) {
        root.paint(g, scale);
        g.setColor(Color.BLUE);
    }
     /**

    /**
     * Calculates minimal radius squared including the k nearest neighbours.
     * @param pos Center from which to search the k nearest neighbours.
     * @param k Number of particles to search.
     * @return Minimal radius squared.
     */
    IFixedPriorityQueue kNearestNeighbours(double[] pos, int k) {
        IFixedPriorityQueue queue = new FixedPriorityQueue(k);
        kNearestNeighbours(pos, k, root, queue);
        return queue;
    }

    public int[] getIndiceskNearestNeighbours(double[] pos, int k) {
        IFixedPriorityQueue queue = new FixedPriorityQueue(k);
        kNearestNeighbours(pos, k, root, queue);
        return queue.indices();
    }

    public void kNearestNeighbours(double[] pos, int k, Node currentNode, IFixedPriorityQueue queue) {

        if (currentNode.isLeaf()) {
            for (int i = currentNode.start; i <= currentNode.end; i++) {
                queue.insert(particles[i].dist2(pos), i);
            }
        }
        else if (currentNode.hasLeft() && currentNode.hasRight()) {
            if (BoxDist2.metric(pos, currentNode.lChild) < BoxDist2.metric(pos, currentNode.rChild)) {
                kNearestNeighbours(pos, k, currentNode.lChild, queue);
                if(BoxDist2.metric(pos, currentNode.rChild) < queue.max()) {
                    kNearestNeighbours(pos, k, currentNode.rChild, queue);
                }
            }
            else {
                kNearestNeighbours(pos, k, currentNode.rChild, queue);
                if(BoxDist2.metric(pos, currentNode.lChild) < queue.max()) {
                    kNearestNeighbours(pos, k, currentNode.lChild, queue);
                }
            }
        }
        else if (currentNode.hasLeft() && BoxDist2.metric(pos, currentNode.lChild) < queue.max()) {
            kNearestNeighbours(pos, k, currentNode.lChild, queue);
        }
        else if (BoxDist2.metric(pos, currentNode.rChild) < queue.max()) {
            kNearestNeighbours(pos, k, currentNode.rChild, queue);
        }
    }
}

