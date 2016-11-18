package org.remipassmoilesel.swing;

/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

import net.miginfocom.swing.MigLayout;
import org.geotools.map.GridCoverageLayer;
import org.geotools.map.MapContent;
import org.geotools.map.RasterLayer;
import org.geotools.swing.MapLayerTable;
import org.geotools.swing.action.*;
import org.geotools.swing.control.JMapStatusBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * A Swing frame containing a map display pane and (optionally) a toolbar,
 * status bar and map layer table.
 * <p>
 * Simplest use is with the static {@link #showMap(MapContent)} method:
 * <pre>{@code \u0000
 * MapContent content = new MapContent();
 * content.setTitle("My beautiful map");
 *
 * // add some layers to the MapContent...
 *
 * JMapFrame.showMap(content);
 * }</pre>
 *
 * @author Michael Bedward
 * @source $URL$
 * @version $Id$
 * @see MapLayerTable
 * @see StatusBar
 * @since 2.6
 */

/**
 * Code from Geotools, unchanged.
 * <p>
 * Copied here for misc experiments
 */

public class CustomMapFrame extends JFrame {
    
    /* 
     * The following toolbar button names are primarily for unit testing
     * but could also be useful for applications wanting to control appearance
     * and behaviour at run-time.
     */

    /**
     * Name assigned to toolbar button for feature info queries.
     */
    public static final String TOOLBAR_INFO_BUTTON_NAME = "ToolbarInfoButton";
    /**
     * Name assigned to toolbar button for map panning.
     */
    public static final String TOOLBAR_PAN_BUTTON_NAME = "ToolbarPanButton";
    /**
     * Name assigned to toolbar button for default pointer.
     */
    public static final String TOOLBAR_POINTER_BUTTON_NAME = "ToolbarPointerButton";
    /**
     * Name assigned to toolbar button for map reset.
     */
    public static final String TOOLBAR_RESET_BUTTON_NAME = "ToolbarResetButton";
    /**
     * Name assigned to toolbar button for map zoom in.
     */
    public static final String TOOLBAR_ZOOMIN_BUTTON_NAME = "ToolbarZoomInButton";
    /**
     * Name assigned to toolbar button for map zoom out.
     */
    public static final String TOOLBAR_ZOOMOUT_BUTTON_NAME = "ToolbarZoomOutButton";

    /**
     * Constants for available toolbar buttons used with the
     * {@link #enableTool} method.
     */
    public enum Tool {
        /**
         * Simple mouse cursor, used to unselect previous cursor tool.
         */
        POINTER,

        /**
         * The feature info cursor tool
         */
        INFO,

        /**
         * The panning cursor tool.
         */
        PAN,

        /**
         * The reset map extent cursor tool.
         */
        RESET,

        /**
         * The zoom display cursor tools.
         */
        ZOOM;
    }

    private boolean showToolBar;
    private Set<org.geotools.swing.JMapFrame.Tool> toolSet;

    /*
     * UI elements
     */
    private CustomMapPane mapPane;
    private MapLayerTable mapLayerTable;
    private JToolBar toolBar;

    private boolean showStatusBar;
    private boolean showLayerTable;
    private boolean uiSet;

    /**
     * Creates a new map frame with a toolbar, map pane and status
     * bar; sets the supplied {@code MapContent}; and displays the frame.
     * If {@linkplain MapContent#getTitle()} returns a non-empty string,
     * this is used as the frame's title.
     * <p>
     * This method can be called safely from any thread.
     *
     * @param content the map content
     */
    public static void showMap(final MapContent content) {

        if (SwingUtilities.isEventDispatchThread()) {
            doShowMap(content);
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    doShowMap(content);
                }
            });
        }
    }

    private static void doShowMap(MapContent content) {
        final org.geotools.swing.JMapFrame frame = new org.geotools.swing.JMapFrame(content);
        frame.enableStatusBar(true);
        frame.enableToolBar(true);
        frame.initComponents();
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    /**
     * Default constructor. Creates a {@code JMapFrame} with
     * no map content or renderer set
     */
    public CustomMapFrame() {
        this(null);
    }

    /**
     * Constructs a new {@code JMapFrame} object with specified map content.
     *
     * @param content the map content
     */
    public CustomMapFrame(MapContent content) {
        super(content == null ? "" : content.getTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        showLayerTable = false;
        showStatusBar = false;
        showToolBar = false;
        toolSet = EnumSet.noneOf(org.geotools.swing.JMapFrame.Tool.class);

        // the map pane is the one element that is always displayed
        mapPane = new CustomMapPane(content);
        mapPane.setBackground(Color.WHITE);
        mapPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // give keyboard focus to the map pane
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                mapPane.requestFocusInWindow();
            }
        });

        mapPane.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                mapPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }

            @Override
            public void focusLost(FocusEvent e) {
                mapPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            }
        });

        mapPane.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                mapPane.requestFocusInWindow();
            }
        });
    }

    /**
     * Sets whether to display the default toolbar (default is false).
     * Calling this with state == true is equivalent to
     * calling {@link #enableTool} with all {@link org.geotools.swing.JMapFrame.Tool}
     * constants.
     *
     * @param enabled whether the toolbar is required
     */
    public void enableToolBar(boolean enabled) {
        if (enabled) {
            toolSet = EnumSet.allOf(org.geotools.swing.JMapFrame.Tool.class);
        } else {
            toolSet.clear();
        }
        showToolBar = enabled;
    }

    /**
     * This method is an alternative to {@link #enableToolBar(boolean)}.
     * It requests that a tool bar be created with specific tools, identified
     * by {@link org.geotools.swing.JMapFrame.Tool} constants.
     * <p>
     * <code><pre>
     * myMapFrame.enableTool(Tool.PAN, Tool.ZOOM);
     * </pre></code>
     * <p>
     * Calling this method with no arguments or {@code null} is equivalent
     * to {@code enableToolBar(false)}.
     *
     * @param tool tools to display on the toolbar
     */
    public void enableTool(org.geotools.swing.JMapFrame.Tool... tool) {
        if (tool == null || tool.length == 0) {
            enableToolBar(false);
        } else {
            toolSet = EnumSet.copyOf(Arrays.asList(tool));
            showToolBar = true;
        }
    }

    /**
     * Set whether a status bar will be displayed to display cursor position
     * and map bounds.
     *
     * @param enabled whether the status bar is required.
     */
    public void enableStatusBar(boolean enabled) {
        showStatusBar = enabled;
    }

    /**
     * Set whether a map layer table will be displayed to show the list
     * of layers in the map content and set their order, visibility and
     * selected status.
     *
     * @param enabled whether the map layer table is required.
     */
    public void enableLayerTable(boolean enabled) {
        showLayerTable = enabled;
    }

    /**
     * Calls {@link #initComponents()} if it has not already been called explicitly
     * to construct the frame's components before showing the frame.
     *
     * @param state true to show the frame; false to hide.
     */
    @Override
    public void setVisible(boolean state) {
        if (state && !uiSet) {
            initComponents();
        }

        super.setVisible(state);
    }

    /**
     * Creates and lays out the frame's components that have been
     * specified with the enable methods (e.g. {@link #enableToolBar(boolean)} ).
     * If not called explicitly by the client this method will be invoked by
     * {@link #setVisible(boolean) } when the frame is first shown.
     */
    public void initComponents() {
        if (uiSet) {
            // @todo log a warning ?
            return;
        }

        /*
         * We use the MigLayout manager to make it easy to manually code
         * our UI design
         */
        StringBuilder sb = new StringBuilder();
        if (!toolSet.isEmpty()) {
            sb.append("[]"); // fixed size
        }
        sb.append("[grow]"); // map pane and optionally layer table fill space
        if (showStatusBar) {
            sb.append("[min!]"); // status bar height
        }

        JPanel panel = new JPanel(new MigLayout(
                "wrap 1, insets 0", // layout constrains: 1 component per row, no insets

                "[grow]", // column constraints: col grows when frame is resized

                sb.toString()));

        /*
         * A toolbar with buttons for zooming in, zooming out,
         * panning, and resetting the map to its full extent.
         * The cursor tool buttons (zooming and panning) are put
         * in a ButtonGroup.
         *
         * Note the use of the XXXAction objects which makes constructing
         * the tool bar buttons very simple.
         */
        if (showToolBar) {
            toolBar = new JToolBar();
            toolBar.setOrientation(JToolBar.HORIZONTAL);
            toolBar.setFloatable(false);

            JButton btn;
            ButtonGroup cursorToolGrp = new ButtonGroup();

            if (toolSet.contains(org.geotools.swing.JMapFrame.Tool.POINTER)) {
                btn = new JButton(new NoToolAction(mapPane));
                btn.setName(TOOLBAR_POINTER_BUTTON_NAME);
                toolBar.add(btn);
                cursorToolGrp.add(btn);
            }

            if (toolSet.contains(org.geotools.swing.JMapFrame.Tool.ZOOM)) {
                btn = new JButton(new ZoomInAction(mapPane));
                btn.setName(TOOLBAR_ZOOMIN_BUTTON_NAME);
                toolBar.add(btn);
                cursorToolGrp.add(btn);

                btn = new JButton(new ZoomOutAction(mapPane));
                btn.setName(TOOLBAR_ZOOMOUT_BUTTON_NAME);
                toolBar.add(btn);
                cursorToolGrp.add(btn);

                toolBar.addSeparator();
            }

            if (toolSet.contains(org.geotools.swing.JMapFrame.Tool.PAN)) {
                btn = new JButton(new PanAction(mapPane));
                btn.setName(TOOLBAR_PAN_BUTTON_NAME);
                toolBar.add(btn);
                cursorToolGrp.add(btn);

                toolBar.addSeparator();
            }

            if (toolSet.contains(org.geotools.swing.JMapFrame.Tool.INFO)) {
                btn = new JButton(new InfoAction(mapPane));
                btn.setName(TOOLBAR_INFO_BUTTON_NAME);
                toolBar.add(btn);

                toolBar.addSeparator();
            }

            if (toolSet.contains(org.geotools.swing.JMapFrame.Tool.RESET)) {
                btn = new JButton(new ResetAction(mapPane));
                btn.setName(TOOLBAR_RESET_BUTTON_NAME);
                toolBar.add(btn);
            }

            panel.add(toolBar, "grow");
        }

        if (showLayerTable) {
            mapLayerTable = new MapLayerTable(mapPane);

            /*
             * We put the map layer panel and the map pane into a JSplitPane
             * so that the user can adjust their relative sizes as needed
             * during a session. The call to setPreferredSize for the layer
             * panel has the effect of setting the initial position of the
             * JSplitPane divider
             */
            mapLayerTable.setPreferredSize(new Dimension(200, -1));
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                    false,
                    mapLayerTable,
                    mapPane);
            panel.add(splitPane, "grow");

        } else {
            /*
             * No layer table, just the map pane
             */
            panel.add(mapPane, "grow");
        }

        if (showStatusBar) {
            panel.add(JMapStatusBar.createDefaultStatusBar(mapPane), "grow");
        }

        this.getContentPane().add(panel);
        uiSet = true;
    }

    /**
     * Get the map content associated with this frame.
     * Returns {@code null} if no map content has been set explicitly with the
     * constructor or {@link #setMapContent}.
     *
     * @return the current {@code MapContent} object
     */
    public MapContent getMapContent() {
        return mapPane.getMapContent();
    }

    /**
     * Set the MapContent object used by this frame.
     *
     * @param content the map content
     * @throws IllegalArgumentException if content is null
     */
    public void setMapContent(MapContent content) {
        if (content == null) {
            throw new IllegalArgumentException("map content must not be null");
        }

        mapPane.setMapContent(content);
    }

    /**
     * Provides access to the instance of {@code CustomMapPane} being used
     * by this frame.
     *
     * @return the {@code CustomMapPane} object
     */
    public CustomMapPane getMapPane() {
        return mapPane;
    }

    /**
     * Provides access to the toolbar being used by this frame.
     * If {@link #initComponents} has not been called yet
     * this method will invoke it.
     *
     * @return the toolbar or null if the toolbar was not enabled
     */
    public JToolBar getToolBar() {
        if (!uiSet) initComponents();
        return toolBar;
    }
}

