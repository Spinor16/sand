package data_structures;

import calc.VectorCalculus;

public class Boundary {
    public VectorCalculus x0;
    public VectorCalculus direction;

    public Boundary(VectorCalculus velocity, VectorCalculus x0, VectorCalculus direction) {
        this.direction = direction;
        this.x0 = x0;
    }


}
