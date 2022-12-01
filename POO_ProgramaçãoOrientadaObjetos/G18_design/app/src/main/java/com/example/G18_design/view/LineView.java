package com.example.G18_design.view;

import android.graphics.Canvas;

import com.example.G18_design.model.Line;

public class LineView extends FigureView {

    public LineView(Line line){
        super(line);
    }

    @Override
    void draw(Canvas canvas) {
        canvas.drawLine(this.figure.getStart().x,
                this.figure.getStart().y,
                this.figure.getEnd().x,
                this.figure.getEnd().y,
                this.brush);
    }
}
