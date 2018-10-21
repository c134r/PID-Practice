package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class PID_Practice extends ApplicationAdapter {
    SpriteBatch batch;
    Texture line;
    Texture ball;
    OrthographicCamera camera;

    float setPoint;
    float pointPos;
    float pointVel;
    float pointAcc;

    float error;
    float lastError;
    float errorIntegral;
    float errorDerivative;

    float kP;
    float kI;
    float kD;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1080, 720);
        setPoint = Gdx.graphics.getWidth() / 2;
        pointPos = setPoint;
        pointVel = 0;
        pointAcc = 0;

        error = 0;
        lastError = 0;
        errorIntegral = 0;
        errorDerivative = 0;

        kP = 10f;
        kI = 10f;
        kD = 10f;

        Pixmap linePixmap = new Pixmap(1, Gdx.graphics.getHeight(), Pixmap.Format.RGBA8888);
        linePixmap.setColor(Color.BLUE);
        linePixmap.fill();
        line = new Texture(linePixmap);

        Pixmap ballPixmap = new Pixmap(64 + 1, 64 + 1, Pixmap.Format.RGBA8888);
        ballPixmap.setColor(Color.BLACK);
        ballPixmap.fillCircle(32, 32, 32);
        ballPixmap.setColor(Color.WHITE);
        ballPixmap.drawPixel(32, 32);
        ball = new Texture(ballPixmap);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        pollInput();
        PID();
        integrate();
        batch.begin();
        batch.draw(line, setPoint, 0);
        batch.draw(ball, pointPos - 32, Gdx.graphics.getHeight() / 2);
        batch.end();
    }

    public void pollInput() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos);
            pointPos = mousePos.x;
            pointVel = 0;
            pointAcc = 0;
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos);
            setPoint = mousePos.x;
        }
    }

    public void PID() {
        lastError = error;
        error = setPoint - pointPos;
        errorIntegral += error * Gdx.graphics.getDeltaTime();
        errorDerivative = (error - lastError) / Gdx.graphics.getDeltaTime();
        float force = 0;
        force += error * kP;
        force += errorIntegral * kI;
        force += errorDerivative * kD;
        pointAcc += force;
    }

    public void integrate() {
        pointVel += pointAcc * Gdx.graphics.getDeltaTime();
        pointPos += pointVel * Gdx.graphics.getDeltaTime();
        pointAcc = 0;
    }

    @Override
    public void dispose() {
        batch.dispose();
        line.dispose();
        ball.dispose();
    }
}
