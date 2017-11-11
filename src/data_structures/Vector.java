package data_structures;

import org.ejml.simple.SimpleMatrix;

public class Vector extends SimpleMatrix {

    public int dimension;

    public Vector(double[] vector) {
        super(vector.length,1);
        for (int i = 0; i < this.dimension; i++) {
            this.set(i,0,vector[i]);
        }
        this.dimension = vector.length;
    }

}
