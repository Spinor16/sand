package main;

import data_structures.InitialConditions;

public class Settings1 implements Settings{

    int resolution = 5;
    int nNearestNeighbours = 60;
    double nParticles = 100;
    double mom_coeff = 0.3;
    double restitution_velocity = 0.000001;
    double timeStep = 0.005;
    double endTime = 200;
    int sleep = 20;
    double coefficient_of_restitution = 0.7;

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
}
