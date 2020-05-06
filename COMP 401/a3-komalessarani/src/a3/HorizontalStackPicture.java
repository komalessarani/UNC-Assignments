package a3;

public class HorizontalStackPicture implements Picture {

	private int height;
	private int width;
	private Picture left;
	private Picture right;
	private Pixel[][] pixel_array;

	public HorizontalStackPicture(Picture left, Picture right) {
		if (left == null || right == null)
			throw new IllegalArgumentException("Incompatible left or right values");
		if (left.getHeight() != right.getHeight())
			throw new IllegalArgumentException("Left and right values are not equal");
		height = left.getHeight();
		width = left.getWidth() + right.getWidth();
		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException("Width and height must be greate than 0");
		this.left = left;
		this.right = right;
		this.pixel_array = new Pixel[getWidth()][getHeight()];
		for (int i = 0; i < getHeight(); i++) {
			for (int j = 0; j < getWidth(); j++) {
				if (j < left.getWidth()) {
					pixel_array[j][i] = left.getPixel(j, i);
				} else {
					pixel_array[j][i] = right.getPixel(j - left.getWidth(), i);
				}
			}
		}

	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public Pixel getPixel(int x, int y) {
		if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight())
			throw new IllegalArgumentException("Coordinate out of bounds");
		return getArray()[x][y];
	}

	@Override
	public Picture paint(int x, int y, Pixel p, double factor) {
		if(x < 0 || y < 0 || x >= getWidth() || y >= getHeight())
			throw new IllegalArgumentException("Coordinate out of bounds");
		pixel_array[x][y] = pixel_array[x][y].blend(p, factor);
		return this;
	}

	@Override
	public Picture paint(int ax, int ay, int bx, int by, Pixel p, double factor) {
		if (ax < 0 || ay < 0 || bx < 0 || by < 0 || ax >= getWidth() || ay >= getHeight() || bx >= getWidth()
				|| by >= getHeight()) {
			ax = 0;
			ay = 0;
		}
		for (int i = ax; i <= bx; i++) {
			for (int j = ay; j <= by; j++) {
				pixel_array[i][j] = pixel_array[i][j].blend(p, factor);
			}
		}
		return this;
	}

	@Override
	public Picture paint(int cx, int cy, double radius, Pixel p, double factor) {
		// validate radius value
		if (radius < 0)
			throw new IllegalArgumentException("Radius must be a non-negative value");
		for (int i = 0; i < pixel_array.length; i++) {
			for (int j = 0; j < pixel_array[i].length; j++) {
				if (Math.sqrt((i - cx) * (i - cx) + (j - cy) * (j - cy)) <= radius)
					pixel_array[i][j] = pixel_array[i][j].blend(p, factor);
			}
		}
		return this;
	}

	private Pixel[][] getArray() {

		return pixel_array;
	}

}
