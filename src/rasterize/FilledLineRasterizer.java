package rasterize;

import java.awt.*;

public class FilledLineRasterizer extends LineRasterizer {
    public FilledLineRasterizer(Raster raster) {
        super(raster);
    }

    /* Vyplnění (rasterizaci) úsečky pomocí Bresenhamova algoritmu

    Výhody Bresenhamova algoritmu jsou:
        1) Je snadno implementovatelný.
        2) Je rychlý a inkrementální.
        3) Provádí se rychle, ale o něco pomaleji než algoritmus DDA.
        4) Body generované tímto algoritmem jsou přesnější než u algoritmu DDA.
        5) Používá pouze pevné body.

    Nevýhody Bresenhamova algoritmu jsou:
        1) I když zlepšuje přesnost generovaných bodů, výsledná čára stále není hladká.
        2) Tento algoritmus je určen pouze pro základní kreslení čar.
        3) Nezvládá zmenšující se zubatosti.
     */

    @Override
    protected void drawLine(int x1, int y1, int x2, int y2, Color color) {
        // Výpočet délky úsečky v osách x a y
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        // Směrový koeficient pro osu x
        int sx = (x1 < x2) ? 1 : -1;
        // Směrový koeficient pro osu y
        int sy = (y1 < y2) ? 1 : -1;

        // Počáteční hodnota chyby
        int error = dx - dy;

        while (x1 != x2 || y1 != y2) {
            // Nastavení pixelu na pozici (x1, y1) na zadanou barvu
            raster.setPixel(x1, y1, color);

            // Dvojnásobná hodnota chyby
            int error2 = 2 * error;

            // Podmínky pro inkrementaci hodnot x a y
            if (error2 > -dy) {
                error -= dy;
                x1 += sx; // Přesun na další pixel v ose x
            }
            if (error2 < dx) {
                error += dx;
                y1 += sy; // Přesun na další pixel v ose y
            }
        }
        // Nastavení posledního pixelu (x2, y2) na zadanou barvu
        raster.setPixel(x2, y2, color);
    }
}
