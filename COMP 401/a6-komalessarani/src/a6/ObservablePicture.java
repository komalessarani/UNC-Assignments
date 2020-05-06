package a6;

public interface ObservablePicture extends Picture {
	
	// Registers an ROIObserver with a particular region of the picture
	void registerROIObserver(ROIObserver observer, Region r);
	
	
	// Unregisters any ROIObservers that are observing a region that intersects
	// with the Region r.
	void unregisterROIObservers(Region r);
	
	// Unregisters a particular ROIObserver previously registered.
	void unregisterROIObserver(ROIObserver observer);

	// Returns an array of all ROIObservers registered to observe a region
	// that intersects with the Region r. The same observer object may appear 
	// more than once in the result if it happens to be registered more than 
	// once with regions of interest that intersect the specified region.
	ROIObserver[] findROIObservers(Region r);
	
	// Suspends notification of registered ROIObservers until resumeObservable
	// has been called.
	void suspendObservable();
	
	// Resumes notification of registered ROIObservers. If any paint operations
	// have occurred since being suspended, immediately notifies any ROIObservers
	// observing regions that intersect with the region that encompasses all paint
	// operations that occurred while suspended.
	void resumeObservable();
}
