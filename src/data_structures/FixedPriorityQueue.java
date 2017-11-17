package data_structures;

public class FixedPriorityQueue implements IFixedPriorityQueue {
    private int[] indices;  // indices of indices in indices array.
    private double[] r2List;  // distance squared between indices and positions.
    private int r2Max; // Index of maximal r2 in r2List.

    public FixedPriorityQueue(int size) {
        indices = new int[size];
        r2List = new double[size];
        r2Max = 0;

        for (int i = 0; i < indices.length; i++) {
            indices[i] = -1;
            r2List[i] = Double.POSITIVE_INFINITY;
        }
    }

    @Override
    public void insert(double r2, int index) {
        if (r2 < r2List[r2Max]) {
            r2List[r2Max] = r2;
            indices[r2Max] = index;

            // Linear search for new r2Max;
            double max = 0;
            for (int i = 0; i < r2List.length; i++) {
                if (r2List[i] > max) {
                    max = r2List[i];
                    r2Max = i;
                }
            }
        }
    }

    @Override
    /**
     * @return maximal radius square in queue.
     */
    public double max() {
        return r2List[r2Max];
    }

    /**
     * @return Array of indices corresponding to indices array in BinaryTree.
     */
    public int[] indices() {
        return indices.clone();
    }
}
