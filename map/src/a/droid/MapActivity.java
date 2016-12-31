package a.droid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ericsson.android.indoormaps.IndoorMapActivity;
import com.ericsson.android.indoormaps.ItemizedOverlay;
import com.ericsson.android.indoormaps.MapController;
import com.ericsson.android.indoormaps.MapController.LoadingListener;
import com.ericsson.android.indoormaps.MapController.MapItemOnFocusChangeListener;
import com.ericsson.android.indoormaps.MapManager;
import com.ericsson.android.indoormaps.MapView;
import com.ericsson.android.indoormaps.MyLocationOverlay;
import com.ericsson.android.indoormaps.Overlay;
import com.ericsson.android.indoormaps.OverlayItem;
import com.ericsson.android.indoormaps.Projection;
import com.ericsson.android.indoormaps.location.IndoorLocationProvider;
import com.ericsson.android.indoormaps.location.IndoorLocationProvider.IndoorLocationListener;
import com.ericsson.android.indoormaps.location.IndoorLocationProvider.IndoorLocationRequestStatus;
import com.ericsson.android.indoormaps.routing.DefaultRoutingService;
import com.ericsson.indoormaps.model.BuildingDescription;
import com.ericsson.indoormaps.model.GeoPoint;
import com.ericsson.indoormaps.model.Location;
import com.ericsson.indoormaps.model.MapDescription;
import com.ericsson.indoormaps.model.MapItem;
import com.ericsson.indoormaps.model.Point;
import com.ericsson.indoormaps.model.StyleDescription;
import com.ericsson.indoormaps.routing.Route;
import com.ericsson.indoormaps.routing.RouteItem;
import com.ericsson.indoormaps.routing.RoutingService;


