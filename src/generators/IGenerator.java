package generators;

public interface IGenerator {
    double nextPos();
    double nextVel();
    int nextDirection();
    void setSeed(long i);
}
