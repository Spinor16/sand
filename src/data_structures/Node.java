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
    public double[] centerOfMass;
    public double RMax;
    public double mass;
    public double trace;
    public double[][] multMoment;
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

    public boolean contains(int pNumber) { return (pNumber >= start && pNumber <= end); }

    public boolean hasLeft() {
        return lChild != null;
    }

    public boolean hasRight() {
        return rChild != null;
    }

    public double dist2(double[] pos){
        double dist2 = 0;
        for (int i = 0; i < pos.length; i++){
            if (pos[i] < posMin[i])
            {dist2 += Math.pow(posMin[i] - pos[i], 2); }
            else if (pos[i] > posMax[i])
            {dist2 += Math.pow(posMax[i] - pos[i], 2); }
            else
            {dist2 += 0; }
        }
        return dist2;
    }

/*    void paint(Graphics g, double scale) {
        if (isLeaf()) {
            double x = posMin[0] * scale;
            double y = posMin[1] * scale;
            double width = (posMax[0] - posMin[0]) * scale;
            double height = (posMax[1] - posMin[1]) * scale;
            Rectangle scaledValues = Drawing.transform2D(posMin[0], posMin[1], posMax[0] - posMin[0],
                    posMax[1] - posMin[1], scale);
            g.setColor(Color.BLACK);
            g.drawRect(scaledValues.x, scaledValues.y, scaledValues.width, scaledValues.height);
            Random rand = new Random();
//            g.setColor(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
            for (int i = start; i <= end; i++) {
                tree.particles[i].paint(g, scale, 7);
            }
        } else {
            if (hasLeft()) {
                lChild.paint(g, scale);
            }
            if (hasRight()) {
                rChild.paint(g, scale);
            }
        }
    }*/

    void buildTreeImage(DefaultMutableTreeNode parent){
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("start = " +start+", end= "+end);
        parent.add(node);
        if(hasLeft()) {
            lChild.buildTreeImage(node);
        }
        if (hasRight()) {
            rChild.buildTreeImage(node);
        }
    }


    public static double pythaDist2(double[] pos1, double[] pos2){
        double pythaDist2 = 0;
        for (int i = 0; i < pos1.length; i++) {
            pythaDist2 += Math.pow( pos1[i]-pos2[i], 2);
        }
        return pythaDist2;
    }

}