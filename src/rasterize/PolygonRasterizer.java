package rasterize;

import model.Line;
import model.Point;
import model.Polygon;

import java.awt.*;

public class PolygonRasterizer {
    private final LineRasterizer lineRasterizer;
    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }
    public void rasterize(Polygon polygon, Color color) {
        if (polygon.size() < 3)
            return;

        for (int i = 0; i < polygon.size(); i++) {
            int indexB = (i + 1) % polygon.size();

            Point pA = polygon.getPoint(i);
            Point pB = polygon.getPoint(indexB);

            lineRasterizer.rasterize(new Line(pA, pB), color);
        }
    }
}
