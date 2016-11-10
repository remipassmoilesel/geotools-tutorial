package playground;

import org.geotools.coverage.CoverageFactoryFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.referencing.CRS;
import org.geotools.referencing.factory.epsg.CartesianAuthorityFactory;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.remipassmoilesel.utils.GuiBuilder;

import javax.swing.undo.UndoManager;
import java.awt.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

/**
 * Created by remipassmoilesel on 19/10/16.
 */
public class Playground {

    private static StyleFactory sf = CommonFactoryFinder.getStyleFactory();
    private static FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

    public static void main(String[] args) throws FactoryException {


        System.out.println(System.nanoTime());

        System.out.println(CRS.decode("EPSG:404000"));

        System.out.println(CRS.parseWKT("LOCAL_CS[\"Wildcard 2D cartesian plane in metric unit\", \n" +
                "  LOCAL_DATUM[\"Unknown\", 0], \n" +
                "  UNIT[\"m\", 1.0], \n" +
                "  AXIS[\"x\", EAST], \n" +
                "  AXIS[\"y\", NORTH], \n" +
                "  AUTHORITY[\"EPSG\",\"404000\"]]"));
    }


}
