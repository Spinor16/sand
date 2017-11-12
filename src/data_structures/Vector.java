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

    public Vector mult(double b){
        return (Vector) this.divide(1/b);

    }

    public double norm2(){
        return this.transpose().dot(this);
    }

    public double norm(){
        return Math.sqrt(this.norm2());
    }

    public static Vector rotate(double alpha,SimpleMatrix matrix){
        SimpleMatrix rotMatrix = new SimpleMatrix(new double[][]
                                          {{Math.cos(alpha), Math.sin(alpha)},
                                          {-Math.sin(alpha), Math.cos(alpha)}});

        return (Vector) rotMatrix.mult(matrix);
    }

    public double get(int index){
        return this.get(index,0);
    }

    public void set(int index,double value){
        this.set(index,value);
    }

}
