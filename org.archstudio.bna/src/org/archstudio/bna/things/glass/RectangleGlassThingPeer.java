package org.archstudio.bna.things.glass;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.media.opengl.GL2;

import org.archstudio.bna.IBNAView;
import org.archstudio.bna.ICoordinateMapper;
import org.archstudio.bna.IResources;
import org.archstudio.bna.IThingPeer;
import org.archstudio.bna.facets.IHasSelected;
import org.archstudio.bna.things.AbstractRectangleThingPeer;
import org.archstudio.bna.utils.BNAUtils;
import org.eclipse.swt.graphics.Rectangle;

public class RectangleGlassThingPeer<T extends RectangleGlassThing> extends AbstractRectangleThingPeer<T> implements
		IThingPeer<T> {

	public RectangleGlassThingPeer(T thing) {
		super(thing);
	}

	@Override
	public void draw(IBNAView view, ICoordinateMapper cm, GL2 gl, Rectangle clip, IResources r) {
		if (Boolean.TRUE.equals(t.get(IHasSelected.SELECTED_KEY))) {
			Rectangle lbb = BNAUtils.getLocalBoundingBox(cm, t);
			if (!clip.intersects(lbb)) {
				return;
			}

			Dimension corner = t.getCornerSize();
			Shape localShape = new RoundRectangle2D.Float(lbb.x, lbb.y, lbb.width, lbb.height, Math.min(lbb.width,
					corner.width), Math.min(lbb.height, corner.height));
			BNAUtils.renderShapeSelected(t, view, cm, gl, clip, r, localShape);
		}
	}
}
