package game.tetris;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.JFrame;

/**
 * Class for drawing simple Grid based graphics using awt and Swing. <br>
 * <br>
 * This class does not use the "event dispatching thread (EDT)" instead all
 * drawing is done into a double buffered Canvas, and a call to
 * {@link #updateWindow()} is needed to show the image on the screen.
 * 
 * 
 * @author Vislike
 */
public class GameEngine {

	public static final int DEFAULT_INITIAL_WAIT = 500;
	public static final int DEFAULT_END_WAIT = 5000;
	public static final int DEFAULT_TEXT_SIZE = 20;
	public static final boolean DEBUG_GRAPHICS = true;

	private boolean firstUpdate = true;
	private int statusTextPos = 0;
	private int currentFrame = 0;
	private int fpsFrame = 0;
	private long fpsTime = 0;

	final private boolean active;
	final private int fps;
	final private int width;
	final private int height;
	final private int scale;
	final private int statusWidth;
	final private int textSize;

	final private long baseTime;
	final private double frameLength;

	final private JFrame window;
	final private BufferStrategy bufferStrategy;
	private Graphics2D graphics;

	public GameEngine(boolean active, int fps, int width, int height, int scale, int statusWidth) {
		this(active, fps, width, height, scale, statusWidth, DEFAULT_TEXT_SIZE);
	}

	public GameEngine(boolean active, int fps, int width, int height, int scale, int statusWidth, int textSize) {
		this.active = active;
		this.fps = fps;
		this.width = width * scale;
		this.height = height * scale;
		this.scale = scale;
		this.statusWidth = statusWidth;
		this.textSize = textSize;
		this.frameLength = 1000.0 / fps;

		if (this.active) {
			int w = this.width + this.statusWidth;
			int h = this.height;

			// Create window
			window = new JFrame();
			setTitle(fps);
			window.setLocationByPlatform(true);
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setResizable(false);

			// Create canvas with default font and background
			Canvas canvas = new Canvas();
			canvas.setIgnoreRepaint(true);
			canvas.setBackground(Color.BLACK);
			Font scorefont = new Font("TimesRoman", Font.BOLD, this.textSize);
			canvas.setFont(scorefont);
			canvas.setSize(w, h);
			canvas.setFocusable(false);
			window.add(canvas);

			// Display window
			window.pack();
			window.setVisible(true);
			Controller.setup(window);

			// Create double buffer in canvas
			canvas.createBufferStrategy(2);
			bufferStrategy = canvas.getBufferStrategy();
			graphics = (Graphics2D) bufferStrategy.getDrawGraphics();
			flipBuffer();

			// Store basetime for animation
			baseTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
			fpsTime = baseTime;
		} else {
			window = null;
			bufferStrategy = null;
			graphics = null;
			baseTime = 0;
		}
	}

	private void setTitle(int currentFPS) {
		window.setTitle("Tetris FPS: " + currentFPS);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int scale(int v) {
		return v * scale;
	}

	public void drawGrid(int x, int y, Color c) {
		drawGrid(x, y, c, false);
	}

	public void drawGrid(int x, int y, Color c, boolean clearFirst) {
		if (active) {
			if (clearFirst) {
				graphics.clearRect(x * scale, y * scale, scale, scale);
			}
			graphics.setColor(new Color((c.getRGB() & 0x00FFFFFF) | 0x44000000, true));
			graphics.fillRect(x * scale, y * scale, scale - 1, scale - 1);
			graphics.setColor(c);
			graphics.fillRect(x * scale + 4, y * scale + 4, scale - 8, scale - 8);

		}
	}

	public void setStatusText(String text, Color c) {
		setStatusText(statusTextPos++, text, c);
	}

	public void setStatusText(int pos, String text, Color c) {
		if (active) {
			int edge = textSize / 4;
			int h = textSize + edge;
			graphics.clearRect(width, h * pos, statusWidth, h);
			graphics.setColor(c);
			graphics.drawString(text, width + edge, (pos + 1) * h - edge);
		}
	}

	public void resetStatusPos() {
		statusTextPos = 0;
	}

	public void custom(Consumer<Graphics2D> func) {
		if (active) {
			func.accept(graphics);
		}
	}

	public void updateWindow() {
		updateWindow(DEFAULT_INITIAL_WAIT, false);
	}

	public void updateWindow(boolean clearGrid) {
		updateWindow(DEFAULT_INITIAL_WAIT, clearGrid);
	}

	public void updateWindow(int initialWaitMs) {
		updateWindow(initialWaitMs, false);
	}

	public void updateWindow(int initialWaitMs, boolean clearGrid) {
		if (active) {
			// Throttle calls to this method so it will only render at fps
			long waitForTime = baseTime + Math.round(currentFrame * (frameLength));
			long isAt = waitFor(waitForTime);
			// printDebugFrameInfo(waitForTime, isAt);

			// Handle FPS
			if (isAt >= fpsTime + 1000) {
				int fps = (int) ((currentFrame - fpsFrame) / ((isAt - fpsTime) / 1000.0));
				setTitle(fps);
				fpsFrame = currentFrame;
				fpsTime = isAt;
			}

			// Repaint
			flipBuffer();

			// Clear Grid portion of Image, leaving status as is
			if (clearGrid) {
				graphics.clearRect(0, 0, width, height);
			}

			// First update, wait extra time so user have time to focus eyes
			if (firstUpdate) {
				currentFrame += (fps * initialWaitMs) / 1000;
				firstUpdate = false;
			}
			currentFrame++;
		}
	}

	public void useStartDelayNextUpdate() {
		firstUpdate = true;
	}

	public void skipFramesToGetInSync() {
		if (active) {
			int oldFrame = currentFrame;
			long isAt = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
			currentFrame = (int) ((isAt - baseTime) / frameLength);
			fpsTime = isAt;
			fpsFrame = currentFrame++;

			if (DEBUG_GRAPHICS) {
				System.out.println("Skipping frames from: " + oldFrame + ", to: " + currentFrame);
			}
		}
	}

	private void flipBuffer() {
		bufferStrategy.show();
		graphics.dispose();
		graphics = (Graphics2D) bufferStrategy.getDrawGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	private long waitFor(long waitFor) {
		long isAt;
		while ((isAt = TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) < waitFor) {
			Thread.yield();
		}
		return isAt;
	}

}
