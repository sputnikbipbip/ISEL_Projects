package com.example.G18_design.model;

import android.util.Log;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class DesignModel {


    private ArrayList<Figure> designs = new ArrayList<>();

    public void add(Figure f) {
        designs.add(f);
    }

    public void delete(){
        designs.clear();
    }

    public void save(PrintStream to) {
        to.println(designs.size());
        for(Figure d: designs)
            switch (d.getLetter()){
                case "C" :
                    Circle circle = (Circle)d;
                    to.println(d.getLetter()+" ("+d.getStart().x+","+d.getStart().y+") |"+ circle.getRadius()+"|");
                    break;
                case "P" :
                    to.println(d.getLetter()+" ("+d.getStart().x+","+d.getStart().y+")");
                    break;
                case "L" :
                case "R" :
                    to.println(d.getLetter()+" ("+d.getStart().x+","+d.getStart().y+") "+"("+d.getEnd().x+","+d.getEnd().y+")");
                    break;
                default:
                    break;
            }
    }

    public ArrayList<Figure> load(Scanner in) {
        int numberOfFigures = Integer.parseInt(in.nextLine());
        while(in.hasNext()) {
            String coordinates = in.nextLine();
            coordinates = coordinates.replaceAll("[()]" , "");
            coordinates = coordinates.replaceAll("[||]" , "");
            coordinates = coordinates.replaceAll("[,]" , " ");
            coordinates = coordinates.trim();
            String[] values = coordinates.split(" ");
            switch (values[0].charAt(0)) {
                case 'R':
                    Rect rect = new Rect(Integer.parseInt(values[1]), Integer.parseInt(values[2]));
                    designs.add(rect);
                    rect.setEnd(Integer.parseInt(values[3]),Integer.parseInt(values[4]));
                    break;
                case 'L':
                    Line line = new Line(Integer.parseInt(values[1]), Integer.parseInt(values[2]));
                    designs.add(line);
                    line.setEnd(Integer.parseInt(values[3]),Integer.parseInt(values[4]));
                    break;
                case 'C':
                    Circle circle = new Circle(Integer.parseInt(values[1]), Integer.parseInt(values[2]));
                    designs.add(circle);
                    circle.setRadius(Integer.parseInt(values[3]));
                    break;
                case 'P':
                    Pixel pixel = new Pixel(Integer.parseInt(values[1]), Integer.parseInt(values[2]));
                    designs.add(pixel);
                    break;
                default:
                    Log.v("Error on the creation of a figure", "APP");
                    break;
            }
        }
        return designs;
    }
}
