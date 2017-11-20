package data_structures;

import org.omg.CORBA.MARSHAL;
import org.w3c.dom.css.Rect;

import javax.swing.*;
import java.awt.*;

public class InitialConditions extends JPanel {
    private static final long serialVersionUID = 1L;  //used for JPanel
    /*
    defines how the initial situation is supposed to look like. includes:
    - where can particles in the beginning be?
    - how does the box around particles look like? shape, dimensions, ect.
    assumptions (for now):
    - particle_radius = 1 for all particles
    - walls don't move
    - situation looks like this:

          w
     ___________
     ppppppppppp    |
                    |
   \              / |  h
    \           /   |
     \        /     |
     l \  a / l     |
         \/         |
         0
    w: width
    h:height
    a:angle alpha
    :length of each lower wall
    0: origin, xaxis to right, yaxis down (as in Jpanel)
    p: possible particle position, with index
    rn: roughness, how many wall points per particle diameter distance? (many points->smooth) constant for all walls

     */

    //constants
    public final double pRadius = 1.;
    public final double[] origin = {0,0};
    public double width;
    public double height;
    public double angle;
    public final double particleDistanceInitial = 0.01; // around the particle, in units of the radius
    public final int nParticles;

    //boundary array
    //public Boundary[] boundaryPoints;
    //public Boundary[] upperWall;
    //public Boundary[] leftWall;
    //public Boundary[] rightWall;

    private Boundary[] boundaries;
    private Boundary lowerWallL;
    private Boundary lowerWallR;

    //particle array
    private Particle[] particles;

    public InitialConditions(double width, double height, double angle) {
        /*
        width and height of box, should be larger than double the radius of the particle (r=1).
        Angle between 0 and pi
         */
        setWidth(width);
        setHeight(height);
        setAngle(angle);

        nParticles = (int) (width/(2*particleDistanceInitial+pRadius));
        particles = new Particle[nParticles];

        makeBoundary();
        makeParticles();
    }

    public void setWidth(double width){
        width = width;
    }

    public void setHeight(double height){
        height = height;
    }

    public void setAngle(double angle){
        angle = angle;
    }

    public Boundary[] getBoundaries() {
        return boundaries;
    }

    public Particle[] getParticles() {
        return particles;
    }

    private void makeBoundary(){
        int dimensions = 2;

//        upperWall = new Boundary[dimensions];
//        leftWall = new Boundary[dimensions];
//        rightWall = new Boundary[dimensions];

        double[] lWallVelocity = {0,0};
        double[] lWallPosition = origin;
        double[] lWallDirection = {Math.sin(angle/2), Math.cos(angle/2)};
        lowerWallL = new Boundary(lWallVelocity, lWallPosition, lWallDirection);

        double[] rWallVelocity = {0,0};
        double[] rWallPosition = origin;
        double[] rWallDirection = {-Math.sin(angle/2), Math.cos(angle/2)};
        lowerWallR = new Boundary(rWallVelocity, rWallPosition, rWallDirection);

        boundaries[0] = lowerWallL;
        boundaries[1] = lowerWallR;
    }

    private void makeParticles() {
        for (int i = 0; i < particles.length; i++){
            /*
            Set the particles in one line along the whole width. Works in 2D
             */
            particles[i].position[0] = origin[0]-width/2.+(i+1)*(2*particleDistanceInitial+pRadius);
            particles[i].position[1] = origin[1]+height;
        }
    }
}
