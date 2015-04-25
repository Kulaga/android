package com.rfe.selectiononmap;

import android.support.v7.app.AppCompatActivity;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements 
	OnMapClickListener, OnMarkerDragListener {

	private boolean isCircleExist = false;
	private Circle circle = null;
	private Marker centerOfCircle = null;
	private Marker pointOnCircle = null;
	private GoogleMap map = null;
	
	private LatLng oldCntr = null; // for correcting the position of the point
	private LatLng oldPoint = null;//  when the user drags the marker
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();		
		map.setOnMapClickListener(this);
		map.setOnMarkerDragListener(this);
		
		UiSettings setting = this.map.getUiSettings();
		setting.setZoomControlsEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_circle) {
			this.map.clear();
			isCircleExist = false;
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void addFigureToMap(LatLng center, LatLng point) {	
		
		if (circle != null)
			circle.remove();
		
		float[] radius = new float[1];
		Location.distanceBetween(center.latitude,
				center.longitude, point.latitude, point.longitude, radius);
		
		CircleOptions circleOpt = new CircleOptions()
				.center(center)
				.radius(radius[0])
				.strokeWidth(3)
				.strokeColor(0x500000FF);
		
		circle = this.map.addCircle(circleOpt);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onMapClick(LatLng arg0) {
		if (!isCircleExist) {
			
			MarkerOptions markOptCenter = new MarkerOptions()
			.draggable(true)
			.position(arg0);
			
			MarkerOptions markOptPoint = new MarkerOptions()
					.draggable(true)
					.position(new LatLng(arg0.latitude, arg0.longitude + 10))
					.flat(true);
			
			pointOnCircle = this.map.addMarker(markOptPoint);
			centerOfCircle = this.map.addMarker(markOptCenter);
			

			oldCntr = centerOfCircle.getPosition();
			oldPoint = pointOnCircle.getPosition();
			
			addFigureToMap(markOptCenter.getPosition(), markOptPoint.getPosition());
			isCircleExist = true;
		}
	}

	@Override
	public void onMarkerDrag(Marker arg0) {
		changePositionOrRadiusOfCircle(arg0);
	}
	
	@Override
	public void onMarkerDragEnd(Marker arg0) {
		oldCntr = centerOfCircle.getPosition();
		oldPoint = pointOnCircle.getPosition();
	}
	@Override
	public void onMarkerDragStart(Marker arg0) {
		changePositionOrRadiusOfCircle(arg0);
	}
	
	private void changePositionOrRadiusOfCircle(Marker arg0) {
		if (arg0.getId().equals(centerOfCircle.getId())){
			LatLng newCntr = arg0.getPosition();
			Double deltaLat = newCntr.latitude - oldCntr.latitude;
			Double deltaLng = newCntr.longitude - oldCntr.longitude;
			
			LatLng newPoint = new LatLng(oldPoint.latitude + deltaLat,
					oldPoint.longitude + deltaLng);
			pointOnCircle.setPosition(newPoint);
			
			addFigureToMap(newCntr, newPoint);
		} else {
			addFigureToMap(centerOfCircle.getPosition(), arg0.getPosition());
		}		
	}
}