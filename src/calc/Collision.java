package calc;

import data_structures.Boundary;
import data_structures.Particle;

public class Collision {
    private static double[] g = {0, -9.81};
    private static double COR = 0.5;
    private static double[] temp = new double[]{0,0};
    private static double[] temp2 = new double[]{0,0};
    private static double[] temp3 = new double[]{0,0};

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

        VectorCalculus.minus(DV,particle1.velocity,particle2.velocity);

        //if DV is 0 there is no collision
        if (VectorCalculus.norm2(DV) == 0){
            return Double.NEGATIVE_INFINITY;
        }

        VectorCalculus.minus(DX,particle1.position,particle2.position);

        //distance squared between particle centers when colliding
        double dist2 = Math.pow(particle1.radius + particle2.radius, 2);


        double a = 0.5 * VectorCalculus.dot(DV,DV);
        double b = VectorCalculus.dot(DX,DV);
        double c = 0.5 * (VectorCalculus.dot(DX,DX) - dist2);

        //fixme: check if in allowed region: no particle overlapping
        return VectorCalculus.sqrt(a, b, c, c < 0);

        //double collisionTime = - (Math.sqrt(discriminant) + VectorCalculus.dot(DV,DX)) / VectorCalculus.norm2(DV);

        //return collisionTime;

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

        //Calculate Delta X and Delta V
        VectorCalculus.minus(DX,boundary.position,particle.position);
        VectorCalculus.minus(DV,particle.velocity, boundary.velocity);

        //Set boundary.normal in same direction as DXn
        int sign = (int) Math.signum(VectorCalculus.dot(DX,boundary.normal));
        VectorCalculus.multSE(sign,boundary.normal);

        //Calculate distance to boundary
        double DXn = VectorCalculus.dot(boundary.normal,DX);

        //Calculate normal velocity component, positive if in direction of boundary
        double DVn = VectorCalculus.dot(boundary.normal,DV);

        //Calculate normal acceleration component
        double gn = VectorCalculus.dot(boundary.normal,g);

        //calculate distance between particle and boundary
        double dist = DXn - particle.radius;

        double a = 0.5 * gn;
        double b = DVn;
        double c = - dist;

        //check if particle overlaps with boundary
        //if yes, shift particle perpendicularly to boundary by a distance dist
        //and return collisionTime = 0 so no collision is processed before this one
//        if (c > 0){
//            VectorCalculus.minusSE(particle.position,VectorCalculus.mult(c,boundary.normal));
//
//            //VectorCalculus.minusSE(particle.velocity, VectorCalculus.mult(2*VectorCalculus.norm(particle.velocity),boundary.normal));
//            //Main.updatePB(particle.index, );
//            return 0;
//        }
        return VectorCalculus.sqrt(a, b, c, dist < 0);
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

        double[] n = temp2; //normed DX
        double[] DV = temp3;
        double dist;

        // Calculate position of collision for particle1 and particle2
        // set positions to the collision positions
        VectorCalculus.plusSE(particle1.position, VectorCalculus.mult(temp,collisionTime, particle1.velocity));
        VectorCalculus.plusSE(particle1.position, VectorCalculus.mult(temp,0.5*collisionTime*collisionTime, g));

        VectorCalculus.plusSE(particle2.position, VectorCalculus.mult(temp,collisionTime, particle2.velocity));
        VectorCalculus.plusSE(particle2.position,VectorCalculus.mult(temp,0.5*collisionTime*collisionTime, g));

        //Calculate velocity at collision position
        VectorCalculus.plusSE(particle1.velocity, VectorCalculus.mult(temp,collisionTime,g));
        VectorCalculus.plusSE(particle2.velocity, VectorCalculus.mult(temp,collisionTime,g));

        //Calculate direction, unit vector connecting ball centers
        VectorCalculus.minus(n,particle1.position,particle2.position);
        double norm = VectorCalculus.norm(n);
        VectorCalculus.divideSE(norm,n);

        //Calculate distance between particle surfaces
        dist = norm-particle1.radius-particle2.radius;


        //Calculate Energy
        double energy = 0.5 * (particle1.mass * VectorCalculus.norm2(particle1.velocity)
                        + particle2.mass * VectorCalculus.norm2(particle2.velocity));

        //Calculate DV
        VectorCalculus.minus(DV,particle1.velocity,particle2.velocity);

