package main;

import data_structures.InitialConditions;

public class Settings1 implements Settings{

    int resolution = 50;
    int nNearestNeighbours = 20;
    double nParticles = 100;
    double mom_coeff = 0.001;
    double restitution_velocity = 1;
    double timeStep = 0.0005;
    double endTime = 200;
    int sleep = 2;
    double coefficient_of_restitution = 0.7;
    int maxNrOverlapCollisions = 1;
    int getPaintFrequency = 3;

    InitialConditions initialConditions = new InitialConditions(resolution * nNearestNeighbours,
                                                                1,1, Math.PI/2);

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

    public int getPaintFrequency() {
        return getPaintFrequency;
    }
}
