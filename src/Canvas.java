import model.Line;
import model.Point;
import model.Polygon;
import rasterize.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Nadezhda Grishina
 * @version 2023.c04
 */

public class Canvas {
    private final JPanel panel;
    private RasterBufferedImage raster;
    private LineRasterizer lineRasterizer;
    private LineRasterizer dottedLineRasterizer;
    private PolygonRasterizer polygonRasterizer;
    private Polygon polygon;
    private Line line;
    private boolean shiftPressed = false;
    private boolean drawLineMode = true;
    private boolean drawDottedLineMode = false;
    private boolean drawPolygonMode = false;
    private int mouseX; // aktuální x-ová pozice myši
    private int mouseY; // aktuální y-ová pozice myši

    public Canvas(int width, int height) {
        // Inicializace hlavního okna
        JFrame frame = new JFrame();
        JButton lineButton = new JButton("Line"); // Tlačítko pro kreslení úsečky
        JButton dottedLineButton = new JButton("Dotted Line"); // Tlačítko pro kreslení tečkované úsečky
        JButton polygonButton = new JButton("Polygon");// Tlačítko pro kreslení polygonu
        JLabel keyDescriptionLabel = new JLabel("<html><div style='width: 180px; padding: 0 5px'><br><b>Shift key:</b> \n" +
                "in Line and Dotted Line modes draw a horizontal, vertical or diagonal line.<br><br>" +
                "<b>C key:</b> delete Canvas and all textures.");
        JPanel buttonPanel = new JPanel(); // Panel tlačítek

        lineButton.setMaximumSize(new Dimension(240, 40)); // Nastavení maximální velikosti tlačítka
        dottedLineButton.setMaximumSize(new Dimension(240, 40));
        polygonButton.setMaximumSize(new Dimension(240, 40));

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Vertikální uspořádání na panelu

        buttonPanel.add(lineButton);
        buttonPanel.add(dottedLineButton);
        buttonPanel.add(polygonButton);
        buttonPanel.add(keyDescriptionLabel);

        frame.setLayout(new BorderLayout());
        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Inicializace rastrového plátna
        raster = new RasterBufferedImage(width, height);
        lineRasterizer = new FilledLineRasterizer(raster);
        dottedLineRasterizer = new DottedLineRasterizer(raster, 5);
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        polygon = new Polygon();

        // Inicializace hlavního panelu pro kreslení
        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        // Posluchač klávesnice
        KeyAdapter keyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftPressed = true;
                } else if (e.getKeyChar() == 'c' || e.getKeyChar() == 'C') {
                    polygon = new Polygon();
                    raster.clear();
                    panel.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftPressed = false;
                }
            }
        };
        // Přidání posluchačů klávesnice
        panel.addKeyListener(keyListener);
        lineButton.addKeyListener(keyListener);
        dottedLineButton.addKeyListener(keyListener);
        polygonButton.addKeyListener(keyListener);

        // Posluchač změny velikosti okna
        ComponentAdapter componentListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int newWidth = panel.getWidth();
                int newHeight = panel.getHeight();
                if (newWidth < 1 || newHeight < 1)
                    return;
                if (newWidth <= raster.getWidth() && newHeight <= raster.getHeight())
                    // Neprovede změnu velikosti, pokud nová velikost je menší
                    return;
                // Nový RasterBufferedImage s většími rozměry
                RasterBufferedImage newRaster = new RasterBufferedImage(newWidth, newHeight);
                dottedLineRasterizer = new DottedLineRasterizer(newRaster, 5);
                lineRasterizer = new FilledLineRasterizer(newRaster);
                polygonRasterizer = new PolygonRasterizer(lineRasterizer);
                polygonRasterizer.rasterize(polygon);
                lineRasterizer.rasterize(line);
                raster = newRaster;
                panel.repaint();
            }
        };
        // Přidání posluchače změny velikosti okna
        panel.addComponentListener(componentListener);
        lineButton.addComponentListener(componentListener);
        dottedLineButton.addComponentListener(componentListener);
        polygonButton.addComponentListener(componentListener);

        // Posluchač myši pro kreslení polygonu
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (drawPolygonMode) {
                    raster.clear();
                    Point p = new Point(e.getX(), e.getY());
                    polygon.addPoint(p);
                    if (polygon.size() >= 3) {
                        polygonRasterizer.rasterize(polygon);
                    }
                    panel.repaint();
                }
            }
        });

        // Posluchač myši pro kreslení čar
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drawLineMode || drawDottedLineMode) {
                    raster.clear();
                    // Počáteční bod
                    Point p1 = new Point(width / 2, height / 2);
                    // Koncový bod určený polohou myši
                    Point p2 = new Point(e.getX(), e.getY());
                    // Výpočet rozdílu v ose x a y
                    int dx = Math.abs(p2.x - p1.x);
                    int dy = Math.abs(p2.y - p1.y);

                    if (shiftPressed) {
                        if (dx == dy) {
                            //Úhlopříčná úsečka
                            if ((p2.x > p1.x) && (p2.y > p1.y)) {
                                // První kvadrant
                                p2.x = p1.x + dx;
                                p2.y = p1.y + dx;
                            } else if ((p1.x > p2.x) && (p1.y > p2.y)) {
                                // Třetí kvadrant
                                p2.x = p1.x - dx;
                                p2.y = p1.y - dx;
                            } else if ((p1.x > p2.x) && (p2.y > p1.y)) {
                                // Čtvrtý kvadrant
                                p2.x = p1.x - dx;
                                p2.y = p1.y + dx;
                            } else {
                                // Druhý kvadrant
                                p2.x = p1.x + dx;
                                p2.y = p1.y - dx;
                            }
                        } else if (dx > dy) {
                            // Vodorovná úsečka
                            p2.y = p1.y;
                        } else {
                            // Svislá úsečka
                            p2.x = p1.x;
                        }
                    }
                    line = new Line(p1, p2, 0xf658b8);
                    if (drawDottedLineMode) {
                        dottedLineRasterizer.rasterize(line);
                    } else if (drawLineMode) {
                        lineRasterizer.rasterize(line);
                    }

                    // Obnovení zobrazení panelu
                    panel.repaint();
                }
            }
        });

        // Posluchače pro tlačítka
        lineButton.addActionListener(e -> setDrawMode(true, false, false));
        dottedLineButton.addActionListener(e -> setDrawMode(false, true, false));
        polygonButton.addActionListener(e -> setDrawMode(false, false, true));

        // Posluchač myši pro aktualizaci koordinát myši
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateMouseCoordinates(e.getX(), e.getY());
                if (drawLineMode || drawDottedLineMode) {
                    updateMouseCoordinates(e.getX(), e.getY());
                    panel.repaint();
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                if (drawPolygonMode) {
                    updateMouseCoordinates(e.getX(), e.getY());
                }
            }
        });


        // Nastavení hlavního okna
        frame.add(panel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.WEST);
        frame.pack();
        frame.setVisible(true);
        panel.requestFocus();
        panel.requestFocusInWindow();
        panel.setFocusable(true);
    }

    // Metoda pro smazání plátna
    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
    }

    // Metoda pro vykreslení plátna
    public void present(Graphics graphics) {
        raster.repaint(graphics);
        graphics.setColor(Color.RED);
        graphics.drawString("Mouse: X=" + mouseX + ", Y=" + mouseY, 10, 20);
    }

    // Metoda pro zahájení kreslení
    public void start() {
        clear(0xaaaaaa);
        panel.repaint();
    }

    // Nastavení režimu kreslení
    private void setDrawMode(boolean line, boolean dottedLine, boolean polygon) {
        drawLineMode = line;
        drawDottedLineMode = dottedLine;
        drawPolygonMode = polygon;
        raster.clear();
        panel.repaint();
    }

    // Metoda pro aktualizaci koordinát myši
    public void updateMouseCoordinates(int x, int y) {
        mouseX = x;
        mouseY = y;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Canvas(800, 600).start());
    }
}
