package com.rfe.selectiononmap;

import android.location.Location;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapCircleControls implements
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {

    private boolean circleExist = false;
    private Circle circle = null;
    private Marker centerOfCircle = null;
    private Marker pointOnCircle = null;
    private GoogleMap map = null;
    private int diskColor;
    private int circleColor;
    private float stroke;

    private LatLng oldCntr = null; // for correcting the position of the point
    private LatLng oldPoint = null;//  when the user drags the marker


    public MapCircleControls(GoogleMap map, int diskColor, int circleColor, float stroke) {
        this.map = map;
        this.diskColor = diskColor;
        this.circleColor = circleColor;
        this.stroke = stroke;
    }

    private void addFigureOnMap(LatLng center, LatLng point) {

        if (circle != null)
            circle.remove();

        float[] radius = new float[1];
        Location.distanceBetween(center.latitude,
                center.longitude, point.latitude, point.longitude, radius);

        CircleOptions circleOpt = new CircleOptions()
                .center(center)
                .radius(radius[0])
                .strokeWidth(this.stroke)
                .strokeColor(this.circleColor)
                .fillColor(this.diskColor);

        circle = this.map.addCircle(circleOpt);
    }

    @Override
    public void onMapClick(LatLng arg0) {
        if (!circleExist) {

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

            addFigureOnMap(markOptCenter.getPosition(), markOptPoint.getPosition());
            circleExist = true;
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
        if (arg0.getId().equals(centerOfCircle.getId())) {
            LatLng newCntr = arg0.getPosition();
            Double deltaLat = newCntr.latitude - oldCntr.latitude;
            Double deltaLng = newCntr.longitude - oldCntr.longitude;

            LatLng newPoint = new LatLng(oldPoint.latitude + deltaLat,
                    oldPoint.longitude + deltaLng);
            pointOnCircle.setPosition(newPoint);

            addFigureOnMap(newCntr, newPoint);
        } else {
            addFigureOnMap(centerOfCircle.getPosition(), arg0.getPosition());
        }
    }

    public void clearMap() {
        this.map.clear();
        this.oldCntr = null;
        this.oldPoint = null;
        this.circle = null;
        this.circleExist = false;
    }
}