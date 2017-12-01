package data_structures;

import calc.VectorCalculus;
import utils.Drawing;

import java.awt.*;

public class Boundary implements CollisionPartner{
    private double[] position;
    private double[] direction; //direction
    private double[] normal; //orthogonal to direction
    private static double[] temp = new double[]{0,0};
    private double[] velocity;

    public Boundary(double[] velocity, double[] position, double[] direction) {
        this.direction = direction;
        VectorCalculus.divideSE(VectorCalculus.norm(this.direction),this.direction);
        this.position = position;
        this.normal = VectorCalculus.orthogonal(this.direction);
        VectorCalculus.divideSE(VectorCalculus.norm(normal),this.normal);
        this.velocity = velocity;
    }

    public double[] getPosition() {
        return position;
    }

    public double[] getDirection() {
        return direction;
    }

    public double[] getNormal() {
        return normal;
    }

    public double[] getVelocity() {
        return velocity;
    }

    public void switchNormalSignTo(int sign){
        VectorCalculus.multSE(sign,normal);
    }

    public void projectForward(double time){
        VectorCalculus.plusSE(position,VectorCalculus.mult(temp,time,velocity));
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
