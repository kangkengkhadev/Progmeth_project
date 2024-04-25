package render;

import javafx.scene.canvas.GraphicsContext;

public interface Renderable {
    int getZIndex();
    void draw(GraphicsContext gc);
    boolean isDestroyed();
}
