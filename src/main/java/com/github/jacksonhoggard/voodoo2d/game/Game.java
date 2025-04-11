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
    private final AABB testBox; //added to test hitboxes
    private Mesh testMesh;
    private GameObject testObject;
    private MapTree testTree;
    // added to create the black fade transition
    private GameObject fadeOverlay;
    private boolean fading = false;
    private float fadeTimer = 0f;
    private final float fadeDuration = 1.0f;
    private boolean hasTriggeredFade = false;

    public Game() {
        renderer = new Renderer();
        camera = new Camera();
        player = new Player();
        mapTree = new MapTree();
        testBox = new AABB();
        testTree = new MapTree("test.tmx");
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        player.init();
        mapTree.init();
        testMesh = Mesh.loadMesh("textures/player.png", 64);
        testObject = new GameObject(testMesh);
        testObject.setPosition(1.5f,1.5f);
        testObject.setScale(.2f);
        testTree.init();
        //this is in order, things at the top of the list are behind things later in the list
        gameObjects = new GameObject[] {
                mapTree.getMapBack(),
                mapTree.getMapFront(),
                player,
                mapTree.getMapTop(),
                testObject
        };
        testBox.setCenter(testObject.getPosition());
        testBox.setDistance(new Vector2f(.025f, .025f));
        // setup hitbox collision for player
        player.hitBox = new AABB();
        player.hitBox.setCenter(player.getPosition());
        player.hitBox.setDistance(new Vector2f(0.025f, 0.025f)); //change values
        //set up fade
        Mesh fadeMesh = Mesh.loadMesh("textures/fade.png", 1);
        fadeOverlay = new GameObject(fadeMesh);
        fadeOverlay.setScale(10f); // covers the screen
        fadeOverlay.setPosition(camera.getPosition().x, camera.getPosition().y); // lock to camera
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        player.input(window);
    }

    @Override
    public void update(MouseInput mouseInput) {
        if(player.getPosition().x >= camera.getPosition().x + 1.0F) {
            camera.movePosition(0.75F * Timer.getDeltaTime(), 0);
        }
        if(player.getPosition().x <= camera.getPosition().x - 1.0F) {
            camera.movePosition(-0.75F * Timer.getDeltaTime(), 0);
        }
        if(player.getPosition().y <= camera.getPosition().y - 1.0F) {
            camera.movePosition(0, -0.75F * Timer.getDeltaTime());
        }
        if(player.getPosition().y >= camera.getPosition().y + 1.0F) {
            camera.movePosition(0, 0.75F * Timer.getDeltaTime());
        }
        player.update();
        // if the player intersects with the hitbox, trigger the fade to change the background
        if (!hasTriggeredFade && testBox.intersects(player.hitBox)) {
            fading = true;
            fadeTimer = 0f;
            hasTriggeredFade = true;
        }
        // handle fade transition
        if (fading) {
            fadeTimer += Timer.getDeltaTime();
            fadeOverlay.setPosition(camera.getPosition().x, camera.getPosition().y);
            if (fadeTimer >= fadeDuration / 2 && gameObjects[0] != testTree.getMapBack()) {
                gameObjects[0] = testTree.getMapBack();
                gameObjects[1] = testTree.getMapFront();
                gameObjects[3] = testTree.getMapTop();
            }
            if (fadeTimer >= fadeDuration) {
                fading = false;
                fadeTimer = 0f;
            }
        }
        if (player.swing.getCenter() != null
                && testBox.intersects(player.swing)) {
            Log.engine().info("hit");
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
}
