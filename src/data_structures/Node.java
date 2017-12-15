package data_structures;

//import dist.BoxDist2;
import utils.Drawing;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Random;

/**
 * Basic Container for the BinaryTree.
 */
@SuppressWarnings("SpellCheckingInspection")
public class Node {
    public final BinaryTree tree;
    /**
     *
     */
    public final double[] posMin, posMax;
    /**
     * start - first index
     * end - inclusive last index of indices included in this node. (may not lay outside of array)
     */
    public final int start, end;
    public Node lChild, rChild;
    public Node parent;


    Node(double[] posMin, double[] posMax, int start, int end, Node parent, BinaryTree binaryTree) {
        this.tree = binaryTree;
        assert posMax.length == posMin.length;
        this.posMin = posMin;
        this.posMax = posMax;
        this.start = start;
        this.end = end;
        this.parent = parent;
    }


    boolean isLeftChild() {
        return this == parent.lChild;
    }

    public boolean isLeaf() {
        return !(hasLeft() || hasRight());
    }

    public boolean contains(int pNumber) {
        return (pNumber >= start && pNumber <= end);
    }

    public boolean hasLeft() {
        return lChild != null;
    }

    public boolean hasRight() {
        return rChild != null;
    }

    public double dist2(double[] pos) {
        double dist2 = 0;
        for (int i = 0; i < pos.length; i++) {
            if (pos[i] < posMin[i]) {
                dist2 += Math.pow(posMin[i] - pos[i], 2);
            } else if (pos[i] > posMax[i]) {
                dist2 += Math.pow(posMax[i] - pos[i], 2);
            } else {
                dist2 += 0;
            }
        }
        return dist2;
    }
}