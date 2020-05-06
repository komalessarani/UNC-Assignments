package a6;

public class SubPictureImpl extends PictureImpl implements SubPicture {

	private Picture _src;
	private int _xoff;
	private int _yoff;
	private int _width;
	private int _height;
	private String _caption;
	
	public SubPictureImpl(Picture src, int xoffset, int yoffset, int width, int height) {
		super(checkSource(src).getCaption());
		
		if (xoffset < 0 || xoffset >= src.getWidth() || 
				yoffset < 0 || yoffset >= src.getHeight() || 
				width < 1 || xoffset+width > src.getWidth() || 
				height < 1 || yoffset+height > src.getHeight()) {
			throw new IllegalArgumentException("Illegal subpicture geometry");
		}
		
		_src = src;
		_xoff = xoffset;
		_yoff = yoffset;
		_width = width;
		_height = height;
		_caption = src.getCaption();
	}

	private static Picture checkSource(Picture src) {
		if (src == null) {
			throw new IllegalArgumentException("source picture is null");
		}
		return src;
	}
	
	@Override
	public int getWidth() {
		return _width;
	}

	@Override
	public int getHeight() {
		return _height;
	}

	@Override
	public Pixel getPixel(int x, int y) {
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			throw new IllegalArgumentException("x or y out of bounds");
		}
		
		return _src.getPixel(_xoff+x, _yoff+y);
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
		
		Picture result = _src.paint(_xoff+x, _yoff+y, p, factor);
		if (result == _src) {
			// Underlying source is mutable, so we are mutable.
			// No need to update _src since it is the same as result.
			return this;
		} else {
			// Underlying source is immutable, so we are immutable.
			// Extract corresponding subpicture from result and return it.
			SubPicture new_sub = result.extract(getXOffset(), getYOffset(), getWidth(), getHeight());
			
			// Our caption might have been different than the underlying source caption.
			new_sub.setCaption(getCaption());
			
			return new_sub;
		}
	}

	@Override
	public int getXOffset() {
		return _xoff;
	}

	@Override
	public int getYOffset() {
		return _yoff;
	}

	@Override
	public Picture getSource() {
		return _src;
	}

}
