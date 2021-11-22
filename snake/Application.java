import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import static java.awt.event.KeyEvent.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Deque;
import java.util.LinkedList;

import static java.lang.Math.*;

public class Main extends JPanel implements KeyListener {
    // config
    int totalWidth = 100, totalHeight = 100;
    int cellSize = 50;

    int centerX = totalWidth / 2, centerY = totalHeight / 2;

    Timer timer = new Timer(1000 / 10, x -> onTick());

    Snake snake;
    GameMap map;

    // rendering
    BufferedImage mapImage;
    Main() {
        newGame();
    }

    void newGame() {
        snake = new Snake(centerX, centerY, 5);
        map = new GameMap(totalWidth, totalHeight);

        for (int i = 0; i < sqrt(totalWidth * totalHeight); i++)
            map.placeRandom(CellType.Apple);
        for (int i = 0; i < sqrt(totalWidth * totalHeight); i++)
            map.placeRandom(CellType.Trap);
    }

    public static void main(String[] args) {
        var window = new JFrame("Snake!");
        window.setSize(new Dimension(400, 400));
        var panel = new Main();
        window.add(panel);
        window.setVisible(true);
        window.addKeyListener(panel);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        preRender();
        g.drawImage(mapImage, 0, 0, this);
        g.setColor(Color.white);
    }

    public void preRender() {
        var img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        var g = img.getGraphics();
        int width = getWidth() / cellSize;
        int height = getHeight() / cellSize;

        int offsetX = max(0, min(totalWidth - width, snake.body.getFirst().x - width / 2));
        int offsetY = max(0, min(totalHeight - height, snake.body.getFirst().y - height / 2));

        int fitX = min(totalWidth, offsetX + width + 1);
        int fitY = min(totalHeight, offsetY + height + 1);

        int drawOffsetX = max((getWidth() - totalWidth * cellSize) / 2, 0);
        int drawOffsetY = max((getHeight() - totalHeight * cellSize) / 2, 0);

        for (int x = offsetX; x < fitX; x++) {
            for (int y = offsetY; y < fitY; y++) {
                var type = map.cellAt(new Point2D(x, y), CellType.Blank);
                if (type == CellType.Blank) {
                    if ((x + y) % 2 == 0) g.setColor(new Color(18, 18, 18));
                    else g.setColor(new Color(19, 19, 19));
                }
                else if (type == CellType.Apple) g.setColor(new Color(0, 255, 0));
                else if (type == CellType.Trap) g.setColor(new Color(255, 0, 0));
                else if (type == CellType.Snake) g.setColor(new Color(40, 40, 40));
                g.fillRect((x - offsetX) * cellSize + drawOffsetX, (y - offsetY) * cellSize + drawOffsetY, cellSize, cellSize);
            }
        }
        int x = snake.body.getFirst().x, y = snake.body.getFirst().y;
        g.setColor(Color.white);
        g.fillRect((x - offsetX) * cellSize + drawOffsetX, (y - offsetY) * cellSize + drawOffsetY, cellSize, cellSize);
        var renderMiniMap = drawOffsetX + drawOffsetY == 0;

        if (renderMiniMap) {
            x = getWidth() - totalWidth * map.miniMapScale - 5;
            y = getHeight() - totalHeight * map.miniMapScale- 5;
            g.drawRect(x - 1, y - 1, totalWidth * map.miniMapScale + 2, totalHeight * map.miniMapScale + 2);
            g.drawImage(map.image, x, y, this);
        }

        mapImage = img;
    }

    void move() {
        var newPos = snake.body.getFirst().add(snake.direction);
        snake.body.addFirst(newPos);

        if (map.cellAt(newPos) == CellType.Apple) map.placeRandom(CellType.Apple);
        else {
            var last = snake.body.removeLast();
            if (!last.equals(snake.body.getLast()))
                map.clearCell(last);
        }

        if (newPos.x < 0 || newPos.x >= totalWidth ||
            newPos.y < 0 || newPos.y >= totalHeight ||
            map.cellAt(newPos) == CellType.Snake || map.cellAt(newPos) == CellType.Trap) {
            timer.stop();
            System.out.println("Game over!");
            newGame();
            return;
        }
        map.changeCell(newPos, CellType.Snake);
        snake.prevDirection = snake.direction;
    }

    void onTick() {
        update();
        repaint();
    }

    private void update() { move(); }

    public void keyPressed(KeyEvent keyEvent) {
        if (!timer.isRunning()) timer.start();
        var e = keyEvent.getKeyCode();
        if (e == VK_W || e == VK_K || e == VK_UP) snake.setDirection(0, -1);
        if (e == VK_S || e == VK_J || e == VK_DOWN) snake.setDirection(0, 1);
        if (e == VK_A || e == VK_H || e == VK_LEFT) snake.setDirection(-1, 0);
        if (e == VK_D || e == VK_L || e == VK_RIGHT) snake.setDirection(1, 0);
    }

    public void keyReleased(KeyEvent keyEvent) { }
    public void keyTyped(KeyEvent keyEvent) { }
}
public class Snake {
    Snake(int x, int y, int length) {
        for (int i = 0; i < length; i++) {
            body.addFirst(new Point2D(x, y));
        }
    }
    Deque<Point2D> body = new LinkedList<>();
    Point2D direction = new Point2D(-1, 0);
    Point2D prevDirection;

    public void setDirection(Point2D direction) {
        if (direction.inv().equals(this.prevDirection)) return;
        this.direction = direction;
    }

    public void setDirection(int x, int y) { setDirection(new Point2D(x, y)); }
}
public class Point2D {
    final int x, y;

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point2D point2D = (Point2D) o;

        if (x != point2D.x) return false;
        return y == point2D.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public Point2D add(Point2D direction) {
        return new Point2D(x + direction.x, y + direction.y);
    }

    public Point2D inv() {
        return new Point2D(-x, -y);
    }
}
public enum CellType {
    Snake, Apple, Trap, Blank;
}
public class GameMap {
    final int miniMapScale = 3;
    int width, height;
    GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        image = new BufferedImage(width * miniMapScale, height * miniMapScale, BufferedImage.TYPE_INT_ARGB);
        mg = (Graphics2D) image.getGraphics();
        mg.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0f));
    }

    Random random = new Random();
    Map<Point2D, CellType> map = new HashMap<>();
    Graphics2D mg;
    BufferedImage image;

    void clearCell(Point2D position) {
        mg.setColor(new Color(0, 0, 0, 0));
        mg.fillRect(position.x * miniMapScale, position.y * miniMapScale, miniMapScale, miniMapScale);
        map.remove(position);
    }

    void changeCell(Point2D position, CellType cellType) {
        clearCell(position);
        map.put(position, cellType);
        var g = image.getGraphics();
        if (cellType == CellType.Snake) g.setColor(new Color(255, 255, 255, 120));
        else if (cellType == CellType.Apple) g.setColor(new Color(0,255,0,120));
        else if (cellType == CellType.Trap) g.setColor(new Color(255, 0, 0, 120));
        g.fillRect(position.x * miniMapScale, position.y * miniMapScale, miniMapScale, miniMapScale);
    }

    CellType cellAt(Point2D position) { return map.get(position); }
    CellType cellAt(Point2D position, CellType defaultValue) { return map.getOrDefault(position, defaultValue); }

    void placeRandom(CellType cellType) {
        for (int i = 0; i < 1000; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            var pos = new Point2D(x, y);
            if (cellAt(pos) == null) {
                changeCell(pos, cellType);
                break;
            };
        }
    }
}