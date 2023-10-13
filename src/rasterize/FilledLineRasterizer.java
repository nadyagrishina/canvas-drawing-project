package rasterize;

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
    protected void drawLine(int x1, int y1, int x2, int y2) {
        // Výpočet délky úsečky v osách x a y
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        // Směrový koeficient pro osu x
        int sx = (x1 < x2) ? 1 : -1;
        // Směrový koeficient pro osu y
        int sy = (y1 < y2) ? 1 : -1;

        // Počáteční hodnota chyby
        int err = dx - dy;

        while (x1 != x2 || y1 != y2) {
            // Nastavení pixelu na pozici (x1, y1) na zelenou barvu
            raster.setPixel(x1, y1, 0xf658b8);
            // Dvojnásobná hodnota chyby
            int err2 = 2 * err;
            if (err2 > -dy) {
                err -= dy;
                x1 += sx; // Přesun na další pixel v ose x
            }
            if (err2 < dx) {
                err += dx;
                y1 += sy; // Přesun na další pixel v ose y
            }
        }
        // Nastavení posledního pixelu (x2, y2) na zelenou barvu
        raster.setPixel(x2, y2, 0xf658b8);
    }
}
