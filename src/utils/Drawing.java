package utils;

import java.awt.*;

public class Drawing {

    public static double scale(Rectangle bounds, double[] minima, double[] maxima) {
        double max = 0;
        for (int i = 0; i < minima.length; i++) {
            max = Math.max(maxima[i] - minima[i], max);
        }
        return Math.min(bounds.width, bounds.height) / max;
    }


    public static Rectangle transform(double x, double y, double width, double height, double scale) {
        return transform(x, y, width, height, scale, false);
    }

    public static Rectangle transform(double x, double y, double width, double height, double scale, boolean center) {
        // Center values about x, y
        if (center) {
            x -= width/2;
            y -= height/2;
        }

        // Scale values
        int xScaled = (int) Math.round(x * scale);
        int yScaled = (int) Math.round(y * scale);
        int widthScaled = (int) Math.round(width * scale);
        int heightScaled = (int) Math.round(height * scale);

        return new Rectangle(xScaled, yScaled, widthScaled, heightScaled);
    }
}