public class MapActivity extends IndoorMapActivity implements LoadingListener,
View.OnClickListener {
    /** Called when the activity is first created. */
 

	private static final int EXAMPLE_BUILDING_ID = 5008;
	private static final int EXAMPLE_MAP_ID = 21;
	private static final int EXAMPLE_STYLE_ID = 1;

	private static final String API_KEY = "q4vhACi3O7vbRsJbnBGolLXcp87hSsewMePJsaEO";

	protected static final String LOG_TAG = "AndroidExampleClient";

	private MapController mMapController;
	private Route mRoute;

	private MyLocationOverlay mMyLocationOverlay;
	private ImageButton mRouteButton;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setMapView((MapView) findViewById(R.id.indoor_map_view));

		mMapController = getMapView().getMapController();

		// Set listener to get callback when map and style is loading. Good
		// thing to set before calling setMap/setStyle.
		mMapController.setLoadingListener(this);

		mMapController.setMap(EXAMPLE_MAP_ID, API_KEY, true);
		mMapController.setStyle(EXAMPLE_STYLE_ID, API_KEY, true);

		// Display zoom controls
		getMapView().setBuiltInZoomControls(true);

		// Listen to focus change of items on the map
		mMapController
				.setOnFocusChangeListener(new MapItemOnFocusChangeListener() {

					
					public void onMapItemFocusChange(MapItem mapItem) {
						if (mapItem != null) {
							// Get and toast the tags
							Map<String, String> tags = mapItem.getTags();
							String text = "MapItem got focus - tags: " + tags;
							toast(text);

							// Animate to the center of the map item
							mMapController.animateTo(mapItem.getCenter());
						} else {
							// mapItem == null means no MapItem has focus any
							// longer
							toast("MapItem lost focus");
						}

					}
				});

		mRouteButton = (ImageButton) findViewById(R.id.buttonRoute);
		mRouteButton.setOnClickListener(this);
		findViewById(R.id.buttonNext).setOnClickListener(this);
		findViewById(R.id.buttonPrev).setOnClickListener(this);
		findViewById(R.id.buttonLocation).setOnClickListener(this);
		findViewById(R.id.buttonTest).setOnClickListener(this);
	}

	/**
	 * When "Test" button is clicked. This method show API features. Read
	 * through it to get ideas of what you can do with the API.
	 * 
	 * @param v
	 */
	public void testClick(View v) {
		toast("Check logcat output to see what happens here");
		// Find rooms and set them selected.
		final HashMap<String, String> withTags = new HashMap<String, String>();
		withTags.put("room", "yes");
		final List<MapItem> mapItems = MapManager.getMapItems(withTags,
				EXAMPLE_MAP_ID, getApplicationContext());
		mMapController.setSelected(mapItems);

		// Set only restroom poi:s visible
		ArrayList<String> poiTypes = new ArrayList<String>();
		poiTypes.add("restroom");
		mMapController.setVisiblePOITypes(poiTypes);

		// Get overlay
		List<Overlay> mapOverlays = mMapController.getOverlays();

		// Create custom itemized overlay and set its default marker icon
		CustomItemizedOverlay overlay = new CustomItemizedOverlay();
		Drawable defaultIcon = getResources().getDrawable(R.drawable.icon);
		overlay.setDefaultMarker(defaultIcon);

		// Add overlay item that use overlays default icon
		Point point = new Point(100, 10);
		OverlayItem item = new OverlayItem(point, null, "Overlay item 1");
		overlay.addItem(item);
		mapOverlays.add(overlay);

		// Add overlay item that use another icon and has some extra
		// info
		Drawable icon2 = getResources().getDrawable(R.drawable.icon2);
		Point point2 = new Point(50, 10);
		OverlayItem item2 = new OverlayItem(point2, icon2, "Overlay item 2");
		overlay.addItem(item2);

		// Get all rooms
		final HashMap<String, String> withTags2 = new HashMap<String, String>();
		withTags2.put("room", "yes");
		final List<MapItem> mapItemOnAnotherMap = MapManager.getMapItems(
				withTags2, 3, this);
		for (final MapItem mapItem : mapItemOnAnotherMap) {
			for (final Entry<String, String> entry : mapItem.getTags()
					.entrySet()) {
				Log.d(LOG_TAG,
						"other tag: " + entry.getKey() + ", "
								+ entry.getValue());
			}
		}

		// Run some of the request to get some info about maps, buildings and
		// styles.
		getDescriptionsAsync();
	}

	/**
	 * Request location button clicked.
	 * 
	 * @param v
	 */
	public void requestLocation(View v) {
		// Setup Indoor Location API and request a location
		IndoorLocationProvider locationProvider = new IndoorLocationProvider();
		IndoorLocationListener locationListener = new IndoorLocationListener() {

			
			public void onIndoorLocation(double latitude, double longitude,
					int buildingId, int floorId, int horizontalAccuracy) {
				GeoPoint geoPoint = new GeoPoint(latitude, longitude);
				geoPoint.setBuildingId(buildingId);
				geoPoint.setFloorId(floorId);
				Location location = getMapView().getProjection().getLocation(
						geoPoint);

				if (location != null) {
					addMyLocationOverlayIfMissing();
					mMyLocationOverlay.setLocation(location);
					mMyLocationOverlay.setAccuracy(horizontalAccuracy);
					mMyLocationOverlay.setShowAccuracy(true);
					mMapController.animateTo(mMyLocationOverlay.getLocation()
							.getPoint());
				}
				Log.d(LOG_TAG,
						"MapActivity.doThings(...).new IndoorLocationHandler() {...}.onIndoorLocation() - "
								+ geoPoint);
				toast("Location received: " + geoPoint);
			}

		
			public void onError(IndoorLocationRequestStatus status,
					String message) {
				Log.d(LOG_TAG,
						"MapActivity.doThings(...).new IndoorLocationHandler() {...}.onError() - "
								+ message + " status: " + status);
				toast("Location not found: " + message);
			}
		};
		locationProvider.requestIndoorLocation(locationListener, this, API_KEY);
	}

	/**
	 * Example of how to get descriptive objects for maps, buildings and styles.
	 * Getting descriptions are blocking calls and if requested from the server
	 * connectivity dependent. So make them async to avoid blocking the UI
	 * thread.
	 */
	private void getDescriptionsAsync() {

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// Get a single map description
				MapDescription mapDescription;
				try {
					mapDescription = MapManager.getMapDescription(
							EXAMPLE_MAP_ID, API_KEY, MapActivity.this);
					Log.d(LOG_TAG, mapDescription.toString());
				} catch (IOException e) {
					Log.e(LOG_TAG,
							"Map description returned error: " + e.getMessage(),
							e);
				}
				// Get a single building description
				BuildingDescription buildingDescription;
				try {
					buildingDescription = MapManager.getBuildingDescription(
							EXAMPLE_BUILDING_ID, API_KEY, MapActivity.this);
					Log.d(LOG_TAG, buildingDescription.toString());
				} catch (IOException e) {
					Log.e(LOG_TAG,
							"Building description returned error: "
									+ e.getMessage(), e);
				}
				try {
					Context c = getApplicationContext();
					// Get map descriptions for all maps cached on the device
					for (MapDescription b : MapManager.getMapDescriptions(c,
							true, API_KEY)) {
						Log.d(LOG_TAG, "Maps - On device: " + b.toString());
					}
					// Get map descriptions for all maps accessible for your API
					// key on the server
					for (MapDescription b : MapManager.getMapDescriptions(c,
							false, API_KEY)) {
						Log.d(LOG_TAG, "Maps - On server: " + b.toString());
					}
					// Get building descriptions for all buildings stored on the
					// device
					for (BuildingDescription b : MapManager
							.getBuildingDescriptions(c, true, API_KEY)) {
						Log.d(LOG_TAG, "Building - On device: " + b.toString());
					}
					// Get building descriptions for all buildings accessible
					// for the API_KEY on
					// the server
					for (BuildingDescription b : MapManager
							.getBuildingDescriptions(c, false, API_KEY)) {
						Log.d(LOG_TAG, "Building - On server: " + b.toString());
					}
					// Get style descriptions for all styles accessible for the
					// API_KEY on the server
					for (StyleDescription b : MapManager.getStyleDescriptions(
							c, API_KEY)) {
						Log.d(LOG_TAG, "Style - On server: " + b.toString());
					}
				} catch (IOException e) {
					Log.d(LOG_TAG, "Could not get maps from server", e);
				}

				return null;
			}

		}.execute();
	}

	/**
	 * 
	 * A custom overlay that draws a circled on the map item in focus. It also
	 * handles touch events to set the location for MyLocationOverlay if the
	 * user long press the map view.
	 * 
	 */
	class CustomOverlay extends Overlay {
		private final GestureDetector mGestureDetector;
		private MapView mTouchedMapView;

		public CustomOverlay() {
			mGestureDetector = new GestureDetector(new MyGestureListener());
		}

		@Override
		public void onTouchEvent(MotionEvent event, MapView mapView) {
			mGestureDetector.onTouchEvent(event);
			mTouchedMapView = mapView;
		}

		@Override
		public void draw(Canvas canvas, MapView mapView) {
			MapItem focusedMapItem = mMapController.getFocusedMapItem();
			if (focusedMapItem != null) {
				// Get canvas coordinates from map coordinates with help from
				// projection
				Projection projection = mapView.getProjection();
				float canvasX = projection.getCanvasCoord(focusedMapItem
						.getCenter().getX());
				float canvasY = projection.getCanvasCoord(focusedMapItem
						.getCenter().getY());

				// Draw circle in the center of focused item
				Paint p = new Paint();
				p.setColor(Color.MAGENTA);
				p.setAlpha(90);
				p.setAntiAlias(true);
				canvas.drawCircle(canvasX, canvasY, 10, p);
			}
		}

		class MyGestureListener extends SimpleOnGestureListener {

			@Override
			public void onLongPress(MotionEvent e) {
				// Calculate the map coordinates for the long pressed pixel
				// coordinate
				Projection p = mTouchedMapView.getProjection();
				Point point = new Point(p.getMapX(e.getX()),
						p.getMapY(e.getY()));

				// Create a Location object for MyLocation set by the long press
				// and att it to MyLocationOverlay
				Location myFakeLocation = new Location(point, getMapView()
						.getBuildingId(), getMapView().getFloorId());

				addMyLocationOverlayIfMissing();

				mMyLocationOverlay.setLocation(myFakeLocation);
				mMyLocationOverlay.setShowAccuracy(false);
				mTouchedMapView.invalidate();
				// Some tactile feedback
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(100);
				super.onLongPress(e);
			}

		}
	}

	private void addMyLocationOverlayIfMissing() {
		// My location overlay
		if (mMyLocationOverlay == null) {
			mMyLocationOverlay = new MyLocationOverlay();
			mMapController.getOverlays().add(mMyLocationOverlay);
		}
	}

	/**
	 * Custom {@link ItemizedOverlay} that listen to tap on a
	 * {@link OverlayItem} and toasts its text.
	 * 
	 */
	class CustomItemizedOverlay extends ItemizedOverlay {
		@Override
		public boolean singleTap(OverlayItem item) {
			if (super.singleTap(item)) {
				// Toast the text of the item
				toast(item.getText());
				// Return true to tell MapView that tap was handeld by overlay.
				return true;
			}
			return false;
		}
	}

	
	public void onMapLoading(LoadingState state, int mapId, String message) {
		Log.d(LOG_TAG, "MapActivity.onMapLoading() - state: " + state
				+ ", message: " + message);

		switch (state) {
		case FINISHED:
			// Get overlays
			List<Overlay> mapOverlays = mMapController.getOverlays();

			// Give example of how to use Projection to get map coordinates for
			// a latitude, longitude coordinate.
			showCaseProjection();

			// CustomOverlay that draws a circle and handles touch input
			// We want only one instance of it in the mapview
			CustomOverlay customOverlay = null;
			for (Overlay o : mapOverlays) {
				if (o instanceof CustomOverlay) {
					customOverlay = (CustomOverlay) o;
					break;
				}
			}
			mapOverlays.remove(customOverlay);
			mapOverlays.add(new CustomOverlay());

			break;
		case ERROR:
			Toast.makeText(MapActivity.this, message, Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
	}

	
	public void onStyleLoading(LoadingState state, int styleId, String message) {
		Log.d(LOG_TAG, "MapActivity.onStyleLoading() - state: " + state);
		if (state == LoadingState.ERROR) {
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Gives example of how to convert a lat, lon coordinate to map coordinates.
	 */
	private void showCaseProjection() {
		// Create a new overlay
		ItemizedOverlay overlay = new ItemizedOverlay();
		// Create a location (representation of map coordinates) from a
		// latitude, longitude point.
		Location locationFromLonLat = getMapView().getProjection().getLocation(
				new GeoPoint(59.40299, 17.94793));
		// The location might be null if the map is missing reference points
		if (locationFromLonLat != null) {
			Point point = locationFromLonLat.getPoint();
			// Create new overlay item and add to overlay
			overlay.addItem(new OverlayItem(point, getResources().getDrawable(
					R.drawable.icon), null));
			// Add overlay to map view
			getMapView().getMapController().getOverlays().add(overlay);
		}
	}

	private void toast(String text) {
		Toast.makeText(MapActivity.this, text, Toast.LENGTH_LONG).show();
	}

	// Play/route button click
	public void route(View v) {
		addMyLocationOverlayIfMissing();
		final Location location = mMyLocationOverlay.getLocation();
		final MapItem focusedMapItem = mMapController.getFocusedMapItem();
		// Check if MyLocation is set and a map item is focused.
		if (mMyLocationOverlay != null && focusedMapItem != null
				&& mRoute == null && location != null) {
			// Run routing async, since it is a blocking call
			new AsyncTask<Void, Void, Route>() {

				Handler handler = new Handler();

				@Override
				protected Route doInBackground(Void... params) {
					// Get a route from MyLocation to selected item
					RoutingService routingService = DefaultRoutingService
							.getRoutingService(getApplicationContext());
					Point from = location.getPoint();
					Point to = focusedMapItem.getCenter();
					try {
						Route r = routingService.getRoute(from, EXAMPLE_MAP_ID,
								to, EXAMPLE_MAP_ID);
						return r;
					} catch (final IllegalArgumentException e) {
						// Tell the user why route could not be calculated. Use
						// handler to display toast on UI thread.
						handler.post(new Runnable() {
							
							public void run() {
								Toast.makeText(MapActivity.this,
										e.getMessage(), Toast.LENGTH_LONG)
										.show();
							}
						});
						return null;
					}
				}

				@Override
				protected void onPostExecute(Route r) {
					mRoute = r;
					if (mRoute != null) {
						getMapView().displayRoute(mRoute);
						mRouteButton
								.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
					}
				}

			}.execute();

		} else if (mRoute != null) {
			mRoute = null;
			getMapView().hideRoute();
			mRouteButton.setImageResource(android.R.drawable.ic_media_play);
		} else {
			toast("Set a location (long press on map) and click on a map item. Then click again to test routing");
		}
	}

	// Next button click
	public void next(View v) {
		if (mRoute != null) {
			getMapView().invalidate();
			RouteItem step = mRoute.next();
			mMapController.animateTo(step.getPoint());
			Toast.makeText(
					this,
					step.getInstruction() + " left: "
							+ mRoute.getDistanceLeft() + " covered: "
							+ mRoute.getDistanceCovered() + " total: "
							+ mRoute.length(), Toast.LENGTH_SHORT).show();
		}
	}

	// Previous button click
	public void prev(View v) {
		if (mRoute != null) {
			getMapView().invalidate();
			RouteItem step = mRoute.prev();
			Toast.makeText(this, step.getInstruction(), Toast.LENGTH_SHORT)
					.show();
		}
	}

	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonLocation:
			requestLocation(v);
			break;
		case R.id.buttonRoute:
			route(v);
			break;
		case R.id.buttonNext:
			next(v);
			break;
		case R.id.buttonPrev:
			prev(v);
			break;
		case R.id.buttonTest:
			testClick(v);
			break;
		default:
			break;
		}

	}
}