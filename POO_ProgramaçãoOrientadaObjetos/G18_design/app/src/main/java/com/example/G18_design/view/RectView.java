package com.example.G18_design.view;

import android.graphics.Canvas;

import com.example.G18_design.model.Rect;

public class RectView extends FigureView {

    public RectView(Rect rect){
        super(rect);
    }

    @Override
    void draw(Canvas canvas) {
        canvas.drawRect(this.figure.getStart().x,
                this.figure.getStart().y,
                this.figure.getEnd().x,
                this.figure.getEnd().y,
                this.brush);
    }
}
