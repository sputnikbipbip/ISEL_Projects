package com.example.G18_design;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.example.G18_design.model.Circle;
import com.example.G18_design.model.DesignModel;
import com.example.G18_design.model.Figure;
import com.example.G18_design.model.Line;
import com.example.G18_design.model.Pixel;
import com.example.G18_design.model.Rect;
import com.example.G18_design.view.CircleView;
import com.example.G18_design.view.DesignView;
import com.example.G18_design.view.FigureView;
import com.example.G18_design.view.LineView;
import com.example.G18_design.view.PixelView;
import com.example.G18_design.view.RectView;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class DesignCtrl extends Activity implements View.OnTouchListener {

    private DesignModel model;
    private DesignView view;
    private Figure currentFigure;
    private String fileName = "Design.txt";

    private Figure createFigure(int id, MotionEvent event) {
        switch (id) {
            case R.id.Rect:
                return new Rect((int) event.getX(), (int) event.getY());
            case R.id.Pixel:
                return new Pixel((int) event.getX(), (int) event.getY());
            case R.id.Line:
                return new Line((int) event.getX(), (int) event.getY());
            case R.id.Circle:
                return new Circle((int) event.getX(), (int) event.getY());
            default:
                return null;
        }
    }

    private FigureView createFigureView(Figure figure) {
        switch(figure.getClass().getSimpleName()) {
            case "Rect":
                return new RectView((Rect) figure);
            case "Circle":
                return new CircleView((Circle) figure);
            case "Pixel":
                return new PixelView((Pixel) figure);
            case "Line":
                return new LineView((Line) figure);
            default:
                return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.designView);
        view.setModel(model = new DesignModel());
        view.setOnTouchListener(this);

        final Button resetButton = findViewById(R.id.reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.delete();
                model.delete();
                view.invalidate();
            }
        });

        final Button saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(fileName);
                try (PrintStream output = new PrintStream(openFileOutput(file.getName(), Context.MODE_PRIVATE))) {
                    model.save(output);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        final Button loadButton = findViewById(R.id.load);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(fileName);
                try (Scanner input = new Scanner(openFileInput(file.getName()))) {
                    ArrayList<Figure> newModel = model.load(input);
                    for (Figure d: newModel) {
                        view.add(createFigureView(d));
                    }
                    view.invalidate();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                RadioGroup group = findViewById(R.id.radioGroup);
                currentFigure = createFigure(group.getCheckedRadioButtonId(), event);
                model.add(currentFigure);
                view.add(createFigureView(currentFigure));
                view.invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                currentFigure.setEnd((int)event.getX(), (int)event.getY());
                view.invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                currentFigure.setEnd((int)event.getX(), (int)event.getY());
                currentFigure = null;
                view.invalidate();
                return true;
        }
        return false;
    }
}
