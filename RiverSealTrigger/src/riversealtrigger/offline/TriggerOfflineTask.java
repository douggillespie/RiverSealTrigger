package riversealtrigger.offline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import PamController.PamController;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import dataMap.OfflineDataMapPoint;
import offlineProcessing.OfflineTask;
import riversealtrigger.RiverTriggerControl;
import riversealtrigger.RiverTriggerDataBlock;
import riversealtrigger.RiverTriggerProcess;
import tritechgemini.detect.DetectedRegion;
import tritechplugins.detect.threshold.RegionDataBlock;
import tritechplugins.detect.threshold.RegionDataUnit;
import tritechplugins.detect.threshold.ThresholdDetector;
import tritechplugins.detect.threshold.ThresholdParams;
import tritechplugins.detect.track.TrackChain;
import tritechplugins.detect.track.TrackLinkDataBlock;
import tritechplugins.detect.track.TrackLinkDataUnit;
import tritechplugins.detect.track.TrackLinkParameters;

public class TriggerOfflineTask extends OfflineTask<TrackLinkDataUnit> {

	private TrackLinkDataBlock trackDatablock;
	private RiverTriggerProcess triggerProcess;
	private RiverTriggerControl riverTriggerControl;
	private ThresholdDetector thresholdDetector;
	private RegionDataBlock regionDatablock;
	
	private HashMap<Long, TrackLinkDataUnit> growingRegions;

	public TriggerOfflineTask(RiverTriggerControl riverTriggerControl, TrackLinkDataBlock trackDatablock) {
		super(trackDatablock);
		this.riverTriggerControl = riverTriggerControl;
		this.triggerProcess = riverTriggerControl.getTriggerProcess();
		this.trackDatablock = trackDatablock;
		this.regionDatablock = findRegionDataBlock(trackDatablock);
		addRequiredDataBlock(trackDatablock);
		// add?, nothing will get loaded, but it needs to be cleared. but if it's loaded after tracks have loaded
		// it will all get deleted. 
		addRequiredDataBlock(regionDatablock); 
		addAffectedDataBlock(triggerProcess.getTriggerDataBlock());
		growingRegions = new HashMap<>();
	}
	
	private static RegionDataBlock findRegionDataBlock(TrackLinkDataBlock trackLinkDataBlock) {
		if (trackLinkDataBlock == null) {
			return null;
		}
		PamDataBlock aBlock = trackLinkDataBlock.getParentProcess().getParentDataBlock();
		if (aBlock instanceof RegionDataBlock) {
			return (RegionDataBlock) aBlock;
		}
		else {
			return null;
		}
	}

	@Override
	public String getName() {
		return "Process trigger data";
	}

//	@Override
//	public boolean processDataUnit(RegionDataUnit regionUnit) {
//		/*
//		 *  more realistic version to emulate what happens in real time
//		 *  whereby regions arrive in order, then tracks get sent out possibly
//		 *  interleaved with one another. Will need a HashMap of developing tracks.
//		 *  Tracks are 'entire' within a file, so safe to clear the map at 
//		 *  start of each load. 
//		 */
//		TrackLinkDataUnit parentTrack = (TrackLinkDataUnit) regionUnit.getSuperDetection(TrackLinkDataUnit.class);
//		TrackLinkDataUnit existingOut = growingRegions.get(parentTrack.getUID());
//		if (existingOut == null) {
//			// create it. 
//			existingOut = new TrackLinkDataUnit(parentTrack.getTimeMilliseconds());
//			existingOut.setUID(parentTrack.getUID());
//			existingOut.setParentDataBlock(parentTrack.getParentDataBlock());
//			existingOut.setEmbryonic(true);
//			growingRegions.put(parentTrack.getUID(), existingOut);
//		}
//		// now clone the regions since we don't want to mess up superdet links in existing. 
//		RegionDataUnit copy = new RegionDataUnit(regionUnit.getTimeMilliseconds(), 
//				regionUnit.getSonarId(), regionUnit.getRegion());
//		copy.setUID(regionUnit.getUID());
//		existingOut.addSubDetection(copy);
//		if (existingOut.getSubDetectionsCount() == parentTrack.getSubDetectionsCount()) {
//			existingOut.setEmbryonic(false);
//		}
//		TrackLinkParameters params = thresholdDetector.getTrackLinkProcess().getTrackLinkParams();
//		int minPoints = params.minTrackPoints;
//		if (existingOut.getSubDetectionsCount() >= minPoints) {
//			triggerProcess.updateData(trackDatablock, existingOut);
//		}
//		
//		return false;
//	}

	public boolean processDataUnit(TrackLinkDataUnit completeUnit) {
		/**
		 * This will get complete track segments, which aren't exactly how 
		 * data would arrive in real time. Fudge it ? Quite hard to fully recreate
		 * how data will be arriving for overlapping tracks in 'real time' 
		 */
		ArrayList<PamDataUnit<?, ?>> regions = completeUnit.getSubDetections();
//		TrackChain chain = completeUnit.getTrackChain();
//		LinkedList<DetectedRegion> regions = chain.getRegions();
////		/ I think thresheold detctor can never be null. 
////		if (thresholdDetector != null) {
		TrackLinkParameters params = thresholdDetector.getTrackLinkProcess().getTrackLinkParams();
		int minPoints = params.minTrackPoints;
//		//		}
//		Iterator<DetectedRegion> chainIt = regions.iterator();
//		TrackChain dummyChain = new TrackChain(chainIt.next());
		TrackLinkDataUnit dummyUnit = new TrackLinkDataUnit(completeUnit.getTimeMilliseconds());
		dummyUnit.setUID(completeUnit.getUID());
		dummyUnit.setParentDataBlock(completeUnit.getParentDataBlock());
		dummyUnit.setEmbryonic(true);
		for (int i = 0; i < regions.size(); i++) {
			PamDataUnit subDet = regions.get(i);
			if (subDet instanceof RegionDataUnit) {
				RegionDataUnit subRegion = (RegionDataUnit) subDet;
				RegionDataUnit copy = new RegionDataUnit(subRegion.getTimeMilliseconds(), 
						subRegion.getSonarId(), subRegion.getRegion());
				copy.setUID(subRegion.getUID());
				dummyUnit.addSubDetection(copy);
				if (i == regions.size()-1) {
					dummyUnit.setEmbryonic(false);
				}
				if (i >= minPoints) {
					triggerProcess.updateData(trackDatablock, dummyUnit);
				}
			}
		}
		
//		triggerProcess.updateData(trackDatablock, dataUnit);
		
		return false;
	}

	@Override
	public void newDataLoad(long startTime, long endTime, OfflineDataMapPoint mapPoint) {
		growingRegions.clear();
	}

	@Override
	public void loadedDataComplete() {
		growingRegions.clear();
	}

	@Override
	public void completeTask() {
		super.completeTask();
		triggerProcess.pamStop();
	}

	@Override
	public void prepareTask() {
		super.prepareTask();
		triggerProcess.pamStart();
		thresholdDetector = triggerProcess.findInputDetector();
	}

	@Override
	public boolean hasSettings() {
		return true;
	}

	@Override
	public boolean callSettings() {
		riverTriggerControl.showSettings(PamController.getMainFrame());
		return true;
	}

}
