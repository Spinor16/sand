package data_structures;

import calc.VectorCalculus;
import utils.Drawing;

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

    public void paint2D(Graphics g, double width, double height, double scale){
        int windowHeight = (int)(height*scale);
        double slope = direction[1]/ direction[0];
        double x1 = 0;
        double y1 = -slope*position[0] + position[1];
        double x2 = width;
        double y2 = (x2 - position[0])*slope+position[1];
        Rectangle rect = Drawing.transform2D(x1, y1, width,y2-y1, scale, false);

        g.drawLine(rect.x,windowHeight-rect.y, rect.x + rect.width,windowHeight-(rect.y + rect.height));
    }


}
