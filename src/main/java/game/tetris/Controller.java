package game.tetris;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JFrame;

public class Controller {

	public static class Action {

		private final List<Integer> keybindings;
		private final boolean repeatable;

		private State state = State.RESET;
		private Ticker ticker = new Ticker(App.INPUT_SPEED);

		public Action(List<Integer> keybindings, boolean repeatable) {
			this.keybindings = keybindings;
			this.repeatable = repeatable;
		}

		public boolean action() {
			if (state == State.PRESSED) {
				state = State.HOLDING;
				ticker.reset();
				return true;
			} else if (state == State.HOLDING && repeatable) {
				return ticker.tick();
			}
			return false;
		}

		private void pressed() {
			if (state == State.RESET) {
				state = State.PRESSED;
			}
		}

		private void released() {
			state = State.RESET;

		}

	}

	public static enum State {
		RESET, PRESSED, HOLDING;
	}

	public static Action left = new Action(List.of(KeyEvent.VK_LEFT, KeyEvent.VK_A), true);
	public static Action right = new Action(List.of(KeyEvent.VK_RIGHT, KeyEvent.VK_D), true);
	public static Action rotate = new Action(List.of(KeyEvent.VK_UP, KeyEvent.VK_W, KeyEvent.VK_SPACE), false);
	public static Action down = new Action(List.of(KeyEvent.VK_DOWN, KeyEvent.VK_S), true);

	private static final List<Action> actions = List.of(left, right, rotate, down);

	public static void setup(JFrame window) {
		window.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

				actions.forEach(a -> {
					if (a.keybindings.stream().anyMatch(k -> k == e.getKeyCode())) {
						a.released();
					}

				});

			}

			@Override
			public void keyPressed(KeyEvent e) {
				actions.forEach(a -> {
					if (a.keybindings.stream().anyMatch(k -> k == e.getKeyCode())) {
						a.pressed();
					}

				});
			}
		});
	}

}
