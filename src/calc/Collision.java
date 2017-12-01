package calc;

import data_structures.Boundary;
import data_structures.Particle;
import main.Main;
import utils.IO;

public class Collision {
    public static final double[] g = {0, -9.81};
    public static final double COR = 0.7;
    public static final double EPS = 1e-7;
    private static double vCrit = 1e-1;
    private static double distCrit = 1e-2;
    private static double[] temp = new double[]{0,0};
    private static double[] temp2 = new double[]{0,0};
    private static double[] temp3 = new double[]{0,0};
    private static double[] temp4 = new double[]{0,0,0,0,0};
    private static double[] temp5 = new double[]{0,0};
    private static double[] temp6 = new double[]{0,0};
    /**
     *
     * @param particle1
     * @param particle2
     * @return collisionTime
     *
     * Calculates the collision time of two particles.
     * This works only for 2dim.
     * If no valid collision time is found, a negative value is returned.
     */

    public static double findCollisionTime(Particle particle1, Particle particle2){

        //D for Delta
        double[] DV = temp2;
        double[] DX = temp3;

        VectorCalculus.minus(DV,particle1.getVelocity(),particle2.getVelocity());

        //if DV is 0 there is no collision
        if (VectorCalculus.norm2(DV) == 0){
            return -1;
        }

        VectorCalculus.minus(DX,particle1.getPosition(),particle2.getPosition());

        //distance squared between particle centers when colliding
        double dist2 = Math.pow(particle1.getRadius() + particle2.getRadius(), 2);

        double a = 0.5 * VectorCalculus.dot(DV,DV);
        double b = VectorCalculus.dot(DX,DV);
        double c = 0.5 * (VectorCalculus.dot(DX,DX) - dist2);
        double collisionTime = VectorCalculus.sqrt(a, b, c);

        if (particle1.isOnBoundary() || particle2.isOnBoundary()){

            //fixme: should test for existence of root

            double[] coeffs = temp4;
            double DG[] = temp5;
            VectorCalculus.minus(DG,particle1.gOnBoundary(),particle2.gOnBoundary());

            coeffs[0] = VectorCalculus.dot(DG,DG) / 4;

            coeffs[1] = VectorCalculus.dot(DG,DV);

            coeffs[2] = coeffs[1] + 2 * a;

            coeffs[3] = 2 * b;

            coeffs[4] = 2 * c;

            collisionTime = VectorCalculus.newtonRootsPoly4(coeffs,collisionTime,EPS);
        }

        return collisionTime;

    }

    /**
     *
     * @param particle
     * @param boundary
     * @return collisionTime
     *
     * Calculates the Collision time of a linear boundary and a particle.
     * This works only for 2dim.
     * If no valid collision time is found, the method returns a negative value.
     *
     */
    //
    public static double findCollisionTime(Particle particle, Boundary boundary){

        //D for Delta
        double[] DV = temp2;
        double[] DX = temp3;

        if (particle.isOnBoundary() && particle.touchingBoundaries.contains(boundary)){
            return -1;
        }

        //Calculate Delta X and Delta V
        VectorCalculus.minus(DX,boundary.getPosition(),particle.getPosition());
        VectorCalculus.minus(DV,particle.getVelocity(), boundary.getVelocity());

        //Set boundary.normal in same direction as DXn
        int sign = (int) Math.signum(VectorCalculus.dot(DX,boundary.getNormal()));
//        boundary.switchNormalSignTo(sign);


        //Calculate normal velocity component, positive if in direction of boundary
        double DVn = sign*VectorCalculus.dot(boundary.getNormal(),DV);

        //Calculate normal acceleration component
        double gn = sign*VectorCalculus.dot(boundary.getNormal(),g);

        //calculate distance between particle and boundary
        double dist = sign*getDist(particle,boundary);

        double a = 0.5 * gn;
        double b = DVn;
        double c = - dist;


        double collisionTime = VectorCalculus.sqrt(a, b, c);

        return collisionTime;
    }

