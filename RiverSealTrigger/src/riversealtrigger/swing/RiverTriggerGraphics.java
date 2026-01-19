package riversealtrigger.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import Jama.Matrix;
import Map.MapProjector;
import Map.MapRectProjector;
import PamUtils.Coordinate3d;
import PamUtils.LatLong;
import PamUtils.PamUtils;
import PamView.GeneralProjector;
import PamView.GeneralProjector.ParameterType;
import PamView.GeneralProjector.ParameterUnits;
import PamView.PamKeyItem;
import PamView.PamSymbol;
import PamView.PamSymbolType;
import PamView.PanelOverlayDraw;
import PamView.symbol.PamSymbolChooser;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.dataSelector.DataSelector;
import riversealtrigger.RiverTriggerControl;
import riversealtrigger.RiverTriggerDataUnit;
import riversealtrigger.RiverTriggerParams;
import tritechplugins.acquire.SonarDaqParams;
import tritechplugins.acquire.SonarPosition;
import tritechplugins.acquire.TritechAcquisition;
import tritechplugins.acquire.TritechDaqParams;
import tritechplugins.acquire.swing.SonarOverlayDraw;
import tritechplugins.display.swing.SonarImagePanel;
import tritechplugins.display.swing.SonarRThiProjector;
import tritechplugins.display.swing.SonarXYProjector;
import tritechplugins.display.swing.SonarZoomTransform;
import tritechplugins.display.swing.SonarsPanel;
import tritechplugins.display.swing.SonarsPanelParams;

public class RiverTriggerGraphics extends SonarOverlayDraw {

	public static PamSymbol defaultSymbol = new PamSymbol(PamSymbolType.SYMBOL_HEXAGRAM, 12, 12, false, Color.RED, Color.BLUE);
	private RiverTriggerControl riverTriggerControl;

	float[] dash = {10,7};

	public RiverTriggerGraphics(RiverTriggerControl riverTriggerControl) {
		super(defaultSymbol);
		this.riverTriggerControl = riverTriggerControl;
	}

	@Override
	public Rectangle drawDataUnit(Graphics g, PamDataUnit pamDataUnit, GeneralProjector generalProjector) {
		if (generalProjector instanceof SonarRThiProjector) {
			return drawOnSonar(g, pamDataUnit, (SonarRThiProjector) generalProjector);
		}
		else if (generalProjector instanceof MapRectProjector) {
			return drawOnMap(g, pamDataUnit, (MapRectProjector) generalProjector);
		}
		return null;
		
	}

	private Rectangle drawOnMap(Graphics g, PamDataUnit pamDataUnit, MapRectProjector generalProjector) {

		LatLong origin = getStreamerOrigin(0, 0);
		if (origin == null) {
			return null;
		}
		RiverTriggerDataUnit rtdu = (RiverTriggerDataUnit) pamDataUnit;
		double x = rtdu.getTriggerX();
		double y = rtdu.getTriggerY();

		LatLong trigLL = origin.addDistanceMeters(x, y);
		Coordinate3d trigCoord = generalProjector.getCoord3d(trigLL);
//		
//		double r = Math.sqrt(x*x+y*y);
//		double ang = -Math.atan2(x,y);
//
//		double endX = rtdu.getEndX();
//		double endY = rtdu.getEndY();
//		double endR = Math.sqrt(endX*endX+endY*endY);
//		double endAng = -Math.atan2(endX, endY);
//
//		Coordinate3d pos = generalProjector.getCoord3d(r, ang,  0);
//		if (pos == null) {
//			return null;
//		}

		PamSymbol symbol = getPamSymbol(rtdu, generalProjector);

		Point xy = trigCoord.getXYPoint();
		Rectangle rect = symbol.draw(g, xy);
		generalProjector.addHoverData(trigCoord, rtdu, 1);

//		Coordinate3d pos2 = generalProjector.getCoord3d(endR, endAng, 0);
//		if (pos2 == null) {
//			return rect;
//		}
//		Point xy2 = pos2.getXYPoint();
//		double dist = pos.getXYPoint().distance(xy2);
//		if (dist > 0 && !Double.isNaN(endR)) {
//			g.setColor(symbol.getLineColor());
//			Graphics2D g2d = (Graphics2D) g;
//			g2d.setStroke(new BasicStroke(symbol.getLineThickness()));
//			symbol.draw(g, xy2);
//			PamSymbol.drawArrow(g, xy.x,  xy.y, xy2.x, xy2.y, 10);
//			generalProjector.addHoverData(pos2, rtdu, 2);
//		}

		return rect;
		
	}

