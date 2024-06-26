package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Config;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.collision.IHasCollision;

/**
 * @author thechiefpotatopeeler
 * This class is the base for all objects with models in the game world
 * */
public abstract class Entity {

    //TODO: Add numeric IDs for each object
    public static String id;
    public Boolean isRenderable;
    private ModelInstance modelInstance;
    private Vector3 modelOffset;
    private Vector3 position;
    private Vector2 rotation; // (azimuthal, altitude)
    private Vector3 scale;
    private Vector3 velocity;
    public IHasCollision collider;

    /**
     * @param position the initial position
     * @param rotation the initial rotation
     * Constructor for entity class
     * takes a position and rotation offset
     * */
    public Entity(Vector3 position, Vector2 rotation){
        this.position = position;
        this.rotation = rotation;
        this.scale = new Vector3(1, 1, 1);
        setModelOffset(Vector3.Zero);
    }

    public Entity(Vector3 position, Vector2 rotation, Vector3 scale){
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        setModelOffset(Vector3.Zero);
    }

    public abstract String getModelAddress();

    /*public void setModelAddress(String modelAddress) {
        this.modelAddress = modelAddress;
    }*/

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public Vector3 getModelOffset() {
        return modelOffset;
    }

    public void setModelInstance(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }
    public Vector3 getPosition() {
        return position.cpy();
    }
    public void setPosition(Vector3 position) {
        this.position = position;
    }
    public Vector2 getRotation() {
        return rotation;
    }
    public void setRotation(Vector2 rotation) {
        this.rotation = rotation;
    }
    public Vector3 getScale() {
        return scale;
    }
    public void setScale(Vector3 scale) {
        this.scale = scale;
    }
    public Vector3 getVelocity() {
        return velocity;
    }
    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    /**
     * Run periodically in {@link Main#render()} to update entity data
     * */
    public void updatePosition(){
        if(modelInstance != null) {
            Vector3 euler = sphericalToEuler(rotation);
            modelInstance.transform.setFromEulerAnglesRad(euler.x, euler.y, euler.z).trn(position.cpy().add(modelOffset)).scale(scale.x, scale.y, scale.z);
        }
        if(collider != null){
            collider.setCentre(position.cpy(), false);
        }
    }

    public void collisionResponse(Vector3 contactNormal){
        Vector3 norVel = this.velocity.cpy().sub(contactNormal.scl(contactNormal.cpy().dot(velocity.cpy())));
        Vector3 tanVel = this.velocity.cpy().sub(norVel);

        norVel.scl(-0.1f);
        tanVel.scl(0.9f);
        Vector3 newVel = norVel.cpy().add(tanVel);

        Vector3 newPos = getPosition().cpy().add(contactNormal.cpy().scl(Config.COLLISION_PENETRATION));

        Gdx.app.debug("Collision", "Normal: " + contactNormal + " Velocity: " + velocity + " New Velocity: " + newVel + " New Position: " + newPos);

        setVelocity(newVel);
        setPosition(newPos);
    }

    public void setModelOffset(Vector3 offset){
        this.modelOffset = offset;
    }

    public static Vector3 sphericalToEuler(Vector2 spherical){
        return new Vector3((float) (spherical.x * Math.cos(spherical.y)), (float) (spherical.x * Math.sin(spherical.y)), 0);
    }

    public static Vector2 eulerToSpherical(Vector3 euler){
        return new Vector2((float) Math.sqrt(euler.x * euler.x + euler.y * euler.y), (float) Math.atan2(euler.y, euler.x));
    }
}
