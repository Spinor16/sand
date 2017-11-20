package data_structures;

import calc.VectorCalculus;

import java.awt.*;

public class Boundary {
    public double[] position;
    public double[] n; //direction
    public double[] t; //orthogonal to direction
    public double[] velocity;

    public Boundary(double[] velocity, double[] position, double[] direction) {
        this.n = direction;
        VectorCalculus.divideSE(VectorCalculus.norm(n),this.n);
        this.position = position;
        this.t = VectorCalculus.orthogonal(n);
        VectorCalculus.divideSE(VectorCalculus.norm(t),this.t);
        this.velocity = velocity;
    }

    public double[] velocity(double time){
        return this.velocity;
    }

    public void paint2D(Graphics g, int width, int height){
        double slope = n[1]/n[0];

        g.drawLine((int)(position[0]*slope),(int) (-1*position[0]/slope),
                   (int)((position[0]-height)*slope),(int)((width - position[0])/slope));
    }


}
