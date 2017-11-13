package calc;

import data_structures.Boundary;
import data_structures.Particle;
import org.ejml.simple.SimpleMatrix;

import java.util.Vector;

public class Collision {
    private static double[] g = {0, 9.81};
    /**
     *
     * @param particle1
     * @param particle2
     * @return collisionTime
     *
     * Calculates the collision time of two particles.
     * This works only for 2dim.
     * If no collision time is found, a negative time is returned.
     */
    public double findCollisionTime(Particle particle1, Particle particle2){

        double[] DV = VectorCalculus.minus(particle1.velocity,particle2.velocity);
        double[] DX = VectorCalculus.minus(particle1.position,particle2.position);

        double[] DV2 = VectorCalculus.elementPower(DV,2);
        double[] DX2 = VectorCalculus.elementPower(DX,2);

        double dist2 = Math.pow(particle1.radius + particle2.radius,2);
        return (Math.sqrt(4 * dist2 * VectorCalculus.norm2(DV)
                          - DV2[0] * DX2[1]
                          - DV2[1] * DX2[0]
                          + 2 * DV[0] * DV[1]
                          * DX[0] * DX[1])
                - VectorCalculus.dot(DV,DX)) / VectorCalculus.norm2(DV);

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
     *
     * Non defined behaviour when no Collision time is found. This still needs to be changed.
     *
     */
    //
    public double findCollisionTime(Particle particle, Boundary boundary){
        double v = particle.velocity;
        double alpha = Math.acos(particle.velocity.dot(boundary.direction) / (v * boundary.direction.norm()));

        VectorCalculus DX = (VectorCalculus) boundary.x0.minus(particle.position);
        SimpleMatrix A = particle.velocity.combine(0,SimpleMatrix.END,boundary.direction);

        //fixme: what is TL?
        VectorCalculus TL = (VectorCalculus) A.solve(DX);


        double collisionTime = TL.get(0) - particle.radius / (Math.sin(alpha) * v);
        return collisionTime;
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
     * There are more calculations than need to be done as particle.velocity[1,0] isn't changed in the rotated frame. We
     * might want to change this.
     *
     */
    public void resolveCollision(Particle particle1, Particle particle2, double collisionTime){
        //

        // Calculate position of collision for particle1 and particle2
        //drift
        double[] xColl1 = VectorCalculus.plus(particle1.position, VectorCalculus.mult(collisionTime, particle1.velocity));
        //kick
        VectorCalculus.plusSE(xColl1,VectorCalculus.mult(0.5*collisionTime*collisionTime, g));

        //drift
        double[] xColl2 = VectorCalculus.plus(particle2.position, VectorCalculus.mult(collisionTime, particle2.velocity));
        //kick
        VectorCalculus.plusSE(xColl2,VectorCalculus.mult(0.5*collisionTime*collisionTime, g));

        //Calculate n,vector connecting ball centers
        double[] n = VectorCalculus.orthogonal(VectorCalculus.minus(xColl1,xColl2));

        //Calculate rotation angle
        double phi = Math.acos(n[0]/VectorCalculus.norm(n));

        //Rotate into aligned frame
        double[] vPrime1 = VectorCalculus.rotate(phi, particle1.velocity);
        double[] vPrime2 = VectorCalculus.rotate(phi, particle2.velocity);

        vPrime1[0] = (particle1.mass - particle2.mass) / (particle1.mass + particle2.mass)
                     * vPrime1[0]
                     + 2 * particle2.mass / (particle1.mass + particle2.mass)
                     * vPrime2[0];

        vPrime2[0] = 2 * particle1.mass / (particle1.mass + particle2.mass)
                     * vPrime2[0]
                     + (particle2.mass - particle1.mass) / (particle1.mass + particle2.mass)
                     * vPrime2[0];

        //Rotate back
        particle1.velocity = VectorCalculus.rotate(-phi, vPrime1);
        particle2.velocity = VectorCalculus.rotate(-phi, vPrime2);

        //Project back
        VectorCalculus.minusSE(xColl1,VectorCalculus.mult(collisionTime,particle1.velocity));
        VectorCalculus.minusSE(xColl1,VectorCalculus.mult(0.5*collisionTime*collisionTime,particle1.velocity));
        particle1.velocity = xColl1;

        VectorCalculus.minusSE(xColl2,VectorCalculus.mult(collisionTime,particle2.velocity));
        VectorCalculus.minusSE(xColl2,VectorCalculus.mult(0.5*collisionTime*collisionTime,particle2.velocity));
        particle2.velocity = xColl2;
    }

    /**
     *
     * @param particle
     * @param boundary
     * @param collisionTime
     *
     * Particle1 is updated to the outgoing velocity. The position is projected backwards in time,
     * such that after the call particle.position + collisionTime * particle.velocity yields the outgoing state of
     * the Collision.
     *
     * There are more calculations than need to be done as particle.velocity[1,0] isn't changed in the rotated frame. We
     * might want to change this.
     */
    public void resolveCollision(Particle particle, Boundary boundary, double collisionTime){
        double alpha = Math.acos(boundary.direction.get(0) / boundary.direction.norm());
        VectorCalculus xColl = (VectorCalculus) particle.position.plus(particle.velocity.mult(collisionTime).plus(gravitationalAcc.mult(0.5*collisionTime*collisionTime))); //fixme: check

        VectorCalculus vPrime = VectorCalculus.rotate(alpha,particle.velocity);
        vPrime.set(1, -particle.velocity.get(1));
        particle.velocity = (VectorCalculus) VectorCalculus.rotate(-alpha,vPrime).plus(gravitationalAcc.mult(collisionTime)); //fixme: Check

        particle.position = (VectorCalculus) xColl.minus(particle.velocity.mult(collisionTime));
    }

    public void resolveCollision(Particle particle1, Particle particle2){
        resolveCollision(particle1, particle2, findCollisionTime(particle1, particle2));
    }


    public void resolveCollision(Particle particle, Boundary boundary){
        resolveCollision(particle, boundary, findCollisionTime(particle, boundary));
    }

    



}
