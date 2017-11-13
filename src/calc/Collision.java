package calc;

import data_structures.Boundary;
import data_structures.Particle;
import data_structures.Vector;
import org.ejml.simple.SimpleMatrix;

public class Collision {
    //fixme: Stadel&Co. said we should not use vector class, just use arrays. Less computational effort -> faster
    private static double[] g = {0, 9.81};
    public static Vector gravitationalAcc = new Vector(g);
    /**
     *
     * @param particle1
     * @param particle2
     * @return collisionTime
     *
     * Calculates the Collision time of two particles.
     * This works only for 2dim.
     * If no Collision time is found the argument of the square root will be negative. This still needs to be changed.
     */
    public double findCollisionTime(Particle particle1, Particle particle2){
        Vector DV = (Vector) particle1.velocity.minus(particle2.velocity);
        Vector DX = (Vector) particle1.position.minus(particle2.position);

        //fixme: why so complicated? DV2&DX2 not efficient and not necessary. Nomenclature also a bit fuzzy
        Vector DV2 = (Vector) DV.elementPower(2);
        Vector DX2 = (Vector) DX.elementPower(2);

        double dist = particle1.radius + particle2.radius;
        double sqrt = Math.sqrt(4 * dist * DV.norm2()
                                - DV2.get(0) * DX2.get(1)
                                - DV2.get(1) * DX2.get(0)
                                + 2 * DV.get(0) * DV.get(1)
                                * DX.get(0) * DX.get(1));

        double subtract = DV.transpose().dot(DX);

        double collisionTime = (sqrt - subtract)/DV.norm2();
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
     *
     * Non defined behaviour when no Collision time is found. This still needs to be changed.
     *
     */
    //
    public double findCollisionTime(Particle particle, Boundary boundary){
        double v = particle.velocity.norm();
        double alpha = Math.acos(particle.velocity.dot(boundary.direction) / (v * boundary.direction.norm()));

        Vector DX = (Vector) boundary.x0.minus(particle.position);
        SimpleMatrix A = particle.velocity.combine(0,SimpleMatrix.END,boundary.direction);

        //fixme: what is TL?
        Vector TL = (Vector) A.solve(DX);


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
     * such that after the call particle.position + collisionTime * particle.velocity yields the outgoing state of
     * the Collision.
     *
     * There are more calculations than need to be done as particle.velocity[1,0] isn't changed in the rotated frame. We
     * might want to change this.
     *
     */
    public void resolveCollision(Particle particle1, Particle particle2, double collisionTime){
        //


        //fixme: check gravitational acceleration
        Vector xColl1 = (Vector) particle1.position.plus(particle1.velocity.mult(collisionTime).plus(gravitationalAcc.mult(0.5*collisionTime*collisionTime)));
        Vector xColl2 = (Vector) particle2.position.plus(particle2.velocity.mult(collisionTime).plus(gravitationalAcc.mult(0.5*collisionTime*collisionTime)));

        Vector DS = (Vector) xColl1.minus(xColl2);

        double phi = Math.acos(DS.get(0,0)/DS.norm());

        Vector vPrime1 = Vector.rotate(phi, particle1.velocity);
        Vector vPrime2 = Vector.rotate(phi, particle2.velocity);

        vPrime1.set(0,   (particle1.mass - particle2.mass) / (particle1.mass + particle2.mass)
                                            * vPrime1.get(0)
                                            + 2 * particle2.mass / (particle1.mass + particle2.mass)
                                            * vPrime2.get(0));

        vPrime2.set(0, 2 * particle1.mass / (particle1.mass + particle2.mass)
                                            * vPrime2.get(0)
                                            + (particle2.mass - particle1.mass) / (particle1.mass + particle2.mass)
                                            * vPrime2.get(0));

        //fixme: check gravitational acceleration again
        particle1.velocity = (Vector) Vector.rotate(-phi, vPrime1).plus(gravitationalAcc.mult(collisionTime));
        particle2.velocity = (Vector) Vector.rotate(-phi, vPrime2).plus(gravitationalAcc.mult(collisionTime));

        particle1.position = (Vector) xColl1.minus(particle1.velocity.mult(collisionTime));
        particle2.position = (Vector) xColl1.minus(particle2.velocity.mult(collisionTime)); //fixme: added this, check

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
        Vector xColl = (Vector) particle.position.plus(particle.velocity.mult(collisionTime).plus(gravitationalAcc.mult(0.5*collisionTime*collisionTime))); //fixme: check

        Vector vPrime = Vector.rotate(alpha,particle.velocity);
        vPrime.set(1, -particle.velocity.get(1));
        particle.velocity = (Vector) Vector.rotate(-alpha,vPrime).plus(gravitationalAcc.mult(collisionTime)); //fixme: Check

        particle.position = (Vector) xColl.minus(particle.velocity.mult(collisionTime));
    }

    public void resolveCollision(Particle particle1, Particle particle2){
        resolveCollision(particle1, particle2, findCollisionTime(particle1, particle2));
    }


    public void resolveCollision(Particle particle, Boundary boundary){
        resolveCollision(particle, boundary, findCollisionTime(particle, boundary));
    }

    



}
