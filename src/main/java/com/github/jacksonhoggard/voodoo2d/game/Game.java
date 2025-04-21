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

public class Game implements IGameLogic {

    private final Camera camera;
    private final Renderer renderer;
    private final MapTree mapTree;
    private GameObject[] gameObjects;
    private final Player player;
    private final AABB testBox; //added to test hitboxes
    private Mesh portalMesh;
    private GameObject testObject;
    private MapTree secondTree;
    private MapTree thirdTree;

    // created array to keep track of the maps
    public static int mapNumber = 0;
    private MapTree[] allMaps;

    private Enemy enemy1;
    private Enemy enemy2;
    private Enemy enemy3;

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
        mapTree = new MapTree("firstMap.tmx");
        testBox = new AABB();
        secondTree = new MapTree("secondMap.tmx");
        thirdTree = new MapTree("thirdMap.tmx");
        enemy1 = new Enemy();
        enemy2 = new Enemy();
        enemy3 = new Enemy();
        allMaps = new MapTree[]{mapTree, secondTree, thirdTree};
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        player.init();
        mapTree.init();
        portalMesh = Mesh.loadMesh("textures/Portal.png", 32);
        testObject = new GameObject(portalMesh);
        testObject.setPosition(0,1.5f);
        testObject.setScale(.2f);
        secondTree.init();
        thirdTree.init();
        enemy1.init();
        enemy2.init();
        enemy3.init();
        //this is in order, things at the top of the list are behind things later in the list
        gameObjects = new GameObject[] {
                mapTree.getMapBack(),
                mapTree.getMapFront(),
                player,
                testObject,
                enemy1,
                enemy2,
                enemy3,
                mapTree.getMapTop()
        };
        enemy1.setPosition(-1.5f, -1.5f);
        enemy2.setPosition(1.5f, -1.2f);
        enemy3.setPosition(-1.5f, 1.5f);
        testBox.setCenter(testObject.getPosition());
        testBox.setDistance(new Vector2f(.025f, .025f));
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
        enemy1.update();
        enemy2.update();
        enemy3.update();
        // if the player intersects with the hitbox, trigger the fade to change the background
        if (!hasTriggeredFade && testBox.intersects(player.hitBox) && mapNumber < allMaps.length - 1) {
            fading = true;
            fadeTimer = 0f;
            hasTriggeredFade = true;
            enemy1.init();
            enemy2.init();
            enemy3.init();
            enemy1.setPosition(-1.5f, -1f);
            enemy2.setPosition(1.5f, 1.2f);
            enemy3.setPosition(-1.5f, 1.5f);
            testObject.setPosition(0,-1.4f);
            mapNumber++;
        }
        // handle fade transition
        if (fading) {
            fadeTimer += Timer.getDeltaTime();
            fadeOverlay.setPosition(camera.getPosition().x, camera.getPosition().y);
            if (fadeTimer >= fadeDuration / 2 && gameObjects[0] != allMaps[mapNumber].getMapBack()) {
                gameObjects[0] = allMaps[mapNumber].getMapBack();
                gameObjects[1] = allMaps[mapNumber].getMapFront();
                gameObjects[gameObjects.length - 1] = allMaps[mapNumber].getMapTop();
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

    public static int getMapNumber() {
        return mapNumber;
    }
}
