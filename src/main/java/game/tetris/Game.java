package game.tetris;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Game {

	private final Render render;
	private final Gameboard gameboard = new Gameboard();
	private List<Shape> shapes = new ArrayList<>();
	private Shape activeShape;
	private Shape nextShape;
	private Random random;
	private boolean gameOver = false;
	private Ticker fallTicker = new Ticker(App.GAME_SPEED);

	public Game(Render render) {

		this.render = render;

		long seed = System.nanoTime();
		random = new Random(seed);

		System.out.println("Seed: " + seed);

		testSetup();
	}

	private void testSetup() {

		int failedPlace = 0;
		while (shapes.size() < 0) {

			Shape shape = new Shape(Type.values()[random.nextInt(Type.values().length)],
					new Point(random.nextInt(App.GRID_WIDTH), random.nextInt(App.GRID_HEIGHT)));

			if (gameboard.canPlace(shape)) {
				gameboard.place(shape);
				shapes.add(shape);
				failedPlace = 0;
			} else {
				failedPlace++;
				if (failedPlace > 100) {
					break;
				}
			}
		}

	}

	public void mainLoop() {

		nextShape = generateShape();
		generateNextShape();

		while (!gameOver) {
			gameboard.load(shapes);

			handleController();

			if (fallTicker.tick()) {
				gameboard.score += 10;
				if (gameboard.canShape(activeShape, s -> s.fall())) {
					gameboard.doShape(activeShape, s -> s.fall());
				} else {
					shapes.add(activeShape);

					removeRows();

					generateNextShape();
				}

			}
			gameboard.place(activeShape);
			render.render(gameboard);

		}
	}

	private void removeRows() {
		int rows = 0;
		for (int y = App.GRID_HEIGHT - 1; y >= 0; y--) {

			if (gameboard.rowFilled(y)) {
				rows++;
				for (var it = shapes.iterator(); it.hasNext();) {
					Shape shape = it.next();

					shape.removeRow(y);

					if (shape.isEmpty()) {

						it.remove();

					}

				}

			}

		}
		gameboard.rows += rows;

		fallTicker.setSpeed(gameboard.speed());

		gameboard.score += switch (rows) {

		default -> 0;

		case 1 -> 1000;
		case 2 -> 2500;
		case 3 -> 5000;
		case 4 -> 10000;

		};

	}

	private void generateNextShape() {
		activeShape = nextShape;
		activeShape.moveToTopCenter(App.GRID_WIDTH, App.GRID_HEIGHT);

		nextShape = generateShape();
		nextShape.moveToCenter(App.PREVIEW_SIZE, App.PREVIEW_SIZE);
		gameboard.loadPreview(nextShape);

		if (!gameboard.canPlace(activeShape)) {
			gameOver();
		}
	}

	private Shape generateShape() {
		return new Shape(Type.values()[random.nextInt(Type.values().length)], random.nextInt(4));
	}

	private void gameOver() {
		gameOver = true;
		render.gameOver();
		if (gameboard.score > gameboard.highscore.getHighScore()) {
			gameboard.highscore.saveHighScore(gameboard.score);
			render.highScore();
		}
	}

	private void handleController() {
		if (Controller.left.action()) {

			if (gameboard.canShape(activeShape, s -> s.left())) {
				gameboard.doShape(activeShape, s -> s.left());

			}
		}

		if (Controller.right.action()) {

			if (gameboard.canShape(activeShape, s -> s.right())) {
				gameboard.doShape(activeShape, s -> s.right());
			}

		}
		if (Controller.rotate.action()) {

			List<Consumer<Shape>> rotateMoves = List.of(s -> s.rotate(), s -> s.rotate().left(),
					s -> s.rotate().right(), s -> s.rotate().left().left(), s -> s.rotate().right().right());

			for (Consumer<Shape> func : rotateMoves) {
				if (gameboard.canShape(activeShape, func)) {
					gameboard.doShape(activeShape, func);
					break;
				}
			}
		}
		if (Controller.down.action()) {

			fallTicker.setSpeed(App.INPUT_SPEED);

		} else {
			fallTicker.setSpeed(gameboard.speed());
		}
	}
}
