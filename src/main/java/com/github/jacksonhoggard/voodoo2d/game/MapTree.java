package com.github.jacksonhoggard.voodoo2d.game;

import java.io.File;

import org.joml.Vector2f;

import com.github.jacksonhoggard.voodoo2d.engine.gameObject.AABB;
import com.github.jacksonhoggard.voodoo2d.engine.gameObject.GameObject;
import com.github.jacksonhoggard.voodoo2d.engine.log.Log;
import com.github.jacksonhoggard.voodoo2d.engine.mapping.MapHost;

public class MapTree {

    private MapHost map;
    private GameObject mapBack;
    private GameObject mapFront;
    private GameObject mapTop;
    // variables for map walls
    public static AABB wallRight;
    public static AABB wallLeft;
    public static AABB wallTop;
    public static AABB wallBottom;

    public MapTree() {
        map = new MapHost("src" + File.separator + "main" + File.separator +
                "resources" + File.separator + "maps" + File.separator + "example.tmx");
    }

    //Added this so you can construct a map other than example.tmx
    public MapTree(String file) {
        map = new MapHost("src" + File.separator + "main" + File.separator +
                "resources" + File.separator + "maps" + File.separator + file);
    }

    public void init() {
        Log.game().info("Loading tree map");
        map.init();
        map.setScale(3.0f);
        mapBack = map.getMap().getLayers()[0].asGameObject();
        mapFront = map.getMap().getLayers()[1].asGameObject();
        mapTop = map.getMap().getLayers()[2].asGameObject();
        // map walls to create map boundaries
        wallRight = new AABB();
        wallRight.setCenter(new Vector2f(1.5f, 1.5f));
        wallRight.setDistance(new Vector2f(0.01f, 3.0f));
        wallLeft = new AABB();
        wallLeft.setCenter(new Vector2f(-1.5f, 1.5f));
        wallLeft.setDistance(new Vector2f(0.01f, 3.0f));
        wallTop = new AABB();
        wallTop.setCenter(new Vector2f(0.0f, 1.5f));
        wallTop.setDistance(new Vector2f(1.5f, 0.01f));
        wallBottom = new AABB();
        wallBottom.setCenter(new Vector2f(0.0f, -1.5f));
        wallBottom.setDistance(new Vector2f(1.5f, 0.05f));
    }

    public GameObject getMapBack() {
        return mapBack;
    }

    public GameObject getMapFront() {
        return mapFront;
    }

    public GameObject getMapTop() {
        return mapTop;
    }
}
