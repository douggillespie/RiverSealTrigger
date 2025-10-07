package riversealtrigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

import PamController.PamController;
import PamUtils.PamCalendar;
import PamUtils.PamUtils;
import PamView.symbol.StandardSymbolManager;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamObservable;
import PamguardMVC.PamProcess;
import riversealtrigger.RiverTriggerParams.RiverRegionThresholds;
import riversealtrigger.logging.RiverTriggerLogging;
import riversealtrigger.swing.RiverTriggerGraphics;
import riversealtrigger.swing.RiverTriggerSymbolManager;
import riversealtrigger.swing.TriggerDisplayProvider;
import tritechgemini.detect.DetectedRegion;
import tritechplugins.detect.threshold.ThresholdDetector;
import tritechplugins.detect.track.TrackChain;
import tritechplugins.detect.track.TrackLinkDataBlock;
import tritechplugins.detect.track.TrackLinkDataUnit;
import tritechplugins.detect.track.TrackLinkProcess;
import tritechplugins.detect.track.TrackVector;
import userDisplay.UserDisplayControl;

public class RiverTriggerProcess extends PamProcess {

	private RiverTriggerControl riverTriggerControl;
	private RiverTriggerDataBlock outputData;

	private HashMap<TrackLinkDataUnit, RiverTriggerDataUnit> currentTriggers = new HashMap<TrackLinkDataUnit, RiverTriggerDataUnit>();

	private Object triggerSynch = new Object();
	
	boolean isViewer;

	public RiverTriggerProcess(RiverTriggerControl riverTriggerControl) {
		super(riverTriggerControl, null);
		this.riverTriggerControl = riverTriggerControl;
		
		isViewer = riverTriggerControl.isViewer();

		outputData = new RiverTriggerDataBlock("Sonar triggers", this);
		outputData.SetLogging(new RiverTriggerLogging(outputData));
		outputData.setOverlayDraw(new RiverTriggerGraphics(riverTriggerControl));
		outputData.setPamSymbolManager(new RiverTriggerSymbolManager(outputData));

		this.addOutputDataBlock(outputData);

		UserDisplayControl.addUserDisplayProvider(new TriggerDisplayProvider(riverTriggerControl, outputData));
	}

	@Override
	public void prepareProcess() {
		super.prepareProcess();
		RiverTriggerParams params = riverTriggerControl.getTriggerParams();
		PamDataBlock source = PamController.getInstance().getDataBlockByLongName(params.dataSourceName);
		setParentDataBlock(source);
	}

	@Override
	public void newData(PamObservable o, PamDataUnit arg) {
		this.updateData(o, arg);
	}

	@Override
	public void updateData(PamObservable o, PamDataUnit arg) {
		/**
		 * Data units will come in embryonic, so can make trigger decisions while unit
		 * is still growing. 
		 */
		if (arg instanceof TrackLinkDataUnit) {
			updatedTrack((TrackLinkDataUnit) arg);
		}
	}

	private void updatedTrack(TrackLinkDataUnit track) {
		boolean isTrig = isTrigger(track); 
		if (isTrig == false) {
			return;
		}
		
		
		
		RiverTriggerDataUnit currentTrigger = currentTriggers.get(track);
		DetectedRegion lastPt = track.getTrackChain().getLastRegion();
		
		if (currentTrigger == null) {
//			System.out.printf("Creating trigger from track UID %d at %s\n", track.getUID(), 
//					PamCalendar.formatDBDateTime(track.getTimeMilliseconds(),  true));
			currentTrigger = new RiverTriggerDataUnit(lastPt.getTimeMilliseconds(), -lastPt.getPeakX(), lastPt.getPeakY(), track);
			currentTriggers.put(track, currentTrigger);
			outputData.addPamData(currentTrigger);
		}
		else {
//			System.out.printf("Extending trigger with track UID %d\n", track.getUID());
			currentTrigger.setTriggerEnd(lastPt.getTimeMilliseconds(), -lastPt.getPeakX(), lastPt.getPeakY(), track);
			outputData.updatePamData(currentTrigger, lastPt.getTimeMilliseconds());
		}
		// get rid of it if it's not going to update any more. 
		if (track.isEmbryonic() == false) {
			currentTriggers.remove(track);
		}
//		checkEndTrigger(track.getTimeMilliseconds());
//
//		if (isTrig == false) {
//			return;
//		}
//		synchronized (triggerSynch) {
//			if (currentTrigger == null) {
//				startTrigger(track);
//			}
//			else {
//				continueTrigger(track);
//			}
//		}
	}

//	private void startTrigger(TrackLinkDataUnit track) {
//		synchronized (triggerSynch) {
//			DetectedRegion lastPt = track.getTrackChain().getLastRegion();
//			currentTrigger = new RiverTriggerDataUnit(track.getEndTimeInMilliseconds(), -lastPt.getPeakX(), lastPt.getPeakY(), track);
//			currentTrigger.setTriggerEnd(lastPt.getTimeMilliseconds() + (long) riverTriggerControl.getTriggerParams().tastDuration*1000);
//			currentTrigger.setEmbryonic(true);
//			outputData.addPamData(currentTrigger);
//			
//		}
//	}
//
//	private void continueTrigger(TrackLinkDataUnit track) {
//		synchronized (triggerSynch) {
//			DetectedRegion lastPt = track.getTrackChain().getLastRegion();
//			double x = -lastPt.getPeakX();
//			double y = lastPt.getPeakY();
//			long newEnd = lastPt.getTimeMilliseconds() + (long) riverTriggerControl.getTriggerParams().tastDuration*1000;
//			currentTrigger.setTriggerEnd(newEnd, x, y, track);
//			outputData.updatePamData(currentTrigger, lastPt.getTimeMilliseconds());
//		}
//	}

//	private void checkEndTrigger(long currentMillis) {
//		synchronized (triggerSynch) {
//			if (currentTrigger == null) {
//				return;
//			}
//			if (currentMillis >= currentTrigger.getTriggerEnd()) {
//				endTrigger();
//			}
//		}
//	}
//
//	private void endTrigger() {
//		synchronized (triggerSynch) {
//			if (currentTrigger == null) {
//				return;
//			}
//			currentTrigger.setEmbryonic(false);
//			outputData.updatePamData(currentTrigger, currentTrigger.getEndTimeInMilliseconds());
//			currentTrigger = null;
//		}
//	}

