package com.example.G18_design.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.example.G18_design.model.DesignModel;

import java.util.ArrayList;

public class DesignView extends View {

    private DesignModel model;
    private ArrayList<FigureView> designFigures = new ArrayList<>();

    public DesignView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setModel(DesignModel model) {
        this.model = model;
    }

    public void add(FigureView figureView) {
        designFigures.add(figureView);
    }

    public void delete() {
        designFigures.clear();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(FigureView figureView : designFigures) {
            figureView.draw(canvas);
        }
    }
}
