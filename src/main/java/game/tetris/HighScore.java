package game.tetris;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class HighScore {

	private Properties highscore = new Properties();
	private Path path = Paths.get("highscore.properties");

	public HighScore() {

		if (Files.exists(path)) {

			try {
				InputStream inputStream = Files.newInputStream(path);
				highscore.load(inputStream);
				inputStream.close();

			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}

		}

	}

	public void saveHighScore(int score) {
		try {
			highscore.setProperty("highscore", String.valueOf(score));
			OutputStream outputStream = Files.newOutputStream(path);
			highscore.store(outputStream, "highscore");
			outputStream.close();

		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public int getHighScore() {

		return Integer.parseInt(highscore.getProperty("highscore", "0"));
	}

}
