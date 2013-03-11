package rts.utils;

public class Resolution implements Comparable<Resolution> {

	private int width;
	private int height;

	public Resolution(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public String toString() {
		return width + " X " + height;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Resolution) {
			Resolution r = (Resolution) obj;
			return this.width == r.width && height == r.height;
		} else
			return false;
	}

	@Override
	public int compareTo(Resolution o) {
		if (o.getWidth() == width) {
			return this.height - o.getHeight();
		} else
			return this.width - o.getWidth();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