	/**
	 * Get how far across the river the track is, i.e. is
	 * it in one of the bank regions. 
	 * @param track
	 * @return
	 */
	private int getRiverRegion(TrackLinkDataUnit track) {
		/*
		 *  need to rotate the track according to the flow angle
		 *  then see if the y coordinate is outside the boundaries 
		 *  Don't need rotated X, so only bother calculating Y
		 */
		RiverTriggerParams trigParams = riverTriggerControl.getTriggerParams();
		double angle = Math.toRadians(90-trigParams.flowDirection);
		double sinAngle = Math.sin(angle);
		double cosAngle = Math.cos(angle);
		TrackChain trackChain = track.getTrackChain();
		DetectedRegion lastRegion = trackChain.getLastRegion();
		double x = -Math.sin(lastRegion.getPeakBearing())*lastRegion.getPeakRange();
		double y = Math.cos(lastRegion.getPeakBearing())*lastRegion.getPeakRange();
		double newY = y*cosAngle-x*sinAngle;
		double[] banks = trigParams.getMidRiverRange();
		if (newY < banks[0] ||newY >= banks[1]) {
			return RiverTriggerParams.RIVER_BANK;
		}
		
		return RiverTriggerParams.RIVER_MIDDLE;
	}
	
	private boolean isTrigger(TrackLinkDataUnit track) {
		boolean complete = !track.isEmbryonic();
//		if (track.getUID() == 11000001 && complete) {
//			System.out.println("Track: " + track.getUID());
//		}
		RiverTriggerParams trigParams = riverTriggerControl.getTriggerParams();
		
		int riverRegion = getRiverRegion(track);
		RiverRegionThresholds regionThresholds = trigParams.getRegionThreshold(riverRegion); 
		
		TrackChain trackChain = track.getTrackChain();
		// check link quality. 
		double q = trackChain.getTrackLinkScore();
//		if (track.getUID() == 440002235) {
//			System.out.printf("Score for track %d length %d is %5.3f\n", 440002235, track.getTrackChain().getRegions().size(), q);
//		}
		if (q < regionThresholds.minLinkScore) {
			return false;
		}
//		if (track.getUID() == 440002203) {
//			System.out.printf("WTF is this track here ? for track %d is %5.3f\n", 440002203, q);
//		}
		// check size
		double rSize = trackChain.getMeanRSize();
		if (rSize < regionThresholds.minRSize) {
			return false;
		}
		TrackVector trackVector = track.getTrackChain().getTrackVector();
		if (trackVector == null) {
			return false;
		}
		// check heading
		double head = trackVector.getRelativeHeading(trigParams.flowDirection);
		if (Math.abs(head) < trigParams.minUpstreamDirection) {
			return false; // downstream
		}
		double len = track.getTrackChain().getEnd2EndMetres();
		if (len < regionThresholds.minLength) {
			return false;
		}
		DetectedRegion lastPoint = trackChain.getLastRegion();
		int zone = getTriggerZone(lastPoint);
		if (zone == 0) {
			// too far downstream to care. 
			return false;
		}
		if (zone == 1) {
			// in middle zone, so trigger if the track has ended. 
			return complete;
		}
		// otherwise zone = 2 and we need to go for it!
		return true;
	}
	
