package com.example.g18_covidExtermination.Game;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.g18_covidExtermination.Game.model.Board;
import com.example.g18_covidExtermination.Game.model.CovidListener;
import com.example.g18_covidExtermination.Game.model.Direction;
import com.example.g18_covidExtermination.Game.model.Empty;
import com.example.g18_covidExtermination.Game.model.Game;
import com.example.g18_covidExtermination.Game.model.Hero;
import com.example.g18_covidExtermination.Game.model.HeroListener;
import com.example.g18_covidExtermination.Game.model.Level;
import com.example.g18_covidExtermination.Game.model.Piece;
import com.example.g18_covidExtermination.Game.model.Trash;
import com.example.g18_covidExtermination.Game.model.Virus;
import com.example.g18_covidExtermination.Game.model.Wall;
import com.example.g18_covidExtermination.Game.view.DeadTile;
import com.example.g18_covidExtermination.Game.view.EmptyTile;
import com.example.g18_covidExtermination.Game.view.HeroTile;
import com.example.g18_covidExtermination.Game.view.TrashTile;
import com.example.g18_covidExtermination.Game.view.VirusTile;
import com.example.g18_covidExtermination.Game.view.WallTile;
import com.example.g18_covidExtermination.R;
import com.example.g18_covidExtermination.tile.Tile;
import com.example.g18_covidExtermination.tile.TilePanel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Scanner;

public class GameControl extends Activity {

    private TilePanel panel;
    private TextView levelText;
    private TextView virusText;
    private int levelCounter = 1;
    private int virusCounter = 0;
    private Game model;
    private Board board;
    private HeroTile heroView;
    private VirusTile virusView;
    private DeadTile deadView;
    private EmptyTile emptyView;
    private Level level;
    private boolean saveFileExistent = false;
    private final String fileName = "covid_save.txt";
    private final String rotationFile = "rotation_save.txt";

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //saveGame(rotationFile);
        //loadGame(rotationFile);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        panel = findViewById(R.id.tilePanel);
        levelText = findViewById(R.id.levelNumber);
        virusText = findViewById(R.id.virusNumber);
        levelText.setText(Integer.toString(levelCounter));
        findViewById(R.id.leftButton).setOnClickListener(v -> board.moveHero(Direction.LEFT));
        findViewById(R.id.rightButton).setOnClickListener(v -> board.moveHero(Direction.RIGHT));
        emptyView = new EmptyTile();
        heroView = new HeroTile(this);
        virusView = new VirusTile(this);
        deadView = new DeadTile(this);

        final Button popButton = findViewById(R.id.popButton);
        popButton.setVisibility(View.INVISIBLE);
        popButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: adicionar metodo para avançar no nível
            }
        });

        final Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGame(fileName);
            }
        });
        final Button loadButton = findViewById(R.id.loadButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!saveFileExistent)
                    restart();
                else {
                    try {
                        loadGame(fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        startGame();

        board.setOnMoveListener(new HeroListener(){
            public void onHeroMoved(int Dx, int Dy, int x, int y, Direction dir) {
                Tile aux = panel.getTile(x,y);
                if(aux instanceof EmptyTile) {
                    panel.setTile(Dx, Dy, emptyView);
                    panel.setTile(x, y, heroView);
                    board.swap(Dx, Dy, x, y);
                }else if(aux instanceof VirusTile && dir.Dx + x >= 0 && dir.Dx + x < panel.getWidth()
                        && panel.getTile(dir.Dx + x, dir.Dy + y) instanceof EmptyTile ) {
                    panel.setTile(Dx, Dy, emptyView);
                    panel.setTile(x, y, heroView);
                    panel.setTile(dir.Dx + x, dir.Dy + y, virusView);
                    List<Virus> toUpdatePositions = board.getVirus();
                    for(Virus v: toUpdatePositions) {
                        if(v.getX() == x && v.getY() == y) {
                            v.setCoordinates(dir.Dx + x, dir.Dy + y);
                            break;
                        }
                    }
                    board.swap(Dx, Dy, x, y);
                    board.swap(Dx, Dy, dir.Dx + x, dir.Dy + y);
                }else if(aux instanceof TrashTile) {
                    panel.setTile(Dx, Dy, deadView);
                    endGame();
                }
            }
        });
        board.setOnCovidListener(new CovidListener() {
            @Override
            public void onCovidMoved(int Dx, int Dy, int x, int y) {
                if(x == -1 && y == -1) {
                    panel.setTile(Dx, Dy, emptyView);
                    virusText.setText(Integer.toString(--virusCounter));
                    if(board.getVirus().isEmpty())
                        endGame();
                }else{
                    panel.setTile(Dx, Dy, emptyView);
                    panel.setTile(x, y, virusView);
                    List<Virus> updatePositions = board.getVirus();
                    for(Virus v: updatePositions) {
                        if(v.getX() == x && v.getY() == y) {
                            v.setCoordinates(x, y);
                            break;
                        }
                    }
                    board.swap(Dx, Dy, x, y);
                }
            }
        });
    }

    public void startGame() {
        Scanner in = new Scanner(getResources().openRawResource(R.raw.covid_levels));
        model = new Game(in);
        updateFields();
        panel.setHeartbeatListener(350, (n, t) -> board.moveHero(Direction.DOWN));
    }

    public void saveGame(String fileName) {
        try {
            OutputStream os = openFileOutput(fileName, MODE_PRIVATE);
            model.saveGame(os, levelCounter);
            saveFileExistent = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadGame(String fileName) throws IOException {
        levelCounter = 1;
        virusCounter = 0;
        levelText.setText(Integer.toString(levelCounter));
        virusText.setText(Integer.toString(virusCounter));
        InputStream is = openFileInput(fileName);
        model.loadGame(is);
        updateFields();
    }

    private void endGame() {
        if(virusCounter == 0) {
            Toast.makeText(this, "Level Completed", Toast.LENGTH_LONG).show();
            levelText.setText(Integer.toString(++levelCounter));
            model.nextLevel();
            updateFields();
        }else if(virusCounter > 0 ) {
            Toast.makeText(this, "GameOver, pay up", Toast.LENGTH_LONG).show();
            restart();
        }
    }

    public void updateFields() {
        level = model.getLevel();
        board = model.getBoard();
        panel.setSize(board.getPanelWidth(), board.getPanelHeight());
        setPanel(level.getBoard());
    }

    public void restart() {
        finish();
        startActivity(getIntent());
        overridePendingTransition(0,0);
        startGame();
    }

    public void setPanel(Piece[][] aux) {
        Tile[][] t = new Tile[level.getHeight()][level.getWidth()];
        for(int i = 0; i < level.getHeight(); i++) {
            for (int j = 0; j < level.getWidth(); j++) {
                if (aux[i][j] instanceof Hero)
                    t[i][j] = new HeroTile(this);
                if (aux[i][j] instanceof Wall)
                    t[i][j] = new WallTile(this);
                if (aux[i][j] instanceof Trash)
                    t[i][j] = new TrashTile(this);
                if (aux[i][j] instanceof Virus) {
                    t[i][j] = new VirusTile(this);
                    virusText.setText(Integer.toString(++virusCounter));
                }
                if (aux[i][j] instanceof Empty)
                    t[i][j] = new EmptyTile();
            }
        }
        panel.setAllTiles(t);
    }
}
