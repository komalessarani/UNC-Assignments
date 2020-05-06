package a7;

public interface Region {

	int getTop();
	int getBottom();
	int getLeft();
	int getRight();

	Region intersect(Region other) throws NoIntersectionException;
	Region union(Region other);

}