    /**
     *
     * @param particle1
     * @param particle2
     * @param collisionTime
     *
     * Particle1 and particle2 are updated to the outgoing velocities. Their position and velocity are projected
     * backwards in time, such that after projecting forwards
     *            particle.position += collisionTime * particle.velocity + 0.5 * collisionTime^2 * g
     *            particle.velocity += collisionTime * g
     * yields the outgoing state of
     * the collision and particle.velocity .
     *
     *
     */
    public static void resolveCollision(Particle particle1, Particle particle2, double collisionTime){

        double[] n = temp2;
        double[] DV = temp3;

        // Calculate position of collision for particle1 and particle2
        // set positions to the collision positions
        //Calculate velocity at collision position

        particle1.projectForward(collisionTime);
        particle2.projectForward(collisionTime);
        checkIfOnBoundariesAndSet(particle1, Main.boundaries);
        checkIfOnBoundariesAndSet(particle2, Main.boundaries);

        //Calculate direction, unit vector connecting ball centers
        VectorCalculus.minus(n,particle1.getPosition(),particle2.getPosition());
        VectorCalculus.divideSE(VectorCalculus.norm(n),n);

        //Calculate Energy
        double energy = 0.5 * (particle1.getMass() * VectorCalculus.norm2(particle1.getVelocity())
                        + particle2.getMass() * VectorCalculus.norm2(particle2.getVelocity()));

        //Calculate DV
        VectorCalculus.minus(DV,particle1.getVelocity(),particle2.getVelocity());

        //For convenience calculate in advance
        double mass_term = (1/particle1.getMass() + 1/particle2.getMass());
        double dot = VectorCalculus.dot(n,DV) / 2. / mass_term;
        double collisionMomentum = - dot + Math.sqrt(dot * dot - (COR - 1) * energy / mass_term);

        //Calculate new velocity particle 1 and 2
        particle1.collideWithParticle(VectorCalculus.mult(temp, collisionMomentum / particle1.getMass(), n));
        particle2.collideWithParticle(VectorCalculus.mult(temp,- collisionMomentum / particle2.getMass(), n));


        //Project back velocities
        particle1.projectBackward(collisionTime);
        particle2.projectBackward(collisionTime);



    }

    /**
     *
     * @param particle
     * @param boundary
     * @param collisionTime
     *
     * Particle1 is updated to the outgoing velocity. Its position and velocity are projected
     * backwards in time, such that after projecting forwards
     *            particle.position += collisionTime * particle.velocity + 0.5 * collisionTime^2 * g
     *            particle.velocity += collisionTime * g
     * yields the outgoing state of the collision.
     *
     */
    public static void resolveCollision(Particle particle, Boundary boundary, double collisionTime) {

        double[] Vt = temp2;

        //Calculate collision position
        particle.projectForward(collisionTime);
        checkIfOnBoundariesAndSet(particle, Main.boundaries);

        //Calculate tangential component
        Vt = getVt(particle, boundary);

        particle.collideWithBoundary(Vt);

        particle.projectBackward(collisionTime);
    }


    public static double[] getVt(Particle particle, Boundary boundary){
        double[] Vt = temp2;

        VectorCalculus.mult(Vt, VectorCalculus.dot(boundary.getDirection(), particle.getVelocity()), boundary.getDirection());
        int sign = (int) Math.signum(VectorCalculus.dot(Vt, particle.getVelocity()));

        VectorCalculus.multSE(sign, Vt);

        return Vt;
    }

    public static double getVn(Particle particle, Boundary boundary){
        double[] DX = temp3;

        //Calculate Delta X and Delta V
        VectorCalculus.minus(DX,boundary.getPosition(),particle.getPosition());

        //Set boundary.normal in same direction as DXn
        int sign = (int) Math.signum(VectorCalculus.dot(DX,boundary.getNormal()));
        //boundary.switchNormalSignTo(sign);

        return sign*VectorCalculus.dot(particle.getVelocity(),boundary.getNormal());
    }


    public static double[] getGn(Boundary boundary){
        double[] gn = temp3;
        double gnDot = VectorCalculus.dot(Collision.g,boundary.getNormal());

        int sign = (int) Math.signum(gnDot);

        return VectorCalculus.mult(gn, - gnDot, boundary.getNormal());

    }


    /**
     * Calculates the distance between a boundary and the surface of a particle. Assumes boundary.normal is set
     * outwards, away from the particle.
     *
     * @param particle
     * @param boundary
     * @return
     */
    public static double getDist(Particle particle, Boundary boundary){
        double[] DX = temp3;

        //Calculate Delta X and Delta V
        VectorCalculus.minus(DX,boundary.getPosition(),particle.getPosition());

        //Set boundary.normal in same direction as DXn
        int sign = (int) Math.signum(VectorCalculus.dot(DX,boundary.getNormal()));
//        boundary.switchNormalSignTo(sign);

        //Calculate distance to boundary
        double DXn = Math.abs(VectorCalculus.dot(boundary.getNormal(),DX));

        //calculate distance between particle and boundary
        if (DXn > particle.getRadius()){
            return sign*(DXn - particle.getRadius());
        }
        else{
            return sign*(DXn + particle.getRadius());
        }
    }


    public static void checkIfOnBoundariesAndSet(Particle particle, Boundary[] boundaries){
        for (Boundary boundary : boundaries) {
            checkIfOnBoundaryAndSet(particle,boundary);
        }
    }

    public static void checkIfOnBoundaryAndSet(Particle particle, Boundary boundary){

        double dist = getDist(particle, boundary);
        double vn = getVn(particle, boundary);
        if (dist < distCrit && Math.abs(vn) < vCrit && !particle.touchingBoundaries.contains(boundary)){
            particle.setOnBoundary(boundary);
            particle.touchingBoundaries.add(boundary);
        }
        else if ((dist > distCrit || Math.abs(vn) > vCrit) && particle.touchingBoundaries.contains(boundary)){
            particle.touchingBoundaries.remove(boundary);
        }

    }

}