	private Rectangle drawOnSonar(Graphics g, PamDataUnit pamDataUnit, SonarRThiProjector rthiProj) {
		RiverTriggerDataUnit rtdu = (RiverTriggerDataUnit) pamDataUnit;
		SonarPosition sonarPosition = getSonarPosition(rthiProj.getSonarID());
		if (rtdu.usesSonar(rthiProj.getSonarID()) == false) {
			return null;
		}
		double x = rtdu.getTriggerX() - sonarPosition.getX();
		double y = rtdu.getTriggerY() - sonarPosition.getY();

		double r = Math.sqrt(x*x+y*y);
		double ang = -Math.atan2(x,y);

		double endX = rtdu.getEndX() - sonarPosition.getX();;
		double endY = rtdu.getEndY() - sonarPosition.getY();;
		double endR = Math.sqrt(endX*endX+endY*endY);
		double endAng = -Math.atan2(endX, endY);
		
		/**
		 * Projector is looking for coordinates in the sonar frame, we're 
		 * in the absolute frame, so rotate back. 
		 */

		SonarsPanelParams imageParams = rthiProj.getImagePanel().getSonarsPanel().getSonarsPanelParams();
		ang += Math.toRadians(sonarPosition.getHead());
		endAng += Math.toRadians(sonarPosition.getHead());
				
		Coordinate3d pos = rthiProj.getCoord3d(r, ang,  0);
		if (pos == null) {
			return null;
		}

		PamSymbol symbol = getPamSymbol(rtdu, rthiProj);

		Point xy = pos.getXYPoint();
		Rectangle rect = symbol.draw(g, xy);
		rthiProj.addHoverData(pos, rtdu, 1);

		Coordinate3d pos2 = rthiProj.getCoord3d(endR, endAng, 0);
		if (pos2 == null) {
			return rect;
		}
		Point xy2 = pos2.getXYPoint();
		double dist = pos.getXYPoint().distance(xy2);
		if (dist > 0 && !Double.isNaN(endR)) {
			g.setColor(symbol.getLineColor());
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(symbol.getLineThickness()));
			symbol.draw(g, xy2);
			PamSymbol.drawArrow(g, xy.x,  xy.y, xy2.x, xy2.y, 10);
			rthiProj.addHoverData(pos2, rtdu, 2);
		}



