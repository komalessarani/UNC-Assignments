package a7;

public class RegionImpl implements Region {

	private int _top;
	private int _bottom;
	private int _left;
	private int _right;
	
	public RegionImpl(int left, int top, int right, int bottom) {
		if (left > right) {
			throw new IllegalArgumentException("Left is larger then right");
		}
		
		if (top > bottom) {
			throw new IllegalArgumentException("Top is larger then bottom");
		}
		
		_left = left;
		_top = top;
		_right = right;
		_bottom = bottom;
	}
	
	@Override
	public int getTop() {
		return _top;
	}

	@Override
	public int getBottom() {
		return _bottom;
	}

	@Override
	public int getLeft() {
		return _left;
	}

	@Override
	public int getRight() {
		return _right;
	}

	@Override
	public Region intersect(Region other) throws NoIntersectionException {
		if (other == null) {
			throw new NoIntersectionException();
		}

		int max_left = getLeft() > other.getLeft() ? getLeft() : other.getLeft();
		int min_right = getRight() < other.getRight() ? getRight() : other.getRight();

		if (max_left > min_right) {
			throw new NoIntersectionException();
		}

		int max_top = getTop() > other.getTop() ? getTop() : other.getTop();
		int min_bottom = getBottom() < other.getBottom() ? getBottom() : other.getBottom();

		if (max_top > min_bottom) {
			throw new NoIntersectionException();
		}

		return new RegionImpl(max_left, max_top, min_right, min_bottom);
	}

	@Override
	public Region union(Region other) {
		if (other == null) {
			return this;
		}
		
		int min_left = getLeft() < other.getLeft() ? getLeft() : other.getLeft();
		int max_right = getRight() > other.getRight() ? getRight() : other.getRight();
		int min_top = getTop() < other.getTop() ? getTop() : other.getTop();
		int max_bottom = getBottom() > other.getBottom() ? getBottom() : other.getBottom();
		
		return new RegionImpl(min_left, min_top, max_right, max_bottom);
	}

}
