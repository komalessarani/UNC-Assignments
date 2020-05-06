package a6;

public interface ROIObserver {
	
	void notify(ObservablePicture picture, Region changed_region);
}
