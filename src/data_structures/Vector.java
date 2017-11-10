package data_structures;

import org.ejml.simple.SimpleMatrix;

public class Vector extends SimpleMatrix {

    SimpleMatrix vector;
    public int dimension;

    public Vector(double[] vector) {
        this.dimension = vector.length;
        this.vector = new SimpleMatrix(dimension,1);
        for (int i = 0; i < this.dimension; i++) {
            this.vector.set(i,0,vector[i]);

        }
    }

}
