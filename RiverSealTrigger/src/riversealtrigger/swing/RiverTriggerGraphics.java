package riversealtrigger.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

import Jama.Matrix;
import PamUtils.Coordinate3d;
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
import tritechplugins.display.swing.SonarImagePanel;
import tritechplugins.display.swing.SonarXYProjector;
import tritechplugins.display.swing.SonarsPanel;

public class RiverTriggerGraphics extends PanelOverlayDraw {

	public static PamSymbol defaultSymbol = new PamSymbol(PamSymbolType.SYMBOL_HEXAGRAM, 12, 12, false, Color.RED, Color.BLUE);
	private RiverTriggerControl riverTriggerControl;
	
	public RiverTriggerGraphics(RiverTriggerControl riverTriggerControl) {
		super(defaultSymbol);
		this.riverTriggerControl = riverTriggerControl;
	}

	@Override
	public Rectangle drawDataUnit(Graphics g, PamDataUnit pamDataUnit, GeneralProjector generalProjector) {
		RiverTriggerDataUnit rtdu = (RiverTriggerDataUnit) pamDataUnit;
		double x = rtdu.getTriggerX();
		double y = rtdu.getTriggerY();
		
		double r = Math.sqrt(x*x+y*y);
		double ang = -Math.atan2(x,y);
		
		double endX = rtdu.getEndX();
		double endY = rtdu.getEndY();
		double endR = Math.sqrt(endX*endX+endY*endY);
		double endAng = -Math.atan2(endX, endY);
		
		Coordinate3d pos = generalProjector.getCoord3d(r, ang,  0);
		if (pos == null) {
			return null;
		}

		PamSymbol symbol = getPamSymbol(rtdu, generalProjector);

		Point xy = pos.getXYPoint();
		Rectangle rect = symbol.draw(g, xy);
		generalProjector.addHoverData(pos, rtdu, 1);
		
		Coordinate3d pos2 = generalProjector.getCoord3d(endR, endAng, 0);
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
			generalProjector.addHoverData(pos2, rtdu, 2);
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
				return (parameterTypes[0] == ParameterType.RANGE && parameterTypes[1] == ParameterType.BEARING)
				|| (parameterTypes[0] == ParameterType.X && parameterTypes[1] == ParameterType.Y);
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
		// if requested, draw the flow arrow. 
		PamSymbolChooser symbolChooser = projector.getPamSymbolChooser();
		if (symbolChooser instanceof RiverTriggerSymbolChooser == false) {
			return false;
		}
		RiverTriggerSymbolChooser rtsc = (RiverTriggerSymbolChooser) symbolChooser;
		if (rtsc.getSymbolOptions().drawFlowDirection == false) {
			return false;
		}
		RiverTriggerParams params = riverTriggerControl.getTriggerParams();
		double angle = Math.toRadians(params.flowDirection);
		// draw a line from the centre, that's 1/4 the width of the screen. 
		Rectangle clip = g.getClipBounds();
		double range = 0;
		SonarXYProjector xyProj = null;
//		if (clip == null) {
			if (projector instanceof SonarXYProjector) {
				xyProj = (SonarXYProjector) projector;
				SonarImagePanel sonarPanel = xyProj.getImagePanel();
				clip = sonarPanel.getBounds();
				range = xyProj.getMaxRange();
			}
			else {
				return false;
			}
//		}
		if (clip == null) {
			return false;
		}
		// get the zero point. 
		int x0, y0;
		Coordinate3d middle = xyProj.getCoord3d(range/2, 0, false);
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
		Graphics2D g2d = (Graphics2D) g.create();
		PamSymbol symbol = symbolChooser.getPamSymbol(projector, null);
		g2d.setColor(symbol.getLineColor());
		int len = clip.height/5;
		int x1 = x0 + (int) (Math.sin(angle)*len);
		int y1 = y0 - (int) (Math.cos(angle)*len);
		g2d.setStroke(new BasicStroke(2));
		PamSymbol.drawArrow(g2d, x0, y0, x1, y1, len/10);
		g2d.translate(x0, y0);
		g2d.rotate(-Math.toRadians(90-params.flowDirection));
		int descent = g2d.getFontMetrics().getDescent();
		g2d.translate(0,-descent);
		g2d.setColor(Color.white);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.drawString("Flow direction", 0, 0);
		
		/**
		 * Draw the trigger lines. 
		 */
		drawTrigLine(g, projector, params.getIgnorePoint(), 1);
		drawTrigLine(g, projector, params.getTriggerPoint(), 2);
		double[] midRiver = params.getMidRiverRange();
		for (int i = 0; i < 2; i++) {
			drawBankLine(g, projector, midRiver[i], i);
		}
		
		return true;
	}

