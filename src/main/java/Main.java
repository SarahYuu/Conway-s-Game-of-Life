import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {

    int frameCount = 0;
    Label label = new Label();
    Timer timer = new Timer();
    // Creating a canvas
    final Canvas canvas = new Canvas(900, 600);
    // Use the graphics context to draw on a canvas.
    GraphicsContext gc = canvas.getGraphicsContext2D();

    String state = "block";
    int manual = 0;

    // 2D array to store states of the graph
    int[][] grid = new int[50][75];
    int[][] newgrid = new int[50][75];

    @Override
    public void start(Stage stage) throws Exception {
        // Create the Toolbar && Create buttons
        Image image1 = new Image("block.png", 20, 20, false, true);
        Button block = new Button("Block", new ImageView(image1));
        Image image2 = new Image("beehive.png", 30, 20, false, true);
        Button beehive = new Button("Beehive", new ImageView(image2));
        Image image3 = new Image("blinker.png", 20, 20, false, true);
        Button blinker = new Button("Blinker", new ImageView(image3));
        Image image4 = new Image("toad.png", 30, 20, false, true);
        Button toad = new Button("Toad", new ImageView(image4));
        Image image5 = new Image("glider.png", 20, 20, false, true);
        Button glider = new Button("Glider", new ImageView(image5));
        Image image6 = new Image("clear.png", 20, 20, false, true);
        Button clear = new Button("Clear", new ImageView(image6));

        ToolBar toolbar = new ToolBar(block, beehive, new Separator(), blinker, toad, glider, new Separator(), clear);

        // Button actions
        block.setOnAction(event -> { state = "block"; });
        beehive.setOnAction(event -> { state = "beehive"; });
        blinker.setOnAction(event -> { state = "blinker"; });
        toad.setOnAction(event -> { state = "toad"; });
        glider.setOnAction(event -> { state = "glider"; });
        clear.setOnAction(event -> {
            for (int i = 0; i < 75; i++) {
                for (int j = 0; j < 50; j++) {
                    newgrid[j][i] = 0;
                }
            }
            state = "clear";
            // If manual is on, no timer ticks. So we have to update the grid once ourselves.
            if (manual == 1) {
                // Copy the new grid to old grid
                copyGrid();
                updateGrid();
            }
        });

        canvas.setOnMouseClicked((MouseEvent event) -> {
            int x = (int) event.getX() / 12;
            int y = (int) event.getY() / 12;
            if (x >= 75) x--;
            if (y >= 50) y--;

            if (state.equals("block")) {
                newgrid[y][x] = 1;
                if (x+1 < 75) newgrid[y][x+1] = 1;
                if (y+1 < 50) newgrid[y+1][x] = 1;
                if ((x+1 < 75) && (y+1 < 50)) newgrid[y+1][x+1] = 1;
            } else if (state.equals("beehive")) {
                if (x+1 < 75) newgrid[y][x+1] = 1;
                if (x+2 < 75) newgrid[y][x+2] = 1;
                if (y+1 < 50) newgrid[y+1][x] = 1;
                if ((x+3 < 75) && (y+1 < 50)) newgrid[y+1][x+3] = 1;
                if ((x+1 < 75) && (y+2 < 50)) newgrid[y+2][x+1] = 1;
                if ((x+2 < 75) && (y+2 < 50)) newgrid[y+2][x+2] = 1;
            } else if ((state.equals("blinker")) && (y+1 < 50)) {
                newgrid[y+1][x] = 1;
                if (x+1 < 75) newgrid[y+1][x+1] = 1;
                if (x+2 < 75) newgrid[y+1][x+2] = 1;
            } else if (state.equals("toad")) {
                if (x+1 < 75) newgrid[y][x+1] = 1;
                if (x+2 < 75) newgrid[y][x+2] = 1;
                if (x+3 < 75) newgrid[y][x+3] = 1;
                if (y+1 < 50) {
                    newgrid[y+1][x] = 1;
                    if (x+1 < 75) newgrid[y+1][x+1] = 1;
                    if (x+2 < 75) newgrid[y+1][x+2] = 1;
                }
            } else if (state.equals("glider")) {
                if (x+2 < 75) newgrid[y][x+2] = 1;
                if (y+1 < 50) newgrid[y+1][x] = 1;
                if ((x+2 < 75) && (y+1 < 50)) newgrid[y+1][x+2] = 1;
                if ((x+1 < 75) && (y+2 < 50)) newgrid[y+2][x+1] = 1;
                if ((x+2 < 75) && (y+2 < 50)) newgrid[y+2][x+2] = 1;
            }

            // Manual Mode, we need to update the grid once.
            if (manual == 1) {
                copyGrid();
                updateGrid();
            }
        });

        // Creating the initial canvas.
        for (int i = 0; i <= 50; i++) {
            gc.strokeLine(0, i * 12, 900, i * 12);
        }
        for (int i = 0; i <= 75; i++) {
            gc.strokeLine(i * 12, 0, i * 12, 600);
        }

        // Timer ticks everytime we want to advance a frame.
        // This timer code is taken from Piazza thread@265
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handle_animation();
            }
        }, 0, 1000);

        // Creating the status bar
        HBox hbox = new HBox(label);
        hbox.setAlignment(Pos.BASELINE_RIGHT);
        // Creating the Root and the Scene
        VBox root = new VBox(toolbar, canvas, hbox);
        Scene scene = new Scene(root, 900, 660);

        // Pause and Resume functionality
        scene.setOnKeyPressed(event -> {
            if ((event.getCode() == KeyCode.M) && (manual == 0)) {
                // Pause
                timer.cancel();
                manual = 1;
            } else if ((event.getCode() == KeyCode.M) && (manual == 1)) {
                // Resume
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        handle_animation();
                    }
                }, 0, 1000);
                manual = 0;
            } else if ((event.getCode() == KeyCode.N) && (manual == 1)) {
                // Advance to the next frame.
                handle_animation();
            }
        });

        stage.setTitle("Conway's Game of Life (xr2yu)");
        stage.setMaxWidth(1600);
        stage.setMaxHeight(1200);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    // This code is taken partially from Piazza thread@265
    public void handle_animation() {
        // if state is clear (nothing is on board), no need to check the rules.
        if (!state.equals("clear")) gridRules();

        // Copy the new grid to old grid
        copyGrid();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Fix Frame Rate
                frameCount ++;
                label.setText("Frame " + frameCount);

                updateGrid();
            }
        });
    }

    @Override
    public void stop() throws Exception {
        timer.cancel();
    }

    // Copy the new grid to old grid
    public void copyGrid() {
        for (int i = 0; i < 75; i++) {
            for (int j = 0; j < 50; j++) {
                grid[j][i] = newgrid[j][i];
            }
        }
    }

    // Loop through grid to fill blocks or not.
    public void updateGrid() {
        for (int i = 0; i < 75; i++) {
            for (int j = 0; j < 50; j++) {
                if (grid[j][i] == 1) {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(i * 12, j * 12, 11, 11);
                } else {
                    gc.setFill(Color.WHITE);
                    gc.fillRect(i * 12, j * 12, 11.5, 11.5);
                }
            }
        }
    }

    // Changes the newgrid based on rules concerning a cell's neighbor.
    public void gridRules() {
        // Rules concerning the neighbors.
        // Loop through the old grid, make changes to the new grid.
        int liveNeighbor = 0;
        for (int i = 0; i < 75; i++) {
            for (int j = 0; j < 50; j++) {
                // Count live neighbors first.
                liveNeighbor = 0;
                if (j-1 >= 0) {
                    if (grid[j-1][i] == 1) liveNeighbor++;
                    if ((i-1 >= 0) && (grid[j-1][i-1] == 1)) liveNeighbor++;
                    if ((i+1 < 75) && (grid[j-1][i+1] == 1)) liveNeighbor++;
                }
                if ((i-1 >= 0) && (grid[j][i-1] == 1)) liveNeighbor++;
                if ((i+1 < 75) && (grid[j][i+1] == 1)) liveNeighbor++;
                if (j+1 < 50) {
                    if (grid[j+1][i] == 1) liveNeighbor++;
                    if ((i-1 >= 0) && (grid[j+1][i-1] == 1)) liveNeighbor++;
                    if ((i+1 < 75) && (grid[j+1][i+1] == 1)) liveNeighbor++;
                }
                // if the cell is live
                if ((grid[j][i] == 1) && (liveNeighbor != 2) && (liveNeighbor != 3)) {
                    newgrid[j][i] = 0;
                } else if ((grid[j][i] == 0) && (liveNeighbor == 3)) {
                    // the cell is not live and has exactly 3 live neighbors
                    newgrid[j][i] = 1;
                }
            }
        }

    }

}


