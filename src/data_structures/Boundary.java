package data_structures;

import calc.VectorCalculus;

import java.awt.*;

public class Boundary implements CollisionPartner{
    public double[] position;
    public double[] direction; //direction
    public double[] normal; //orthogonal to direction
    public double[] velocity;

    public Boundary(double[] velocity, double[] position, double[] direction) {
        this.direction = direction;
        VectorCalculus.divideSE(VectorCalculus.norm(this.direction),this.direction);
        this.position = position;
        this.normal = VectorCalculus.orthogonal(this.direction);
        VectorCalculus.divideSE(VectorCalculus.norm(normal),this.normal);
        this.velocity = velocity;
    }

    public double[] velocity(double time){
        return this.velocity;
    }

    public void paint2D(Graphics g, int width, int height, double scale){
        double slope = direction[1]/ direction[0];
        int x1 = 0;
        int y1 = (int)((-slope*position[0]*scale + position[1]*scale));
        int x2 = width;
        int y2 = (int)(((width - position[0]*scale)*slope+position[1]*scale));
//        IO.print(x1);
//        IO.print(x2);
//        IO.print(y1);
//        IO.print(y2);
        g.drawLine(x1,y1,x2,y2);
    }


}
