package com.rfe.selectiononmap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;

public class MapViewComponent extends RelativeLayout {

    public MapViewComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.map_item, this, true);

        final GoogleMap map = ((MapFragment)(((AppCompatActivity) context)
                .getFragmentManager().findFragmentById(R.id.map))).getMap();

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.MapViewComponent, 0, 0);
        int circleColor= a.getInt(R.styleable.MapViewComponent_circleColor, 0x500000FF);
        int diskColor = a.getInt(R.styleable.MapViewComponent_diskColor, Color.TRANSPARENT);
        float stroke = a.getFloat(R.styleable.MapViewComponent_stroke, 3);
        a.recycle();

        final MapCircleControls mapCircleControls =
                new MapCircleControls(map,diskColor, circleColor, stroke);

        map.setOnMapClickListener(mapCircleControls);
        map.setOnMarkerDragListener(mapCircleControls);

        UiSettings setting = map.getUiSettings();
        setting.setZoomControlsEnabled(true);

        Button removeButton = (Button) findViewById(R.id.remove_button);
        removeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mapCircleControls.clearMap();
            }
        });
    }
}