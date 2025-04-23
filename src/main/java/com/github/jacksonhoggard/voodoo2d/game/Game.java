package com.github.jacksonhoggard.voodoo2d.game;

import org.joml.Vector2f;

import com.github.jacksonhoggard.voodoo2d.engine.Camera;
import com.github.jacksonhoggard.voodoo2d.engine.IGameLogic;
import com.github.jacksonhoggard.voodoo2d.engine.MouseInput;
import com.github.jacksonhoggard.voodoo2d.engine.Renderer;
import com.github.jacksonhoggard.voodoo2d.engine.Timer;
import com.github.jacksonhoggard.voodoo2d.engine.Window;
import com.github.jacksonhoggard.voodoo2d.engine.gameObject.AABB;
import com.github.jacksonhoggard.voodoo2d.engine.gameObject.GameObject;
import com.github.jacksonhoggard.voodoo2d.engine.graphic.Mesh;
import com.github.jacksonhoggard.voodoo2d.engine.log.Log;

public class Game implements IGameLogic {

    private final Camera camera;
    private final Renderer renderer;
    private final MapTree mapTree;
    private GameObject[] gameObjects;
    private final Player player;
    private final AABB testBox;
    private Mesh testMesh;
    private GameObject testObject;
    private MapTree testTree;
    private Enemy enemy1;
    private Enemy enemy2;
    private Enemy enemy3;
    private Enemy enemy4;
    private Enemy enemy5;
    private Enemy enemy6;


    private GameObject fadeOverlay;
    private boolean fading = false;
    private float fadeTimer = 0f;
    private final float fadeDuration = 1.0f;
    private boolean hasTriggeredFade = false;

    private Window gameWindow;

    // Static map number
    private static int mapNumber = 0;

    public static int getMapNumber() {
        return mapNumber;
    }

    public Game() {
        renderer = new Renderer();
        camera = new Camera();
        player = new Player();
        mapTree = new MapTree();
        testBox = new AABB();
        testTree = new MapTree("test.tmx");
        enemy1 = new Enemy();
        enemy2 = new Enemy();
        enemy3 = new Enemy();
        enemy4 = new Enemy();
        enemy5 = new Enemy();
        enemy6 = new Enemy();
    }

    @Override
    public void init(Window window) throws Exception {
        gameWindow = window;
        renderer.init(window);
        player.init();
        mapTree.init();
        testMesh = Mesh.loadMesh("textures/player.png", 64);
        testObject = new GameObject(testMesh);
        testObject.setPosition(1.5f, 1.5f);
        testObject.setScale(.2f);
        testTree.init();
        enemy1.init();
        enemy2.init();
        enemy3.init();
        enemy4.init();
        enemy5.init();
        enemy6.init();

        gameObjects = new GameObject[]{
                mapTree.getMapBack(),
                mapTree.getMapFront(),
                player,
                testObject,
                enemy1,
                enemy2,
                enemy3,
                enemy4,
                enemy5,
                enemy6,
                mapTree.getMapTop()
        };

        enemy1.setPosition(-1.5f, -1.5f);
        enemy2.setPosition(1.5f, -1.2f);
        enemy3.setPosition(-1.5f, 1.5f);

        testBox.setCenter(testObject.getPosition());
        testBox.setDistance(new Vector2f(.025f, .025f));

        Mesh fadeMesh = Mesh.loadMesh("textures/fade.png", 1);
        fadeOverlay = new GameObject(fadeMesh);
        fadeOverlay.setScale(10f);
        fadeOverlay.setPosition(camera.getPosition().x, camera.getPosition().y);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        player.input(window);
    }

    @Override
    public void update(MouseInput mouseInput) {
        if (player.getPosition().x >= camera.getPosition().x + 1.0F) {
            camera.movePosition(0.75F * Timer.getDeltaTime(), 0);
        }
        if (player.getPosition().x <= camera.getPosition().x - 1.0F) {
            camera.movePosition(-0.75F * Timer.getDeltaTime(), 0);
        }
        if (player.getPosition().y <= camera.getPosition().y - 1.0F) {
            camera.movePosition(0, -0.75F * Timer.getDeltaTime());
        }
        if (player.getPosition().y >= camera.getPosition().y + 1.0F) {
            camera.movePosition(0, 0.75F * Timer.getDeltaTime());
        }

        player.update();
        enemy1.update();
        enemy2.update();
        enemy3.update();


        // Restart game on player-enemy collision
        if (player.hitBox.intersects(enemy1.hitBox) ||
                player.hitBox.intersects(enemy2.hitBox) ||
                player.hitBox.intersects(enemy3.hitBox)) {
            restartGame();
            return;
        }

        // Trigger fade and map change
        if (!hasTriggeredFade && testBox.intersects(player.hitBox)) {
            fading = true;
            fadeTimer = 0f;
            hasTriggeredFade = true;
            enemy1.setPosition(-1.5f, -1f);
            enemy2.setPosition(1.5f, 1.2f);
            enemy3.setPosition(-1.5f, 1.5f);
            enemy4.setPosition(0.5f, 1.5f);
            enemy5.setPosition(0.0f, -1.2f);
            enemy6.setPosition(1.5f, -1.5f);

        }

        if (fading) {
            fadeTimer += Timer.getDeltaTime();
            fadeOverlay.setPosition(camera.getPosition().x, camera.getPosition().y);
            if (fadeTimer >= fadeDuration / 2 && gameObjects[0] != testTree.getMapBack()) {
                gameObjects[0] = testTree.getMapBack();
                gameObjects[1] = testTree.getMapFront();
                gameObjects[gameObjects.length - 1] = testTree.getMapTop();
                mapNumber = 1; // Change map number here
            }
            if (fadeTimer >= fadeDuration) {
                fading = false;
                fadeTimer = 0f;
                hasTriggeredFade = false;
            }
        }
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameObjects);
        if (fading) {
            renderer.render(window, camera, new GameObject[]{fadeOverlay});
        }
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameObject gameObject : gameObjects) {
            gameObject.getMesh().cleanUp();
        }
    }

    public void restartGame() {
        Log.game().debug("Player collided with enemy! Restarting game...");
        try {
            init(gameWindow);
        } catch (Exception e) {
            Log.game().error("Exception during game restart"); // improved logging
        }
    }
}
