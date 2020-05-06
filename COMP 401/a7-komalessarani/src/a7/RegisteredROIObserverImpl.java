package a7;

public class RegisteredROIObserverImpl implements RegisteredROIObserver {

	private ROIObserver _observer;
	private Region _region;
	
	public RegisteredROIObserverImpl(ROIObserver o, Region r) {
		if (o == null) {
			throw new IllegalArgumentException("ROIObserver o is null");
		}
		
		if (r == null) {
			throw new IllegalArgumentException("Region r is null");
		}
		
		_observer = o;
		_region = r;
	}
	
	@Override
	public void notify(ObservablePicture picture, Region changed_region) {		
		try {
			_observer.notify(picture, _region.intersect(changed_region));			
		}
		catch (NoIntersectionException e) {
		}
	}

	@Override
	public Region getRegion() {
		return _region;
	}

	@Override
	public ROIObserver getObserver() {
		return _observer;
	}

}
