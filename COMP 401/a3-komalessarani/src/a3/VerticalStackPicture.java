package a3;

public class VerticalStackPicture implements Picture {

	private int width;
	private int height;
	private Picture top;
	private Picture bottom;
	private Pixel[][] pixel_array;
	
	public VerticalStackPicture(Picture top, Picture bottom) {
		if(top == null || bottom == null)
			throw new IllegalArgumentException("Top or bottom is null");
		if(top.getWidth() != bottom.getWidth())
			throw new IllegalArgumentException("Invalid input");
		
		height = top.getHeight() + bottom.getHeight();
		width = top.getWidth();
		
		this.top = top;
		this.bottom = bottom;
		this.pixel_array = new Pixel[getWidth()][getHeight()];
		for(int x = 0; x < getWidth(); x++) {
			for(int y = 0; y < getHeight(); y++) {
				if(y < top.getHeight()) {
					pixel_array[x][y] = top.getPixel(x, y);
				} else {
					pixel_array[x][y] = bottom.getPixel(x, y-top.getHeight()); 
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
