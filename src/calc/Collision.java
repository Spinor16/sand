package calc;

import data_structures.BinaryTree;
import data_structures.Boundary;
import data_structures.CollisionHeap;
import data_structures.Particle;

public class Collision {
    private static double[] g = {0, 9.81};
    private static double DE = 0.7;
    private static double[] temp = new double[]{0,0};

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
        double[] DV = VectorCalculus.minus(particle1.velocity,particle2.velocity);

        //if DV is 0 there is no collision
        if (VectorCalculus.norm(DV) == 0){
            return -1;
        }

        double[] DX = VectorCalculus.minus(particle1.position,particle2.position);

        //Elements squared
        double[] DV2 = VectorCalculus.elementPower(DV,2);
        double[] DX2 = VectorCalculus.elementPower(DX,2);

        //distance squared between particle centers when colliding
        double dist2 = Math.pow(particle1.radius + particle2.radius,2);


        double determinant = (dist2 - VectorCalculus.norm2(DX)) * VectorCalculus.norm2(DV)
                            + VectorCalculus.dot(DV2,DX2);

        if (determinant < 0){
            return -1;
        }


        double collisionTime = (Math.sqrt(determinant) - VectorCalculus.dot(DV,DX)) / VectorCalculus.norm2(DV);
        return collisionTime;

    }

    /**
     *
     * @param particle
     * @param boundary
     * @return collisionTime
     *
     * Calculates the Collision time of a linear boundary and a particle. This works for a non-moving boundary only, since
     * the positional time invariance of the boundary needs to be satisfied when calculating the time back from the
     * intersection of boundary.direction and particle.velocity.
     *
     * This works only for 2dim.
     * If no valid collision time is found, the method returns a negative value.
     *
     */
    //
    public static double findCollisionTime(Particle particle, Boundary boundary){

        //Calculate Delta X
        double[] DX = VectorCalculus.minus(boundary.position,particle.position);

        //Check in which direction the normal vector points, and change it such that it points outwards of boundary
        //fixme: needs to be done only once actually, not for each particle
        int sign = (int) Math.signum(VectorCalculus.dot(boundary.t,DX));
        VectorCalculus.mult(sign,boundary.t);

        //Calculate normal velocity component
        double DVn = VectorCalculus.dot(VectorCalculus.minus(boundary.velocity, particle.velocity),boundary.t);

        if (DVn <= 0){
            return -1;
        }

        //Calculate normal acceleration component
        double gt = VectorCalculus.dot(boundary.t,g);

        //calculate distance between particle and boundary
        double dist = VectorCalculus.dot(boundary.t, DX) - particle.radius;

        double determinant = Math.pow(2 * DVn / gt, 2) - 8 * dist / gt;

        if (determinant < 0){
            return -1;
        }

        double collisionTime = 2 * DVn / gt + Math.sqrt(determinant);

        if (collisionTime > 0) {
            return collisionTime;
        }
        else {
            return 2 * DVn / gt - Math.sqrt(determinant);
        }
    }

    /**
     *
     * @param particle1
     * @param particle2
     * @param collisionTime
     *
     * Particle1 and particle2 are updated to the outgoing velocities. Their position is projected backwards in time,
     * such that after projecting forwards
     *            particle.position + collisionTime * particle.velocity + 0.5 * collisionTime^2 * g
     * yields the outgoing state of
     * the collision and particle.velocity .
     *
     *
     */
    public static void resolveCollision(Particle particle1, Particle particle2, double collisionTime){
        //

        // Calculate position of collision for particle1 and particle2, for this set positions to the collision positions
        //drift
        VectorCalculus.plusSE(particle1.position, VectorCalculus.mult(temp,collisionTime, particle1.velocity));
        //kick
        VectorCalculus.plusSE(particle1.position, VectorCalculus.mult(temp,0.5*collisionTime*collisionTime, g));

        //drift
        VectorCalculus.plusSE(particle2.position, VectorCalculus.mult(temp,collisionTime, particle2.velocity));
        //kick
        VectorCalculus.plusSE(particle2.position,VectorCalculus.mult(temp,0.5*collisionTime*collisionTime, g));

        //Calculate n, unit vector connecting ball centers
        double[] n = VectorCalculus.minus(particle1.position,particle2.position);
        VectorCalculus.divideSE(VectorCalculus.norm(n),n);

        //Calculate Energy
        double energy = particle1.mass * VectorCalculus.norm2(particle1.velocity)
                        + particle2.mass * VectorCalculus.norm2(particle2.velocity);

        //Calculate DV
        double[] DV = VectorCalculus.minus(particle1.velocity,particle2.velocity);
        double mass_term = (1/particle1.mass + 1/particle2.mass);
        double dot = VectorCalculus.dot(n,DV) / mass_term;

        //Calculate new velocities
        VectorCalculus.plusSE(particle1.velocity, VectorCalculus.mult(temp,dot / particle1.mass, n));
        VectorCalculus.plusSE(particle1.velocity,
                            VectorCalculus.mult(temp,Math.sqrt(dot*dot + DE * energy / mass_term) / particle1.mass, n));

        VectorCalculus.minusSE(particle2.velocity, VectorCalculus.mult(temp,dot / particle2.mass, n));
        VectorCalculus.minusSE(particle2.velocity,
                            VectorCalculus.mult(temp,Math.sqrt(dot*dot + DE * energy / mass_term) / particle2.mass, n));


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
     * Particle1 is updated to the outgoing velocity. The position is projected backwards in time,
     * such that after the call
     *      particle.position + collisionTime * particle.velocity + 0.5 * collisionTime^2 * g
     * yields the outgoing state of the collision.
     *
     */
    public static void resolveCollision(Particle particle, Boundary boundary, double collisionTime){

        //Calculate collsion position
        VectorCalculus.plusSE(particle.position, VectorCalculus.mult(temp,collisionTime, particle.velocity));
        //kick
        VectorCalculus.plusSE(particle.position,VectorCalculus.mult(temp,0.5*collisionTime*collisionTime, g));

        //Calculate tangential component
        double[] Vt = VectorCalculus.mult(temp,VectorCalculus.dot(boundary.n,particle.velocity),boundary.n);

        //Update to velocity after collision
        VectorCalculus.plusSE(particle.velocity,VectorCalculus.mult(temp,2, Vt));

        //Project back position
        VectorCalculus.minusSE(particle.position,VectorCalculus.mult(temp,collisionTime,particle.velocity));
        VectorCalculus.minusSE(particle.position,VectorCalculus.mult(temp,0.5*collisionTime*collisionTime,g));
    }

    public static void resolveCollision(Particle particle1, Particle particle2){
        resolveCollision(particle1, particle2, findCollisionTime(particle1, particle2));
    }


    public static void resolveCollision(Particle particle, Boundary boundary){
        resolveCollision(particle, boundary, findCollisionTime(particle, boundary));
    }

    public static void projectForwardParticle(Particle particle, double timeStep){
        VectorCalculus.plusSE(particle.position,VectorCalculus.mult(temp,timeStep,particle.velocity));
        VectorCalculus.plusSE(particle.position,VectorCalculus.mult(temp,0.5*timeStep*timeStep,g));
    }

    public static void projectForwardBoundary(Boundary boundary, double timeStep){
        VectorCalculus.plusSE(boundary.position,VectorCalculus.mult(temp,timeStep,boundary.velocity(timeStep)));
    }
}
