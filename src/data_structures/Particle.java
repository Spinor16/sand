package data_structures;

import calc.Collision;
import calc.VectorCalculus;
import utils.Drawing;

import java.awt.Color;

import java.awt.*;
import java.util.ArrayList;





public class Particle implements CollisionPartner{
    public double[] position;
    public double[] velocity;

    public final double mass;
    public final double radius;
    public int index;

    public int colorIndex;

    public static final double RESTITUTION_VELOCITY = 0.1;

    public ArrayList<Boundary> touchingBoundaries = new ArrayList<>();

    private double[] temp = new double[]{0,0};

    public Particle(double[] position, double[] velocity) {
        //mass
        this.mass = 1;

        this.radius = 0.01;

        //position
        this.position = position;

        //velocity
        this.velocity = velocity;

    }

    int dimensions() { return position.length; }

    public double mass() { return mass; }

    public double position(int i) { return position[i]; }

    public double velocity(int i) { return velocity[i]; }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    //distance_squared between own position and vector2
    public double dist2(double[] vector2){
        return dist2(this.position, vector2);
    }

    //distance between two vectors
    public double dist2(double[] vector1, double[] vector2) {
        double norm2 = 0;
        for (int i = 0; i < vector1.length; i++) {
            norm2 = (vector1[i]-vector2[i])*(vector1[i]-vector2[i]);
        }
        return norm2;
    }

    public void paint2D(Graphics g, double scale, Rectangle bounds, int nParticles) {
        Rectangle scaledValues = Drawing.transform2D(position[0], position[1], 2*radius, 2*radius, scale, bounds, true);

        //color coupled to index, probably not optimal
        Color myColor = new Color((int)((double)colorIndex/nParticles*155), (int)((double)colorIndex/nParticles*205),(int)((double)colorIndex/nParticles*255));
        g.setColor(myColor);

        if(colorIndex==0){g.setColor(Color.RED);}
        if(isOnBoundary()){g.setColor(Color.YELLOW);}

        g.fillOval(scaledValues.x , scaledValues.y, scaledValues.width, scaledValues.height);
    }

    public boolean checkIfOnBoundary(Boundary boundary){
        double dist = Collision.getDist(this, boundary);
        double vn = Collision.getVn(this, boundary);
        if ((dist < 0 && vn > 0)){
            return true;
        }
        else{
            return false;
        }
//        return false;
    }

    public boolean isOnBoundary(){
        return !touchingBoundaries.isEmpty();
    }

    public void setTouchingBoundary(Boundary boundary){
        reflectOnBoundary(boundary);
        if (!touchingBoundaries.contains(boundary)){
            touchingBoundaries.add(boundary);
        }
        else{
            return;
        }

    }

    public double[] gOnBoundary(){
        double[] g = Collision.g.clone();
        if (isOnBoundary()){
            for (Boundary boundary : touchingBoundaries) {
                VectorCalculus.minusSE(g, VectorCalculus.mult(temp, VectorCalculus.dot(g,boundary.normal),boundary.normal));
            }

        }
        return g;
    }

    public void reflectOnBoundary(Boundary boundary){
        double vn = Collision.getVn(this, boundary);
        if (vn > 0){
            double reflectionVelocity = Math.max(2 * vn, RESTITUTION_VELOCITY);
            VectorCalculus.minusSE(velocity, VectorCalculus.mult(temp, reflectionVelocity, boundary.normal));
        }
    }
}
