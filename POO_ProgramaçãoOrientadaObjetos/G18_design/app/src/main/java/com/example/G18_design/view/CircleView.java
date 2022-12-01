package com.example.G18_design.view;

import android.graphics.Canvas;

import com.example.G18_design.model.Circle;

public class CircleView extends FigureView {

    public CircleView(Circle circle) {
        super(circle);
    }
    @Override
    void draw(Canvas canvas) {
        Circle circle = (Circle) figure;
        canvas.drawCircle((float)this.figure.getStart().x,
                (float)this.figure.getStart().y,
                (float)circle.getRadius(),
                brush);
    }
}