	/**
	 * Get the 'zone' for a point.<br>
	 * 0 = downstream of the ignore line<br>
	 * 1 = between the ignore and trigger immediately line<br>
	 * 2 = upstream of the trigger immediately line
	 * @param region
	 * @return zone number: 0, 1, or 2
	 */
	private int getTriggerZone(DetectedRegion region) {
		return getTriggerZone(-region.getPeakX(), region.getPeakY());
	}

	/**
	 * 
	 * Get the 'zone' for a point.<br>
	 * 0 = downstream of the ignore line<br>
	 * 1 = between the ignore and trigger immediately line<br>
	 * 2 = upstream of the trigger immediately line
	 * @param peakX x Coordinate
	 * @param peakY y Coordinate
	 * @return zone number: 0, 1, or 2
	 */
	private int getTriggerZone(double peakX, double peakY) {
		RiverTriggerParams params = riverTriggerControl.getTriggerParams();
		if (isDownstream(params.getIgnorePoint(), peakX, peakY)) {
			return 0;
		}
		if (isDownstream(params.getTriggerPoint(), peakX, peakY)) {
			return 1;
		}
		return 2;
	}
	
	/**
	 * Get true if the xy coordinates are downstream of the given point 
	 * based on the flow angle. 
	 * @param triggerPoint
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isDownstream(double[] triggerPoint, double x, double y) {
		/*
		 * Calculate the angle from triggerPoint to x,y, then see if 
		 * this is within 90 degrees of the flow direction. 
		 */
		double dx = x-triggerPoint[0];
		double dy = y-triggerPoint[1];
		double ang = Math.toDegrees(Math.atan2(dx, dy));
		double deltAng = ang-riverTriggerControl.getTriggerParams().flowDirection;
		deltAng = PamUtils.constrainedAngle(deltAng, 180.);
		deltAng = Math.abs(deltAng);
		
		return deltAng < 90;
	}

	@Override
	public void pamStart() {
//		currentTrigger = null;
		currentTriggers.clear();
	}

	@Override
	public void pamStop() {
//		endTrigger();
	}

	public RiverTriggerDataBlock getTriggerDataBlock() {
		return outputData;
	}

	/**
	 * @return the riverTriggerControl
	 */
	public RiverTriggerControl getRiverTriggerControl() {
		return riverTriggerControl;
	}

	/**
	 * Find the track link process that is feeding this trigger
	 * @return parent track link process. 
	 */
	public TrackLinkProcess findTrackLinkProcess() {
		/*
		 * Should simply be the parent process of the parent data block. 
		 */
		PamDataBlock parentBlock = getParentDataBlock();
		if (parentBlock == null) {
			return null;
		}
		PamProcess parentProc = parentBlock.getParentProcess();
		if (parentProc instanceof TrackLinkProcess) {
			return (TrackLinkProcess) parentProc;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Find the detector (PAM Controlled Unit) feeding the trigger. 
	 * @return input detector 
	 */
	public ThresholdDetector findInputDetector() {
		TrackLinkProcess parentProcess = findTrackLinkProcess();
		if (parentProcess == null) {
			return null;
		}
		return parentProcess.getThresholdDetector();
	}

	/**
	 * Called in Viewer mode to re-link triggers to their parent tracks. 
	 */
	public void linkTriggerTracks() {
		PamDataBlock parentBlock = getParentDataBlock();
		if (parentBlock instanceof TrackLinkDataBlock == false) {
			return;
		}
		TrackLinkDataBlock trackBlock = (TrackLinkDataBlock) getParentDataBlock();
		trackBlock.sortData();
		ArrayList<TrackLinkDataUnit> tracks = trackBlock.getDataCopy();
		outputData.sortData();
		ArrayList<RiverTriggerDataUnit> trigs = outputData.getDataCopy();
		ListIterator<TrackLinkDataUnit> trackIt = tracks.listIterator();
		long oneDay = 3600000L*24L;
		for (RiverTriggerDataUnit aTrig : trigs) {
			String uidsString = aTrig.getTrackUIDList();
			try {
				String[] uids = uidsString.split(",");
				for (int i = 0; i < uids.length; i++) {
					boolean found = false;
					long uid = Long.valueOf(uids[i]);
					while (trackIt.hasNext()) {
						TrackLinkDataUnit aTrack = trackIt.next();
						if (aTrack.getUID() == uid && Math.abs(aTrack.getEndTimeInMilliseconds()-aTrig.getTimeMilliseconds()) < oneDay) {
							aTrig.getTrackDataUnits().add(aTrack);
							found = true;
							break;
						}
					}
					if (!found) {
						// need to try again from start. This could get slow. 
						trackIt = tracks.listIterator();
						while (trackIt.hasNext()) {
							TrackLinkDataUnit aTrack = trackIt.next();
							if (aTrack.getUID() == uid && Math.abs(aTrack.getEndTimeInMilliseconds()-aTrig.getTimeMilliseconds()) < oneDay) {
								aTrig.getTrackDataUnits().add(aTrack);
								found = true;
								break;
							}
						}
					}
				}
			}
			catch (Exception e) {
			}
		}
	}
}
