package game.tetris;

import java.awt.Color;

public class Render {

	private GameEngine hg;

	public Render() {
		hg = new GameEngine(true, App.FPS, App.GRID_WIDTH, App.GRID_HEIGHT, App.SCALE, App.SCALE * 10, App.SCALE);
	}

	public void render(Gameboard gameboard) {

		draw(gameboard.grid(), 0, 0);
		draw(gameboard.preview, App.GRID_WIDTH + 1, -1);

		hg.setStatusText(1, "LEVEL: " + gameboard.level(), Color.WHITE);
		hg.setStatusText(2, "SCORE: " + gameboard.score, Color.WHITE);
		hg.setStatusText(3, "HIGH SCORE: ", Color.WHITE);
		hg.setStatusText(4, "" + gameboard.highscore.getHighScore(), Color.WHITE);
		hg.setStatusText(10, "   NEXT: ", Color.WHITE);

		hg.updateWindow(true);
	}

	private void draw(int[][] grid, int offsetX, int offsetY) {

		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[x].length; y++) {
				Color color = color(grid, x, y);

				hg.drawGrid(x + offsetX, App.GRID_HEIGHT - 1 - y + offsetY, color, true);
			}
		}
	}

	public void setText(String text) {
		hg.setStatusText(text, Color.WHITE);
	}

	public void gameOver() {
		hg.setStatusText(6, "GAME OVER!", Color.RED);
	}

	public void highScore() {

		hg.setStatusText(5, "NEW HIGH SCORE!", Color.GREEN);
	}

	private Color color(int[][] grid, int x, int y) {

		return switch (grid[x][y]) {

		default -> new Color(0x333333);

		case 1 -> Color.BLUE;

		case 2 -> Color.RED;

		case 3 -> Color.GREEN;

		case 4 -> Color.YELLOW;

		case 5 -> Color.CYAN;

		case 6 -> Color.MAGENTA;

		case 7 -> Color.ORANGE;

		};
	}
}
