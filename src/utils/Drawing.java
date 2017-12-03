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


    public static Rectangle transform2D(double x, double y, double width, double height, Rectangle bounds, double scale) {
        return transform2D(x, y, width, height, scale, bounds, false);
    }

    public static Rectangle transform2D(double x, double y, double width, double height, double scale, Rectangle bounds, boolean center) {
        // Center values about x, y
        if (center) {
            x -= width/2;
            y += height/2;
        }

        // Scale values
        int xScaled = (int) Math.round(x * scale);
        int yScaled = (int) Math.round(y * scale);
        int widthScaled = (int) Math.round(width * scale);
        int heightScaled = (int) Math.round(height * scale);

        return new Rectangle(xScaled, bounds.height - yScaled, widthScaled, heightScaled);
    }
}
