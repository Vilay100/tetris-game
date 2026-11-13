package game.tetris;

import java.util.ArrayList;
import java.util.List;

public class Shape {

	List<Point> points;
	Point center;
	int color;

	public Shape(Type type, Point start) {
		points = generateShape(type);
		color = type.ordinal() + 1;
		this.center = this.points.get(centerIndex(type));
		move(start.x, start.y);
	}

	private Shape(List<Point> points, int color, Point center) {
		this.color = color;
		this.points = new ArrayList<>();
		for (Point p : points) {
			this.points.add(new Point(p.x, p.y));
			if (center == p) {
				this.center = this.points.getLast();
			}
		}
	}

	private int centerIndex(Type type) {
		return switch (type) {
		case J, L, T -> 2;
		case Z -> 0;
		default -> 1;
		};
	}

	public Shape(Type type, int rotation) {
		points = generateShape(type);
		this.center = this.points.get(centerIndex(type));
		color = type.ordinal() + 1;
		for (int i = 0; i < rotation; i++) {
			rotate();
		}

	}

	private List<Point> generateShape(Type type) {
		List<Point> result = new ArrayList<>();
		switch (type) {
		case I:
			result.add(new Point(0, 0));
			result.add(new Point(0, 1));
			result.add(new Point(0, 2));
			result.add(new Point(0, 3));
			break;
		case J:
			result.add(new Point(0, 0));
			result.add(new Point(1, 0));
			result.add(new Point(1, 1));
			result.add(new Point(1, 2));
			break;
		case L:
			result.add(new Point(0, 0));
			result.add(new Point(1, 0));
			result.add(new Point(0, 1));
			result.add(new Point(0, 2));
			break;
		case O:
			result.add(new Point(0, 0));
			result.add(new Point(1, 0));
			result.add(new Point(0, 1));
			result.add(new Point(1, 1));
			break;
		case S:
			result.add(new Point(0, 0));
			result.add(new Point(1, 0));
			result.add(new Point(1, 1));
			result.add(new Point(2, 1));
			break;
		case T:
			result.add(new Point(1, 0));
			result.add(new Point(0, 1));
			result.add(new Point(1, 1));
			result.add(new Point(2, 1));
			break;
		case Z:
			result.add(new Point(1, 0));
			result.add(new Point(0, 1));
			result.add(new Point(1, 1));
			result.add(new Point(2, 0));
			break;
		default:
			break;
		}

		return result;
	}

	public Shape fall() {

		for (Point point : points) {
			point.y--;
		}
		return this;
	}

	public Shape right() {

		for (Point point : points) {
			point.x++;
		}
		return this;
	}

	public Shape left() {

		for (Point point : points) {
			point.x--;
		}
		return this;
	}

	public Shape rotate() {

		for (Point point : points) {

			int x = point.x - center.x;
			int y = point.y - center.y;
			point.x = center.x + y;
			point.y = center.y - x;

		}

		return this;

	}

	public void move(int x, int y) {

		for (Point p : points) {

			p.x += x;
			p.y += y;
		}

	}

	public void moveToTopCenter(int right, int top) {

		int moveY = top;
		int leftmost = right;
		int rightmost = 0;

		for (Point p : points) {
			if (top - p.y < moveY) {
				moveY = top - p.y;

			}

			if (p.x < leftmost) {
				leftmost = p.x;
			}
			if (p.x > rightmost) {
				rightmost = p.x;
			}
		}
		int bredth = rightmost - leftmost + 1;

		move((right / 2) - leftmost - bredth / 2, moveY - 1);
	}

	public Shape copy() {

		return new Shape(points, color, center);

	}

	@Override
	public String toString() {
		return center.toString();
	}

	public void removeRow(int y) {

		for (var it = points.iterator(); it.hasNext();) {
			Point p = it.next();
			if (p.y > y) {
				p.y--;
			} else if (p.y == y) {
				it.remove();
			}
		}

	}

	public boolean isEmpty() {

		return points.isEmpty();
	}

	public void moveToCenter(int gridWidth, int gridHeight) {

		int topMost = gridHeight;
		int bottomMost = 0;
		int leftmost = gridWidth;
		int rightmost = 0;

		for (Point p : points) {

			if (p.y < topMost) {
				topMost = p.y;
			}
			if (p.y > bottomMost) {
				bottomMost = p.y;
			}

			if (p.x < leftmost) {
				leftmost = p.x;
			}
			if (p.x > rightmost) {
				rightmost = p.x;
			}
		}
		int shapeWidth = rightmost - leftmost + 1;
		int shapeHeight = bottomMost - topMost + 1;

		move((gridWidth / 2) - leftmost - shapeWidth / 2, (gridHeight / 2) - topMost - shapeHeight / 2);

	}

}
