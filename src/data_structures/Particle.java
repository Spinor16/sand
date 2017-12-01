package data_structures;

import calc.Collision;
import calc.VectorCalculus;
import utils.Drawing;

import java.awt.Color;

import java.awt.*;
import java.util.ArrayList;

public class Particle implements CollisionPartner{
    private double[] position;
    private double[] velocity;
    private static double[] temp = new double[]{0,0};
    private static double[] temp2 = new double[]{0,0};

    private final double mass;
    private final double radius;
    private int index;
    public ArrayList<Boundary> touchingBoundaries = new ArrayList<>();

    public int colorIndex;
    //private double speedFactor;

    public Particle(double[] position, double[] velocity) {
        //mass
        this.mass = 1;

        this.radius = 0.01;

        //position
        this.position = position;

        //velocity
        this.velocity = velocity;

    }
    public double[] getVelocity() { return velocity;}

    public void setVelocity(int i, double value) { velocity[i] = value; }

    public double[] getPosition() { return position; }

    public void setPosition(int i, double value) { position[i] = value; }

    public double getMass(){ return mass;}

    public double getRadius() { return radius;}

    public int getIndex(){ return index;}

    public void setIndex(int i){ index = i; }

    public boolean isOnBoundary() { return !touchingBoundaries.isEmpty(); }

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
            norm2 = (vector1[i] - vector2[i])*(vector1[i] - vector2[i]);
        }
        return norm2;
    }

    public void updatePosition(double[] summand){
        VectorCalculus.plusSE(position, summand);
    }

    public void updateVelocity(double[] summand){
        VectorCalculus.plusSE(velocity, summand);
    }

    public void collideWithParticle(double[] collisionVelocity){
        updateVelocity(collisionVelocity);
        if (isOnBoundary()){
            //simulate collision with boundary
            //fixme: there is no energy loss in PB collision
            for (Boundary boundary : touchingBoundaries) {
                updateVelocity(VectorCalculus.mult(temp,- 2 * Collision.getVn(this,boundary),boundary.getNormal()));
                setOnBoundary(boundary);
            }

        }
    }

    public void setOnBoundary(Boundary boundary){
        updateVelocity(VectorCalculus.mult(temp, - Collision.getVn(this,boundary), boundary.getNormal()));
//        updatePosition(VectorCalculus.mult(temp, - Collision.getDist(this, boundary), boundary.getNormal()));
    }

    public void collideWithBoundary(double[] Vt){
        double vi2 = VectorCalculus.norm2(velocity);

        VectorCalculus.minus(velocity, VectorCalculus.mult(temp,2, Vt), velocity);

        //Consider energy loss
        //first normalize particle.velocity
        //then multiply with energy loss corrected absolute value
        VectorCalculus.divideSE(VectorCalculus.norm(velocity), velocity);
        VectorCalculus.multSE(Math.sqrt((1 - Collision.COR) * vi2), velocity);

    }

    public double[] gOnBoundary(){
        double[] g = Collision.g.clone();
        if (isOnBoundary()){
            for (Boundary boundary : touchingBoundaries) {
                VectorCalculus.plusSE(g, Collision.getGn(boundary));
            }

        }
        return g;
    }

    public void projectForward(double time){
        double[] g = gOnBoundary();
        updatePosition(VectorCalculus.mult(temp, time, velocity));
        updatePosition(VectorCalculus.mult(temp,0.5 * time * time, g));

        updateVelocity(VectorCalculus.mult(temp, time, g));
    }

    public void projectBackward(double time){

        double[] g = Collision.g.clone();
        if (isOnBoundary()){
            for (Boundary boundary : touchingBoundaries) {
                VectorCalculus.plusSE(g, Collision.getGn(boundary));
            }
        }

        updateVelocity(VectorCalculus.mult(temp, - time, g));

        updatePosition(VectorCalculus.mult(temp, - time, velocity));
        updatePosition(VectorCalculus.mult(temp,- 0.5 * time * time, g));

    }

    public void paint2D(Graphics g, double scale, int height, int nParticles) {
        Rectangle scaledValues = Drawing.transform2D(position[0], position[1], 2 * radius, 2 * radius, scale,true);

        //color coupled to index, probably not optimal
        Color myColor = new Color((int)((double) colorIndex / nParticles * 155), (int)((double) colorIndex / nParticles * 205),(int)((double) colorIndex / nParticles * 255));
        g.setColor(myColor);

        if(colorIndex == 0){g.setColor(Color.RED);}

        g.fillOval(scaledValues.x , height - scaledValues.y, scaledValues.width, scaledValues.height);
    }
}