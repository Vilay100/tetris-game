package game.tetris;

public class App {

	public static final int GRID_WIDTH = 10;
	public static final int GRID_HEIGHT = 20;
	public static final int PREVIEW_SIZE = 5;
	public static final int GAME_SPEED = 60;
	public static final int INPUT_SPEED = 5;
	public static final int SCALE = 46;
	public static final int FPS = 100;

	public static void main(String[] args) throws Exception {
		Render render = new Render(); // Graphic

		Game game = new Game(render);

		game.mainLoop();

	}
}
