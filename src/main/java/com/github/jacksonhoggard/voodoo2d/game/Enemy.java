package com.github.jacksonhoggard.voodoo2d.game;

import com.github.jacksonhoggard.voodoo2d.engine.Timer;
import com.github.jacksonhoggard.voodoo2d.engine.Window;
import com.github.jacksonhoggard.voodoo2d.engine.animation.Animation;
import com.github.jacksonhoggard.voodoo2d.engine.gameObject.AABB;
import com.github.jacksonhoggard.voodoo2d.engine.gameObject.GameObject;
import com.github.jacksonhoggard.voodoo2d.engine.graphic.Mesh;
import com.github.jacksonhoggard.voodoo2d.engine.log.Log;
import org.joml.Vector2f;

public class Enemy extends GameObject {
    private Animation[] animations;
    private Vector2f deltaPosition;
    private Vector2f lastPosition;
    private Vector2f enemyPos;
    private final float enemySpeed = 0.0025F;
    public AABB hitBox;

    public Enemy() {
        super();
        animations = new Animation[0];
        deltaPosition = new Vector2f(1,0);
        lastPosition = new Vector2f(0, 0);
        setScale(0.2f);
    }

    public void init() {
        this.setMesh(Mesh.loadMesh("textures/pixil-frame-0.png", 32));
        Animation runDown = new Animation(this, 0, 3, 6);
        Animation runLeft = new Animation(this, 4, 7, 6);
        Animation runRight = new Animation(this, 8, 11, 6);
        Animation runUp = new Animation(this, 12, 15, 6);
        animations = new Animation[]{runDown, runLeft, runRight, runUp};
        enemyPos = new Vector2f(0,0);
        hitBox = new AABB();
        hitBox.setCenter(enemyPos); //added player hitbox
        hitBox.setDistance(new Vector2f(.2f, .2f));
    }

    public void update() {
        enemyPos.y = getPosition().y;
        enemyPos.x = getPosition().x;
        enemyPos.y += ((deltaPosition.y * enemySpeed));
        enemyPos.x += ((deltaPosition.x * enemySpeed));
        this.setPosition(enemyPos.x, enemyPos.y);
        if(!lastPosition.equals(enemyPos) && deltaPosition.x == 0 && deltaPosition.y == 0) {
            Log.game().debug("Enemy pos: (x:" + enemyPos.x + ", y: " + enemyPos.y + ")");
            lastPosition.set(enemyPos);
        }
        //swap directions when hitting edges
        if(enemyPos.x >= 1.6 || enemyPos.x <= -1.6) {
            deltaPosition.x = deltaPosition.x * -1;
        }
    }
}