        //For convenience calculate in advance
        double mass_term = (1/particle1.mass + 1/particle2.mass);
        double dot = VectorCalculus.dot(n,DV) / 2. / mass_term;
        double collisionMomentum = - dot + Math.sqrt(dot * dot - (COR - 1) * energy / mass_term);


        //Calculate new velocity particle 1
        VectorCalculus.plusSE(particle1.velocity, VectorCalculus.mult(temp, collisionMomentum / particle1.mass, n));

        //Calculate new velocity particle 2
        VectorCalculus.minusSE(particle2.velocity, VectorCalculus.mult(temp,collisionMomentum / particle2.mass, n));


        //check if particle overlaps with particle
        //if yes, shift particle perpendicularly to boundary by a distance dist
        //and return collisionTime = 0 so no collision is processed before this one
        if (dist < 0){
            double dv = VectorCalculus.norm(DV);
            VectorCalculus.plusSE(particle1.position, VectorCalculus.mult(temp,dist / dv, n));
        }

        //Project back velocities
        VectorCalculus.minusSE(particle1.velocity,VectorCalculus.mult(temp,collisionTime,g));
        VectorCalculus.minusSE(particle2.velocity,VectorCalculus.mult(temp,collisionTime,g));

        //Project back positions
        VectorCalculus.minusSE(particle1.position,VectorCalculus.mult(temp,collisionTime,particle1.velocity));
        VectorCalculus.minusSE(particle1.position,VectorCalculus.mult(temp,0.5*collisionTime*collisionTime,g));

        VectorCalculus.minusSE(particle2.position,VectorCalculus.mult(temp,collisionTime,particle2.velocity));
        VectorCalculus.minusSE(particle2.position,VectorCalculus.mult(temp,0.5*collisionTime*collisionTime,g));




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
    public static void resolveCollision(Particle particle, Boundary boundary, double collisionTime){

        double[] Vt = temp2;

        //Calculate collision position
        VectorCalculus.plusSE(particle.position, VectorCalculus.mult(temp,collisionTime, particle.velocity));
        VectorCalculus.plusSE(particle.position,VectorCalculus.mult(temp,0.5*collisionTime*collisionTime, g));

        //Calculate velocity at collision position
        VectorCalculus.plusSE(particle.velocity, VectorCalculus.mult(temp,collisionTime, g));

        //Calculate tangential component
        VectorCalculus.mult(Vt,VectorCalculus.dot(boundary.direction,particle.velocity),boundary.direction);

        //make sure Vt is parallel to particle.velocity
        int sign = (int) Math.signum(VectorCalculus.dot(Vt,particle.velocity));
        VectorCalculus.mult(sign,Vt);

        double vi2 = VectorCalculus.norm2(particle.velocity);

        //Update to velocity after collision
        particle.velocity = VectorCalculus.minus(VectorCalculus.mult(temp,2, Vt),particle.velocity);

        //Consider energy loss
        //first normalize particle.velocity
        particle.velocity = VectorCalculus.divide(VectorCalculus.norm(particle.velocity),particle.velocity);
        //then multiply with energy loss corrected absolute value
        VectorCalculus.multSE(Math.sqrt((1- COR)*vi2), particle.velocity);

        //Project back velocity
        VectorCalculus.minusSE(particle.velocity,VectorCalculus.mult(temp,collisionTime,g));

        //Project back position
        VectorCalculus.minusSE(particle.position,VectorCalculus.mult(temp,collisionTime,particle.velocity));
        VectorCalculus.minusSE(particle.position,VectorCalculus.mult(temp,0.5*collisionTime*collisionTime,g));
    }

    public static void projectForwardParticle(Particle particle, double timeStep){
        VectorCalculus.plusSE(particle.position,VectorCalculus.mult(temp,timeStep,particle.velocity));
        VectorCalculus.plusSE(particle.position,VectorCalculus.mult(temp,0.5*timeStep*timeStep,g));

        VectorCalculus.plusSE(particle.velocity,VectorCalculus.mult(temp,timeStep,g));


    }

    public static void projectForwardBoundary(Boundary boundary, double timeStep){
        VectorCalculus.plusSE(boundary.position,VectorCalculus.mult(temp,timeStep,boundary.velocity(timeStep)));
    }
}
