package a7;

public class MutablePixelArrayPicture extends PixelArrayPicture implements Picture {

	
	public MutablePixelArrayPicture(Pixel[][] parray, String caption) {
		super(parray, caption);
	}
	
	@Override
	public Picture paint(int x, int y, Pixel p, double factor) {
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IllegalArgumentException("x or y out of bounds");
		}
		
		if (p == null) {
			throw new IllegalArgumentException("Pixel p is null");
		}
		
		if (factor < 0.0 || factor > 1.0) {
			throw new IllegalArgumentException("factor is out of bounds");
		}

		_pixels[x][y] = _pixels[x][y].blend(p,  factor);
		
		return this;
	}

}