		return rect;
	}

	@Override
	public boolean canDraw(ParameterType[] parameterTypes, ParameterUnits[] parameterUnits) {
		try {
			if (parameterTypes[0] == ParameterType.BEARING) {
				return true;
			}
			if (parameterTypes.length >= 2) {
				if ((parameterTypes[0] == ParameterType.RANGE && parameterTypes[1] == ParameterType.BEARING)
						|| (parameterTypes[0] == ParameterType.X && parameterTypes[1] == ParameterType.Y)) {
					return true;
				}
				if (parameterTypes[0] == ParameterType.LATITUDE
						&& parameterTypes[1] == ParameterType.LONGITUDE) {
					return true; // can draw on map
				}
			}
		}
		catch (Exception e) {
			return false;
		}
		return false;
	}

	@Override
	public PamKeyItem createKeyItem(GeneralProjector generalProjector, int keyType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHoverText(GeneralProjector generalProjector, PamDataUnit dataUnit, int iSide) {
		//		return dataUnit.getSummaryString();
		return null;
	}

	@Override
	public boolean preDrawAnything(Graphics g, PamDataBlock pamDataBlock, GeneralProjector projector) {

		if (projector instanceof SonarRThiProjector) {
			preDrawOnSonars(g, pamDataBlock, (SonarRThiProjector) projector);
		}
		else if (projector instanceof MapRectProjector){
			MapRectProjector mapProjector = (MapRectProjector) projector;
			preDrawOnMap(g, pamDataBlock, mapProjector);
		}
		return true;
	}

	/**
	 * Draw trigger data on the map
	 * @param g
	 * @param pamDataBlock
	 * @param mapProjector
	 */
	private void preDrawOnMap(Graphics g, PamDataBlock pamDataBlock, MapRectProjector mapProjector) {

		PamSymbolChooser symbolChooser = mapProjector.getPamSymbolChooser();
		if (symbolChooser instanceof RiverTriggerSymbolChooser == false) {
			return ;
		}
		RiverTriggerSymbolChooser rtsc = (RiverTriggerSymbolChooser) symbolChooser;
		TriggerSymbolOptions rtSymbolOpts = rtsc.getSymbolOptions();
		if (rtSymbolOpts.drawFlowDirection == false && rtSymbolOpts.drawTriggerboundaries == false) {
			return;
		}
		Graphics2D g2d = (Graphics2D) g.create();
		
		RiverTriggerParams riverParams = riverTriggerControl.getTriggerParams();

		TritechAcquisition tritechDaq = riverTriggerControl.getTritechAcquisition();
		SonarPosition sonarPosition;
		if (tritechDaq == null) {
			return;
		}
		int[] sonarIds = tritechDaq.getSonarIds();
		double r = 1;
		double maxAng = 60; // would be good if this was in the parameters. 
		// need to get the bounds we're drawing in. Do this as a rectangle on the map
		double x1=0, x2=0, y1=0, y2=0; // for now these are in metres. 
		for (int i = 0; i < sonarIds.length; i++) {
			sonarPosition = tritechDaq.getDaqParams().getSonarPosition(sonarIds[i]);
			SonarDaqParams daqP = tritechDaq.getDaqParams().getSonarParams(sonarIds[i]);
			r = Math.max(r, daqP.getRange());
			// now need to get the bounds of that in absolute coordinates. 
			double aStep = maxAng/5;
			if (i == 0) {
				x1 = x2 = sonarPosition.getX();
				y1 = y2 = sonarPosition.getY();
			}
			else {
				x1 = Math.min(x1,  sonarPosition.getX());
				x2 = Math.max(x2,  sonarPosition.getX());
				y1 = Math.min(y1,  sonarPosition.getY());
				y2 = Math.max(y2,  sonarPosition.getY());
			}
			double a = -maxAng;
			while (a <= maxAng) {
				double sa = Math.toRadians(a + sonarPosition.getHead());
				double x = sonarPosition.getX() + Math.sin(sa) * r;
				double y = sonarPosition.getY() + Math.cos(sa) * r;
				x1 = Math.min(x1,  x);
				x2 = Math.max(x2,  x);
				y1 = Math.min(y1,  y);
				y2 = Math.max(y2,  y);
				a += aStep;
			}
		}
		
		
		// get the reference point, which is the position of streamer 0. 
		LatLong origin = getStreamerOrigin(0, 0);
		LatLong llTopLeft = origin.addDistanceMeters(x1, y2);
		LatLong llBotRight = origin.addDistanceMeters(x2, y1);
		Coordinate3d topLeft = mapProjector.getCoord3d(llTopLeft);
		Coordinate3d botRight = mapProjector.getCoord3d(llBotRight);
		Point2D pTL = topLeft.getPoint2D();
		Point2D pBR = botRight.getPoint2D();
		

		double lenPixels = Math.max(Math.abs(pBR.getX()-pTL.getX()), Math.abs(pBR.getY()-pTL.getY()));
		
		Rectangle rect = new Rectangle((int) pTL.getX(), (int) pTL.getY(), (int) (pBR.getX()-pTL.getX()), (int) ( pBR.getY()-pTL.getY()));
//		g2d.draw(rect);
		g2d.setClip(rect);
		LatLong llCent = new LatLong((llTopLeft.getLatitude()+llBotRight.getLatitude())/2, (llTopLeft.getLongitude()+llBotRight.getLongitude())/2.);
		if (rtSymbolOpts.drawFlowDirection) {
			LatLong arrEnd = llCent.travelDistanceMeters(riverParams.flowDirection, r/4);
			Coordinate3d centPt = mapProjector.getCoord3d(llCent);
			Coordinate3d arrEndPt = mapProjector.getCoord3d(arrEnd);
			g2d.setStroke(new BasicStroke(2));
			PamSymbol.drawArrow(g2d, (int) centPt.x, (int) centPt.y, (int) arrEndPt.x, (int) arrEndPt.y, 7);
		}
		if (rtSymbolOpts.drawTriggerboundaries) {
			drawMapTrigLine(g2d, origin, mapProjector, riverParams.getIgnorePoint(), 90-riverParams.flowDirection, lenPixels, Color.CYAN, null);
			drawMapTrigLine(g2d, origin, mapProjector, riverParams.getTriggerPoint(), 90-riverParams.flowDirection, lenPixels, Color.RED, null);// mid river lines only have a distance. Assume that this distance is perpendicular to the flow. 
			double[] midPoint = new double[2];
			double[] midRange = riverParams.getMidRiverRange();
			if (midRange != null && midRange.length == 2) {
				String[] banks = {"Near Bank", "Far Bank"};
				for (int i = 0; i < 2; i++) {
					double flowR = Math.toRadians(riverParams.flowDirection);
					midPoint[0] = midRange[i] * Math.cos(flowR);
					midPoint[1] = midRange[i] * Math.sin(flowR);
					drawMapTrigLine(g2d, origin, mapProjector, midPoint, riverParams.flowDirection, lenPixels, Color.WHITE, null);
				}
			}
		}
		
	}

	private void drawMapTrigLine(Graphics2D g2d, LatLong origin, MapRectProjector mapProj, double[] point, 
			double angleDegrees, double lenPixels, Color colour, String title) {
		double l = lenPixels*2;
//		l = 2000;
		
		// translate to the point ...
		LatLong pt = origin.addDistanceMeters(point[0], point[1]);
		Coordinate3d ptPos = mapProj.getCoord3d(pt);
		double mapAng = mapProj.getMapRotationDegrees();
		double a = Math.toRadians(angleDegrees + mapAng);
		int x1 = (int) (ptPos.x + l*Math.sin(a));
		int y1 = (int) (ptPos.y - l*Math.cos(a));
		int x2 = (int) (ptPos.x - l*Math.sin(a));
		int y2 = (int) (ptPos.y + l*Math.cos(a));
		
		g2d.setColor(colour);
		g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash, 0));
		g2d.drawLine(x1, y1, x2, y2);
	}

	public void preDrawOnSonars(Graphics g, PamDataBlock pamDataBlock, SonarRThiProjector rthiProj) {
		// if requested, draw the flow arrow. 
		// assume now that all coordinates are absolute and NOT relative to the sonar. 
		// will probably therefore have to SUBTRACT off the transforms of the sonar frame. 
		int sonarId = rthiProj.getSonarID();

		PamSymbolChooser symbolChooser = rthiProj.getPamSymbolChooser();
		if (symbolChooser instanceof RiverTriggerSymbolChooser == false) {
			return ;
		}
		RiverTriggerSymbolChooser rtsc = (RiverTriggerSymbolChooser) symbolChooser;
		TriggerSymbolOptions rtSymbolOpts = rtsc.getSymbolOptions();
		//		if (rtsc.getSymbolOptions().drawFlowDirection == false) {
		//			return false;
		//		}
		RiverTriggerParams params = riverTriggerControl.getTriggerParams();
		//		double angle = Math.toRadians(params.flowDirection);
		// draw a line from the centre, that's 1/4 the width of the screen. 
		Rectangle clip = g.getClipBounds();
		double range = 0;
		//		if (clip == null) {
			SonarImagePanel sonarPanel = rthiProj.getImagePanel();
			clip = sonarPanel.getBounds();
			range = rthiProj.getMaxRange();
		SonarPosition sonarPosition = getSonarPosition(sonarId);
		SonarsPanelParams imageParams = rthiProj.getImagePanel().getSonarsPanel().getSonarsPanelParams();
		// get the zero point. 
		int x0, y0;
		double zeroAng = 0;
		double zeroY = range/2;
		Coordinate3d middle = rthiProj.getCoord3d(zeroY, zeroAng, false);
		double arrowLen = range / 3.;
		//		double arrEndX = arrowLen * Math.sin(angle);
		//		double arrEndY = 

		if (middle == null) {
			x0 = clip.width/2;
			y0 = clip.y + clip.height/2;
		}
		else {
			x0 = (int) middle.x;
			y0 = (int) middle.y; //.y + clip.height/2;
		}
		/*
		 * Draw the arrow. 
		 */
		// clone the graphics so transforms only affect this function
		// have to use the projector for everything since the display
		// may or may not be rotated - that is the point of the projector that
		// should be able to deal with this stuff. 
		/*
		 * Or can use normal screen coordinates, but if the image isn't rotated, then
		 * allow for that in calculations. 
		 */
		if (rtSymbolOpts.drawFlowDirection) {
			Graphics2D g2d = (Graphics2D) g.create();
			PamSymbol symbol = symbolChooser.getPamSymbol(rthiProj, null);
			g2d.setColor(symbol.getLineColor());

			int len = clip.height/5;
			double arrowAng = Math.toRadians(params.flowDirection);
			if (imageParams.isUseSonarRotation() == false) {
				arrowAng -= Math.toRadians(sonarPosition.getHead());
			}
			int x1 = x0 + (int) (Math.sin(arrowAng)*len);
			int y1 = y0 - (int) (Math.cos(arrowAng)*len);

			g2d.setStroke(new BasicStroke(2));
			PamSymbol.drawArrow(g2d, x0, y0, x1, y1, len/10);
			if (rtSymbolOpts.showLabels) {
				Graphics2D arrowG = (Graphics2D) g2d.create();
				arrowG.translate(x0, y0);
				arrowG.rotate(-Math.toRadians(90-Math.toDegrees(arrowAng)));
				int descent = g2d.getFontMetrics().getDescent();
				arrowG.translate(0,-descent);
				arrowG.setColor(Color.white);
				arrowG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				arrowG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				arrowG.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				arrowG.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				arrowG.drawString("Flow direction", 0, 0);
			}
		}

		if (rtSymbolOpts.drawTriggerboundaries) {
			/**
			 * Draw the trigger lines. These will need to be translated. 
			 */
			boolean dt = rtSymbolOpts.showLabels;
			drawTrigLine(g, sonarPosition, rthiProj, params.getIgnorePoint(), params.flowDirection, Color.CYAN, dt ? "Ignore line" : null);
			drawTrigLine(g, sonarPosition, rthiProj, params.getTriggerPoint(), params.flowDirection, Color.RED, dt ? "Trigger line" : null);

			// mid river lines only have a distance. Assume that this distance is perpendicular to the flow. 
			double[] midPoint = new double[2];
			double[] midRange = params.getMidRiverRange();
			if (midRange != null && midRange.length == 2) {
				String[] banks = {"Near Bank", "Far Bank"};
				for (int i = 0; i < 2; i++) {
					double flowR = Math.toRadians(params.flowDirection);
					midPoint[0] = midRange[i] * Math.cos(flowR);
					midPoint[1] = midRange[i] * Math.sin(flowR);
					drawTrigLine(g, sonarPosition, rthiProj, midPoint, 90-params.flowDirection, Color.WHITE, dt ? banks[i] : null);
				}
			}
		}

	}



	private void drawTrigLine(Graphics g, SonarPosition sonarPosition, SonarRThiProjector rthiProj, double[] point, 
			double angleDegrees, Color colour, String title) {
		if (point == null || point.length != 2) {
			return;
		}
		/**
		 * The rthi projector works and gives a correct screen coordinate
		 * for a position given relative to the sonar image, so just 
		 * need to work out what r and phi are in the sonar image frame and
		 * we can get some positions. Really only need to do this for the
		 * one position, since just have to draw a line through if of about the
		 * correct length. 
		 * Deffo need a point relative to the coordinate frame though since we can't 
		 * really use the sonar angle. 
		 */
		SonarsPanelParams imageParams = rthiProj.getImagePanel().getSonarsPanel().getSonarsPanelParams();
		RiverTriggerParams params = riverTriggerControl.getTriggerParams();
		double rotAngle = 0;
		if (imageParams.isUseSonarRotation() == false) {
			rotAngle = -Math.toRadians(sonarPosition.getHead());
		}
		//		double flowRadians = Math.toRadians(params.flowDirection);
		Graphics2D g2d = (Graphics2D) g.create();
		Shape clip = getClipShape(rthiProj, rthiProj.getMaxRange(), 60, 10);
		//		g2d.draw(clip);
		g2d.setClip(clip);

		double[] sonarrthi = sonarPosition.absXY2SonarRThi(point[0], point[1]);
		Coordinate3d ptC = rthiProj.getCoord3d(sonarrthi[0], sonarrthi[1], false);

		//can now draw lines without having to use the projector.
		double lineAng = Math.toRadians(90-angleDegrees) - rotAngle;
		Rectangle cb = g.getClipBounds();
		double l = 2500;
		if (cb != null) {
			l = Math.max(cb.height, cb.width);
		}
		double x1 = ptC.x + Math.sin(lineAng) * l;
		double x2 = ptC.x - Math.sin(lineAng) * l;
		double y1 = ptC.y + Math.cos(lineAng) * l;
		double y2 = ptC.y - Math.cos(lineAng) * l;
		g2d.setColor(colour);
		g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash, 0));
		g2d.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

		/**
		 * now any text. Rotated and next to the line. Ideally visible ! 
		 */
		if (title != null) {
			/*
			 * Bit of a faff finding which bits of the line are within the clip.
			 * Make a narrow polygon that follows the line, then get it's intersect
			 * with the clip, then get the middle of that.  
			 */
			//			int xa = (int) Math.min(x1, x2)-1;
			//			int xb = (int) Math.max(x1, x2)+1;			
			//			int ya = (int) Math.min(y1, y2)-1;
			//			int yb = (int) Math.max(y1, y2)+1;	
			int xa = (int) x1;
			int xb = (int) x2;
			int ya = (int) y1;
			int yb = (int) y2;

			g2d.setStroke(new BasicStroke(2));
			//			g2d.setColor(Color.white);
			//			g2d.drawLine(xa,  ya,  xb,  yb);


			Polygon pxy;
			lineAng = PamUtils.constrainedAngleR(lineAng, Math.PI/2);
			if ((xb-xa)*(yb-ya) >= 0) {
				int[] px = {xa+1, xa+1, xb-1, xb-1}; 
				int[] py = {ya-1, ya+1, yb-1, yb+1};
				pxy = new Polygon(px, py, 4);
			}
			else {
				int[] px = {xa-1, xa-1, xb+1, xb+1}; 
				int[] py = {ya+1, ya-1, yb+1, yb-1};
				pxy = new Polygon(px, py, 4);
			}
			Area a1 = new Area(clip);
			Area a2 = new Area(pxy);
			a1.intersect(a2);
			a2.intersect(a1);
			Rectangle r = a1.getBounds();
			//			g2d.fillRect(r.x,  r.y,  r.width, r.height);
			//			g2d.drawPolygon(pxy);
			//			g2d.draw

			xa = (int) ptC.x;
			ya = (int) ptC.y;
			if (r != null) {
				xa = r.x + r.width/2;
				ya = r.y + r.height/2;
			}


			AffineTransform rotTrans = AffineTransform.getRotateInstance(Math.PI/2-lineAng, xa, ya);
			g2d.setTransform(rotTrans);
			g2d.drawString(title, xa, ya-3);
		}
	}


	/**
	 * Get a clip shape - the sonar outline. 
	 * @param rthiProj
	 * @param range
	 * @param maxAng
	 * @param aStep
	 * @return
	 */
	private Shape getClipShape(SonarRThiProjector rthiProj, double range, double maxAng, double aStep) {
		int nA = (int) Math.ceil(maxAng*2/aStep);
		int[] x = new int[nA+2];
		int[] y = new int[nA+2];
		double a = -maxAng;
		int ix = 0;
		Coordinate3d c;
		while (a <= maxAng) {
			c = rthiProj.getCoord3d(range, Math.toRadians(a), false);
			x[ix] = (int) c.x;
			y[ix] = (int) c.y;
			ix++;
			a += aStep;
		}
		if (a < maxAng) {
			c = rthiProj.getCoord3d(range, Math.toRadians(maxAng), false);
			x[ix] = (int) c.x;
			y[ix] = (int) c.y;
			ix++;
		}
		c = rthiProj.getCoord3d(0, 0, false);
		for (int i = ix; i < x.length; i++) {
			x[i] = (int) c.x;
			y[i] = (int) c.y;
		}
		return new Polygon(x, y, x.length);
	}


	@Override
	public SonarPosition getSonarPosition(int sonarId) {
		TritechAcquisition tritechDaq = riverTriggerControl.getTritechAcquisition();
		SonarPosition sonarPosition;
		if (tritechDaq != null) {
			sonarPosition = tritechDaq.getDaqParams().getSonarPosition(sonarId);
		}
		else {
			sonarPosition = new SonarPosition();
		}
		return sonarPosition;
	}



}
