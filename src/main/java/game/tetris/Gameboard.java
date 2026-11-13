package game.tetris;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Gameboard {

	private int[][] grid = new int[App.GRID_WIDTH][App.GRID_HEIGHT];
	public int rows;
	public int score;
	public HighScore highscore = new HighScore();
	int[][] preview = new int[App.PREVIEW_SIZE][App.PREVIEW_SIZE];

	public void load(List<Shape> shapes) {

		clear();

		for (Shape shape : shapes) {

			place(shape);

		}
	}

	public int level() {
		return rows / 5 + 1;
	}

	public int speed() {

		return App.GAME_SPEED - 4 * (level() - 1);

	}

	private void clear() {

		for (int[] row : grid) {
			Arrays.fill(row, 0);
		}
	}

	public void place(Shape shape) {

		for (Point point : shape.points) {

			grid[point.x][point.y] = shape.color;

		}

	}

	public void remove(Shape shape) {

		for (Point point : shape.points) {

			grid[point.x][point.y] = 0;

		}

	}

	public boolean inside(Shape shape) {
		for (Point point : shape.points) {

			if (point.x >= App.GRID_WIDTH || point.x < 0 || point.y >= App.GRID_HEIGHT || point.y < 0) {

				return false;

			}
		}
		return true;
	}

	public boolean canPlace(Shape shape) {

		if (!inside(shape)) {
			return false;
		}

		for (Point point : shape.points) {
			if (grid[point.x][point.y] != 0) {
				return false;
			}

		}
		return true;

	}

	public boolean canShape(Shape shape, Consumer<Shape> func) {

		remove(shape);
		try {
			Shape copy = shape.copy();
			func.accept(copy);
			return canPlace(copy);

		} finally {

			place(shape);
		}

	}

	public void doShape(Shape shape, Consumer<Shape> func) {

		remove(shape);
		func.accept(shape);
		place(shape);

	}

	public int[][] grid() {
		return grid;
	}

	public boolean rowFilled(int y) {
		for (int x = 0; x < App.GRID_WIDTH; x++) {
			if (grid[x][y] == 0) {
				return false;
			}
		}
		return true;
	}

	public void loadPreview(Shape nextShape) {

		for (int[] row : preview) {
			Arrays.fill(row, 0);
		}

		for (Point point : nextShape.points) {

			preview[point.x][point.y] = nextShape.color;

		}

	}

}
