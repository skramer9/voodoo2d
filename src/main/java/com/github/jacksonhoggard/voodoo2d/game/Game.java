package com.github.jacksonhoggard.voodoo2d.game;

import com.github.jacksonhoggard.voodoo2d.engine.*;
import com.github.jacksonhoggard.voodoo2d.engine.animation.Animation;
import com.github.jacksonhoggard.voodoo2d.engine.gameObject.AABB;
import com.github.jacksonhoggard.voodoo2d.engine.gameObject.GameObject;
import com.github.jacksonhoggard.voodoo2d.engine.graphic.Mesh;
import com.github.jacksonhoggard.voodoo2d.engine.log.Log;
import org.joml.Vector2f;

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
        //mapTree.init();
        testMesh = Mesh.loadMesh("textures/player.png", 64);
        testObject = new GameObject(testMesh);
        testObject.setPosition(1.5f,1.5f);
        testObject.setScale(.2f);
        testTree.init();
        //this is in order, things at the top of the list are behind things later in the list
        gameObjects = new GameObject[] {
                //mapTree.getMapBack(),
                //mapTree.getMapFront(),
                //player,
                //mapTree.getMapTop(),
                testTree.getMapBack(),
                testTree.getMapFront(),
                player,
                testTree.getMapTop(),
                testObject
        };
        testBox.setCenter(testObject.getPosition());
        testBox.setDistance(new Vector2f(.025f, .025f));
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
        //tests if player runs into the test hitbox
        if (testBox.intersects(player.hitBox)) {
            Log.engine().info("intersection");
        }
        if (player.swing.getCenter() != null
                && testBox.intersects(player.swing)) {
            Log.engine().info("hit");
        }
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameObjects);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameObject gameObject : gameObjects) {
            gameObject.getMesh().cleanUp();
        }
    }
}
