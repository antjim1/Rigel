package ch.epfl.rigel.gui;

import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class EpflLogo extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(800, 300);
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        Transform scale = Transform.scale(0, 0, 0, 0);
        // Fond blanc
        ctx.setFill(Color.WHITE);
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Texte EPFL rouge
        ctx.setFont(Font.font("Helvetica", 300));
        ctx.setFill(Color.RED);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.fillText("EPFL", 400, 250);

        // Trous dans le E et le F
        ctx.setFill(Color.WHITE);
        ctx.fillRect(50, 126, 30, 26);
        ctx.fillRect(440, 135, 28, 22);

        primaryStage.setScene(new Scene(new BorderPane(canvas)));
        primaryStage.show();
    }
}
