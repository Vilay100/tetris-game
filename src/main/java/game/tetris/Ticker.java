package game.tetris;

public class Ticker {

	private int speed;
	private int ticker;

	public Ticker(int speed) {

		this.speed = speed;

	}

	public boolean tick() {

		if (ticker >= speed) {
			ticker = 0;
			return true;

		} else {
			ticker++;
			return false;
		}

	}

	public void setSpeed(int speed) {

		this.speed = speed;

	}

	public void reset() {
		ticker = 0;

	}

}
