package main;

import data_structures.InitialConditions;

public class Settings1000Spheres implements Settings{

    int resolution = 20;
    int nNearestNeighbours = 50;
    //double nParticles = 100;
    double radius = 0.006;

    double coefficient_of_restitution = 0.2; //total energy loss -> 1, no energy loss -> 0
    int maxNrOverlapCollisions = 10; //how many times resolve overlap before resolving a real collision again

    double mom_coeff = 0.01;      //particle particle
    double restitution_velocity = 0.1; //particle boundary

    boolean reflectOnBoundary = true;

    double timeStep = 0.001;
    double endTime = 200;
    int sleep = 1;
    int getPaintFrequency = 5;
    boolean printOut = true;


    InitialConditions initialConditions = new InitialConditions(resolution * nNearestNeighbours,
            1,1, Math.PI/2, radius, reflectOnBoundary);

    @Override
    public int getResolution() {
        return resolution;
    }

    @Override
    public int getnNearestNeighbours() {
        return nNearestNeighbours;
    }

    @Override
    public double getMom_coeff() {
        return mom_coeff;
    }

    @Override
    public double getRestitution_velocity() {
        return restitution_velocity;
    }

    @Override
    public double getTimeStep() {
        return timeStep;
    }

    @Override
    public double getEndTime() {
        return endTime;
    }

    @Override
    public InitialConditions getInitialConditions() {
        return initialConditions;
    }

    @Override
    public int getSleep() {
        return sleep;
    }

    @Override
    public double getCoefficient_of_restitution() {
        return coefficient_of_restitution;
    }

    @Override
    public int getMaxNrOverlapCollisions() {
        return maxNrOverlapCollisions;
    }

    @Override
    public int getPaintFrequency() {
        return getPaintFrequency;
    }

    @Override
    public boolean getReflectOnBoundary() { return reflectOnBoundary; }

    @Override
    public double getRadius() { return radius; }

    public boolean isPrintOut() {
        return printOut;
    }


}
