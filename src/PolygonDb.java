import java.awt.Dimension;
import java.awt.Polygon;

public class PolygonDb {
	double Area;
	public PolygonDb(Polygon poly1) {
		Dimension dim = poly1.getBounds( ).getSize( );
		Area = dim.width * dim.height;
	}	
}