	/**
	 * Draw lines to represent river bank regions where detection thresholds are a bit
	 * different. These are parallel to the flow arrow, and the given distance is 
	 * perpendicular to the flow arrow. 
	 * @param g
	 * @param projector
	 * @param distance
	 * @param i
	 */
	private void drawBankLine(Graphics g, GeneralProjector projector, double distance, int iBank) {
//		if (distance == 0) {
//			return;
//		}

		RiverTriggerParams params = riverTriggerControl.getTriggerParams();
		double angle = Math.toRadians(params.flowDirection-30.);
		double sixty = Math.toRadians(60);
		if (projector instanceof SonarXYProjector == false) {
			return;
		}
		SonarXYProjector sonarProjector = (SonarXYProjector) projector;
		double leftRange = distance/Math.cos(angle);
		Coordinate3d leftPt = sonarProjector.getCoord3d(leftRange, sixty, false);
		int x0 = (int) leftPt.x;
		int y0 = (int) leftPt.y;
		SonarImagePanel sonarPanel = sonarProjector.getImagePanel();
		Rectangle clip = sonarPanel.getBounds();
		double len = clip.getWidth();
		len = len*sonarProjector.getSonarZoomTransform().getZoomFactor();
		angle = Math.toRadians(params.flowDirection);
		int x1 = (int) (x0 + len*Math.sin(angle));
		int y1 = (int) (y0 - len*Math.cos(angle));
		
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(Color.WHITE);
		float[] dash = {15,10};
		g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash, 0));
		g2d.drawLine(x0, y0, x1, y1);
		String txt;
		if (iBank == 0) {
			txt = "Near bank region";
		}
		else {
			txt = "Far bank region";
		}
		// need to rotate text. 
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.translate(x0, y0);
		g2d.rotate(Math.toRadians(params.flowDirection-90));
		int descent = g2d.getFontMetrics().getMaxAscent();
		int xPos = descent*10;
		if (iBank == 1) {
			descent = -descent/5;
		}
		g2d.translate(0,+descent);
		g2d.drawString(txt, xPos, 0);
	}

	/**
	 * Draw a line across the display perpendicular to the flow direction and going through point 
	 * @param g
	 * @param projector
	 * @param point
	 * @param i
	 */
	private void drawTrigLine(Graphics g, GeneralProjector projector, double[] point, int lineType) {
		if (point == null || point.length != 2) {
			return;
		}
		RiverTriggerParams params = riverTriggerControl.getTriggerParams();
		double angle = Math.toRadians(params.flowDirection);
		double sixty = Math.toRadians(60);
		if (projector instanceof SonarXYProjector == false) {
			return;
		}
		SonarXYProjector sonarProjector = (SonarXYProjector) projector;
		double[] p1 = point;
		double[] p2 = {0,0};
		double[] v1 = {Math.cos(angle), -Math.sin(angle)};
		double[] v2 = {Math.sin(sixty), Math.cos(sixty)};
		double[] p3 = {0, sonarProjector.getMaxRange()};
		double[] v3 = {1,0};
		
		double[] intersect = getIntersect(p1, v1, p2, v2);
		if (intersect == null) {
			return;
		}
		double[] intersect2 = getIntersect(p1, v1, p3, v3);
		
		p1 = xyTorThi(p1);
		intersect = xyTorThi(intersect);
		intersect2 = xyTorThi(intersect2);
		// need to convert back to r thi coordinates. 
		
		Coordinate3d pointXY = sonarProjector.getCoord3d(p1[0], p1[1], false);
		Coordinate3d c1 = sonarProjector.getCoord3d(intersect[0], intersect[1], false);
		Coordinate3d c2 = sonarProjector.getCoord3d(intersect2[0], intersect2[1], false);
		Graphics2D g2d = (Graphics2D) g.create();
		PamSymbolChooser symbolChooser = projector.getPamSymbolChooser();
		PamSymbol symbol = symbolChooser.getPamSymbol(projector, null);
		g2d.setColor(symbol.getLineColor());
		g2d.setStroke(new BasicStroke(2));
		if (c1 == null || c2 == null) {
//			if (c1 != null) {
//				g2d.drawLine((int) c1.x, (int) c1.y, 0, 0);
//			}
//			if (c2 != null) {
//				g2d.drawLine((int) c2.x, (int) c2.y, 0, 0);
//			}
			return;
		}
//		g2d.setColor(Color.gray);
//		g2d.drawLine((int) c1.x, (int) c1.y, (int) c2.x, (int) c2.y);
		g2d.setColor(lineType == 1 ? Color.BLUE : Color.RED);
		float[] dash = {15,10};
		g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash, 0));
		g2d.drawLine((int) c1.x, (int) c1.y, (int) c2.x, (int) c2.y);
		
		g2d.translate(pointXY.x, pointXY.y);
		g2d.rotate(Math.toRadians(params.flowDirection));
		int descent = g2d.getFontMetrics().getMaxAscent();
		g2d.translate(0,+descent);
		String str = null;
		if (lineType == 1) {
			str = "Trigger if track ends";
		}
		else if (lineType == 2) {
			str = "Trigger immediately";
		}
		else {
			return;
		}
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.drawString(str, 0, 0);
	}
	
	private double[] xyTorThi(double[] xy) {
		double[] rthi = new double[2];
		rthi[0] = Math.sqrt(xy[0]*xy[0]+xy[1]*xy[1]);
		rthi[1] = Math.atan2(-xy[0], xy[1]);
		return rthi;
	}
	
	/**
	 * Intersect between two vectors passing through points p with unit vectors v. 
	 * @param p1
	 * @param v1
	 * @param p2
	 * @param v2
	 * @return interext point, or null if lines are parallel. 
	 */
	private double[] getIntersect(double[] p1, double[] v1, double[] p2, double[] v2) {
		double[][] md = {{v1[0], v2[0]}, {v1[1], v2[1]}};
		double[][] ad = {{p2[0]-p1[0]},{p2[1]-p1[1]}};
		Matrix m = new Matrix(md);
		Matrix ans = new Matrix(ad);
		try {
//			m = m.transpose();
			Matrix minv = m.inverse();
			Matrix pos = minv.times(ans);
			double[] uv = pos.getColumnPackedCopy();
			double[] intcept = new double[2];
			intcept[0] = p1[0]+v1[0]*uv[0];
			intcept[1] = p1[1]+v1[1]*uv[0];
			return intcept;
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	

}
