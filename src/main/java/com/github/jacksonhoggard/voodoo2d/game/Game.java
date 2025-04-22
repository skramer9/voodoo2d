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
    private final AABB testBox;
    private Mesh testMesh;
    private GameObject testObject;
    private GameObject monster;
    private MapTree testTree;
    private Window windowRef;

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
        windowRef = window;
        renderer.init(window);
        player.init();
        mapTree.init();
        testTree.init();

        // Test Object
        testMesh = Mesh.loadMesh("textures/player.png", 64);
        testObject = new GameObject(testMesh);
        testObject.setPosition(1.5f, 1.5f);
        testObject.setScale(0.2f);
        testBox.setCenter(testObject.getPosition());
        testBox.setDistance(new Vector2f(.025f, .025f));

        //  Monster
        Mesh monsterMesh = Mesh.loadMesh("textures/enemy.png", 64); // replace with actual texture
        monster = new GameObject(monsterMesh);
        monster.setPosition(3.0f, 3.0f); // customize position
        monster.setScale(0.2f);

        gameObjects = new GameObject[]{
                mapTree.getMapBack(),
                mapTree.getMapFront(),
                player,
                mapTree.getMapTop(),
                testObject,
                monster
        };
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

        // 🧪 Test collision (map change logic)
        if (testBox.intersects(player.hitBox)) {
            gameObjects[0] = testTree.getMapBack();
            gameObjects[1] = testTree.getMapFront();
            gameObjects[3] = testTree.getMapTop();
        }

        // ✅ Monster collision — restart game
        if (player.collidesWith(monster)) {
            System.out.println("💀 Player collided with a MONSTER! Restarting...");
            restartGame();
            return;
        }

        // Comment out swing for now (doesn't exist in Player)
        /*
        if (player.swing.getCenter() != null && testBox.intersects(player.swing)) {
            Log.engine().info("hit");
        }
        */
    }

    private void restartGame() {
        try {
            init(windowRef);
        } catch (Exception e) {
            System.err.println("Game restart failed: " + e.getMessage());
            e.printStackTrace();
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
