package b451_Project.render;

import b451_Project.global.GameVariables;
import b451_Project.global.WindowVariables;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

/**
 * This Class generates particle effect
 */

public class ParticleGenerator {

    class Particle
    {
        public int objectID;
        public float vx;
        public float vy;
        public float x;
        public float y;
        public float span;
    }

    private float generateInterval;
    private float direction;
    private float dRange;
    private float velocity;
    private float vRange;
    private Color color;
    private float lifeSpan;
    private float friction;
    private float x;
    private float y;
    private float orderView;
    private ArrayList<Particle> particleList;
    private double intervalCounter = 0;
    RenderFactory rf;
    private Random ranGen;

    /**
     * @param generateInterval time interval to generate a new particle
     * @param direction particle shooting direction (in degrees)
     * @param dRange random direction range, that is set particle's direction within range of random(direction-dRange, direction + dRange)
     * @param velocity particle initial velocity
     * @param vRange random velocity range, that is set particle's initial velocity within range of random(velocity - vRange, velocity + vRange)
     * @param color particle color
     * @param lifeSpan particle survival time in seconds
     * @param x particle generator x location
     * @param y particle generator y location
     * @param friction particle friction
     * @param orderView z-depth value
     * @param rf render factory instance
     * */
    public ParticleGenerator(float generateInterval, float direction, float dRange, float velocity, float vRange, Color color, float lifeSpan, float x, float y, float friction, float orderView, RenderFactory rf)
    {
        //particles must have lifeSpan
        if(lifeSpan == -1)
        {
            this.lifeSpan = 1;
        }
        this.lifeSpan = lifeSpan;
        this.generateInterval = generateInterval;
        this.direction = direction;
        this.dRange = dRange;
        this.velocity = velocity;
        this.vRange = vRange;
        this.color = color;
        this.x = x;
        this.y = y;
        this.friction = friction;
        this.orderView = orderView;
        this.rf = rf;
        this.ranGen = new Random();
        this.particleList = new ArrayList<Particle>();
    }

    /**
     * set particle generator location
     * @param x center x location
     * @param y center y location
     * */
    public void setLocation(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * set particle generator shooting direction
     * @oaram d degree
     * */
    public void setRotation(float d)
    {
        this.direction = d;
    }

    /**
     * destroy particle generator
     * */
    public void setDestroy()
    {
        for(int i = particleList.size() - 1; i >=0; i--)
        {
            rf.setPolygonDestroy(particleList.get(i).objectID);
            particleList.remove(i);
        }
        stop();
    }

    /**
     * stop generating new particles
     * */
    public void stop()
    {
        generateInterval = 999999999999999999f;
    }

    public float getSpan()
    {
        return lifeSpan;
    }

    /**
     * should be called internally by render Factory to redraw particles
     */
    public void tick(double dt)
    {
        //remove died particles
        for(int i = particleList.size() - 1; i >=0; i--)
        {
            if(particleList.get(i).span <=0)
            {
                rf.setPolygonDestroy(particleList.get(i).objectID);
                particleList.remove(i);
            }
        }

        //generate new particles
        intervalCounter += dt;
        while(intervalCounter >= generateInterval)
        {
            float vel = velocity + (ranGen.nextFloat() * vRange * 2) - vRange;
            float dir = direction + (ranGen.nextFloat() * dRange * 2) - dRange;

            Particle p = new Particle();
            p.span = lifeSpan;
            p.x = this.x;
            p.y = this.y;
            p.vx = (float)(Math.cos(Math.toRadians(dir)) * vel);
            p.vy = (float)(Math.sin(Math.toRadians(dir)) * vel);
            p.objectID = rf.makePolygon(-1, -1, color, 8, 5, orderView, 0, p.x, p.y);
            particleList.add(p);

            intervalCounter-= generateInterval;
        }

        //update particles
        for(Particle p : particleList)
        {
            p.span -= dt;
            p.vx *= friction;
            p.vy *= friction;
            p.x += p.vx;
            p.y +=p.vy;

            rf.setPolygonCenterLocation(p.objectID, p.x, p.y);
        }

    }
}
