package com.ttran.flyingsaucer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Preferences;

import java.util.Random;
//import java.util.prefs.Preferences;


public class FlyingSaucer extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	Circle ufoCircle;
	Texture [] ufos;
	float ufoY =0;
	Texture gameover;
	Texture plane;
	Texture planes[];
	Texture start;
	Texture pause;
	Texture resume;
//	Texture [] planesL1 = {new Texture("plane1.png"),new Texture("plane2.png"),new Texture("plane3.png"),new Texture("plane4.png"),new Texture("plane5.png")};
//	Texture [] planesL2 = {new Texture("plane5.png"),new Texture("plane3.png"),new Texture("plane2.png"),new Texture("plane4.png"),new Texture("plane1.png")};
//	Texture [] planesL3 = {new Texture("plane4.png"),new Texture("plane5.png"),new Texture("plane2.png"),new Texture("plane1.png"),new Texture("plane3.png")};
//	Texture [] planesL4 = {new Texture("plane2.png"),new Texture("plane5.png"),new Texture("plane3.png"),new Texture("plane1.png"),new Texture("plane4.png")};
	int ufoState = 0;
	float gap = 600;
	float velocity = 0;
	int gameState = 0;
	float gravity = 0.7f;
	float maxOffset;
	int score = 0;
	int highScore = 0;
	int scoringPlane = 0;
	int numOfPraises = 5;
	float centerX;
	float centerY;

	BitmapFont scoreFont;
	BitmapFont highScoreFont;
	BitmapFont [] levelFont;
	BitmapFont [] pointFonts = new BitmapFont[10];
	BitmapFont startFont;
	String [] praises = {"Great", "Wonderful", "Fantastic", "Super", "Magfinicient"};


	float planeVelocity = 2;
	float planeVelocity2 = 2;

	int numOfPlanes = 4;
	float [] planeX = new float[numOfPlanes];
	float [] planeOffset = new float[numOfPlanes];
	float distanceBetweenPlanes;
	Random randomGenerator;

	Rectangle [] plane1Rectangles;
	Rectangle [] plane2Rectangles;
	Rectangle [] plane3Rectangles;
	Rectangle [] plane4Rectangles;
	Rectangle [] plane5Rectangles;

	Rectangle startRect;
	Rectangle pauseRect;
	Rectangle resumeRect;
	Rectangle rect;
	Sound ufoSound;
	Sound flyingSound;
	Sound crashingSound;

	FileHandle flyingSoundHandle;
	FileHandle crashingSoundHandle;

	int ufoSoundId = 0;
	int crashingSoundId = 0;

	Texture [] background;
	int gameLevel = 1;

	Preferences pref;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture[4];
		background[0] = new Texture("skybg.png");
		background[1] = new Texture("skybg1.png");
		background[2] = new Texture("skybg2.png");
		background[3] = new Texture("skybg3.png");
		ufos = new Texture[3];
		ufos[0] = new Texture("saucer1.png");
		ufos[1] = new Texture("saucer2.png");
		ufos[2] = new Texture("saucer3.png");
		planes = new Texture[5];
		planes[0] = new Texture("plane1.png");
		planes[1] = new Texture("plane2.png");
		planes[2] = new Texture("plane3.png");
		planes[3] = new Texture("plane4.png");
		planes[4] = new Texture("plane5.png");

		rect = new Rectangle(0,0,1,1);
		startRect = new Rectangle();
		pauseRect = new Rectangle();
		resumeRect = new Rectangle();

		maxOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
		randomGenerator = new Random();
		shapeRenderer = new ShapeRenderer();
		ufoCircle = new Circle();
		plane1Rectangles = new Rectangle[numOfPlanes];
		plane2Rectangles = new Rectangle[numOfPlanes];
		plane3Rectangles = new Rectangle[numOfPlanes];
		plane4Rectangles = new Rectangle[numOfPlanes];
		plane5Rectangles = new Rectangle[numOfPlanes];


		scoreFont = new BitmapFont();
		gameover = new Texture("gameover.png");
		start = new Texture("start.png");
		pause = new Texture("pause.png");
		resume = new Texture("resume.png");
		distanceBetweenPlanes = Gdx.graphics.getWidth() * 3/4;

		levelFont = new BitmapFont[5];
		highScoreFont = new BitmapFont();
		startFont = new BitmapFont();
		centerX = Gdx.graphics.getWidth();
		centerY = Gdx.graphics.getHeight();

		flyingSoundHandle = Gdx.files.internal("ufoflying.mp3");
		crashingSoundHandle = Gdx.files.internal("crashing.mp3");
		crashingSound = Gdx.audio.newSound(crashingSoundHandle);
		flyingSound = Gdx.audio.newSound(flyingSoundHandle);

		initialize();
		startGame();
	}

	public void initialize(){
		highScoreFont.setColor(Color.GOLD);
		highScoreFont.getData().setScale(4);
		startFont.setColor(Color.FIREBRICK);
		startFont.getData().setScale(5);
		pref = Gdx.app.getPreferences("PlayerPreferences");
		for (int i=0; i < 5; i++){
			levelFont[i] = new BitmapFont();
			levelFont[i].setColor(Color.GOLD);
			levelFont[i].getData().setScale(4);
		}

	}

	public void soundUFOFlying(){
		//if (flyingSound != null)
		//	flyingSound.stop();
		flyingSound.play();

	}

	public void soundUFOStop(){
		if (flyingSound != null)
			flyingSound.stop();
		if (crashingSound != null)
			crashingSound.stop();

	}

	public void soundCrashing(){
		//if (crashingSound != null)
		//	crashingSound.stop();
		crashingSound.play();
	}

	private int touching_checking() {
		int result = 0;
//		if (Gdx.input.isTouched()) {
			rect.setPosition(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
			if (rect.overlaps(startRect)) {
				gameState = 1;
				result = 0;
				Gdx.app.log("Start button","tapped!");
			}
			else if (rect.overlaps(pauseRect)){
				Gdx.app.log("Pause Button","Clicked");
				gameState = 3;
				result = 1;
				pause();
			}
			else if (rect.overlaps(resumeRect)){
				gameState = 1;
				result = 1;
			}
			else
			 	return 1;
//				Gdx.app.log("X:Y position: Height",String.valueOf(rect.getX()) + ":" + String.valueOf(rect.getY()) + "Height: "+ Gdx.graphics.getHeight());
//				Gdx.app.log("Start X: Start Y position",String.valueOf(startRect.getX()) + ":" + String.valueOf(startRect.getY()));
//		}
		return result;
	}

	public void startGame(){
		highScore = pref.getInteger("highscore");
		ufoY = Gdx.graphics.getHeight()/2 - planes[0].getHeight()/2;

		for (int i=0; i < numOfPlanes; i++){
			planeX[i] = (Gdx.graphics.getWidth() / 2) - planes[0].getWidth() / 2  + Gdx.graphics.getWidth() + i * (distanceBetweenPlanes);
			planeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight()/2 - gap/2);
			plane1Rectangles[i] = new Rectangle();
			plane2Rectangles[i] = new Rectangle();
			plane3Rectangles[i] = new Rectangle();
			plane4Rectangles[i] = new Rectangle();
			plane5Rectangles[i] = new Rectangle();

			pointFonts[i] = new BitmapFont();
			pointFonts[i].setColor(Color.GOLD);
			pointFonts[i].getData().setScale(5);
		}

//		for (int i=0; i < planes.length; i++){
//			planes[i] = planesL1[i];
//		}

//		for (int i=0; i < numOfPlanes; i++){
//			planeX[i] = (Gdx.graphics.getWidth() / 2) - planes[0].getWidth() / 2  + Gdx.graphics.getWidth() + i * distanceBetweenPlanes;
//			planeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight()/2 - gap/2);
//			plane1Rectangles[i] = new Rectangle();
//			plane2Rectangles[i] = new Rectangle();
//			plane3Rectangles[i] = new Rectangle();
//			plane4Rectangles[i] = new Rectangle();
//		}

	}

	@Override
	public void pause(){
	}

	@Override
	public void render () {

		batch.begin();

		batch.draw(background[gameLevel-1], 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


		if (gameState == 1) {
			if (Gdx.input.justTouched()) {
			    if (touching_checking() == 1) {
					velocity = -12;
					soundUFOFlying();
				}
			}
			for (int i=0; i < numOfPlanes; i++) {
				if (planeX[i] < - planes[0].getWidth()){
					planeX[i] += numOfPlanes * distanceBetweenPlanes;
					// planeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (centerY - gap/2);
					planeOffset[i] = (randomGenerator.nextFloat() + .3f) * (gap);
				}
				planeX[i] -= planeVelocity;
				batch.draw(planes[0], planeX[i] + gap/2, planeOffset[i] + 100);
				batch.draw(planes[1], planeX[i], planeOffset[i] + gap + 100);


				plane1Rectangles[i].set(planeX[i] + gap/2, planeOffset[i] + 100, planes[0].getWidth(), planes[0].getHeight());
				plane2Rectangles[i].set(planeX[i], planeOffset[i] + gap + 100, planes[1].getWidth(), planes[1].getHeight());
			}

			for (int i=0; i < numOfPlanes; i++) {
				if (planeX[i] < - planes[0].getWidth()){
					planeX[i] += numOfPlanes * distanceBetweenPlanes;
					// planeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (centerY - gap/2);
					planeOffset[i] = (randomGenerator.nextFloat() + 0.2f) * (gap);
				}
				planeX[i] -= planeVelocity2;
//				batch.draw(planes[0], planeX[i], gap / 2 - planes[0].getHeight() + planeOffset[i]);
//				batch.draw(planes[1], planeX[i], (gap / 2  + (gap * .5f))- planes[1].getHeight() + planeOffset[i]);
				batch.draw(planes[2], planeX[i] + gap/3, planeOffset[i] + 100 + gap * 2);
				batch.draw(planes[3], planeX[i], planeOffset[i] + 100 + gap * 3);
				batch.draw(planes[4], planeX[i], planeOffset[i] + 100 + gap * 4);

//				plane1Rectangles[i].set(planeX[i], gap  - planes[0].getHeight() + planeOffset[i], planes[0].getWidth(), planes[0].getHeight());
//				plane2Rectangles[i].set(planeX[i], (gap   + (gap * .5f))- planes[1].getHeight() + planeOffset[i], planes[1].getWidth(), planes[1].getHeight());
				plane3Rectangles[i].set(planeX[i] + gap/3, planeOffset[i] + 100 + gap * 2, planes[2].getWidth(), planes[2].getHeight());
				plane4Rectangles[i].set(planeX[i], planeOffset[i] + 100 + gap * 3, planes[3].getWidth(), planes[3].getHeight());
				plane5Rectangles[i].set(planeX[i] + gap, planeOffset[i] + 100 + gap * 4, planes[4].getWidth(), planes[4].getHeight());

			}

//			if (planeX[scoringPlane] < Gdx.graphics.getWidth()/2 && (ufoY < planeOffset[3] + gap * 3)){
			if (planeX[scoringPlane] < Gdx.graphics.getWidth()/2){
				score++;
				if (score == 5 || score == 15 || score == 30) {
					gameLevel++;
					planeVelocity += 1;
					planeVelocity2 += 1;
					if (gameLevel == 2) {
						gap = 600;
//						for (int i=0; i < planes.length; i ++){
//							planes[i] = planesL2[i];
//						}
					}
					else if (gameLevel == 3) {
						gap = 600;
//						for (int i=0; i < planes.length; i ++){
//							planes[i] = planesL3[i];
//						}
					}
					else if (gameLevel == 4) {
//						for (int i=0; i < planes.length; i ++){
//							planes[i] = planesL4[i];
//						}
					}
				}
				if (scoringPlane < numOfPlanes -1){

					scoringPlane++;
				}
				else
					scoringPlane = 0;

			}

			//if (ufoY > 0 || velocity < 0) {
			// if ((ufoY > Gdx.graphics.getHeight()/4 || velocity < 0) && (ufoY < Gdx.graphics.getHeight()) ) {
			if ((ufoY > 0 || velocity < 0) && (ufoY < Gdx.graphics.getHeight()) ) {
				velocity = velocity + gravity;
				ufoY -= velocity;
			}
			else if (ufoY > Gdx.graphics.getHeight()){
				velocity = 12;
				velocity = velocity + 1;
				ufoY -= velocity;
			}
			batch.draw(pause,Gdx.graphics.getWidth() - 250, 10);
			pauseRect.set(Gdx.graphics.getWidth() - 250, 10, pause.getWidth(),pause.getHeight());
		}
		else if (gameState == 0) {

			//startFont.setFixedWidthGlyphs("Click to Start");
			//startFont.draw(batch,"Click to start",Gdx.graphics.getWidth()/2 - 350,Gdx.graphics.getHeight() - 500);
			batch.draw(start,Gdx.graphics.getWidth()/2 - start.getWidth()/2,Gdx.graphics.getHeight() - 1200);
			startRect.set(Gdx.graphics.getWidth() / 2 - start.getWidth() / 2, Gdx.graphics.getHeight() - 1200, start.getWidth(), start.getHeight());
			if (Gdx.input.justTouched()) {
				if (touching_checking() == 0){
//			if (Gdx.input.justTouched()){
					gameState = 1;
				}

			}
		} else if (gameState == 2) {
			batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2 + 100);
//			startFont.draw(batch,"Click to start",Gdx.graphics.getWidth()/2 - 350,Gdx.graphics.getHeight() - 550);
			if (score > highScore) {
				pref.putInteger("highscore", score);
				pref.flush();
			}
			batch.draw(start,Gdx.graphics.getWidth()/2 - start.getWidth()/2,Gdx.graphics.getHeight() - 1200);
			startRect.set(Gdx.graphics.getWidth() / 2 - start.getWidth() / 2, Gdx.graphics.getHeight() - 1200, start.getWidth(), start.getHeight());
			if (Gdx.input.justTouched()) {
				if (touching_checking() == 0){
					gameState = 1;
					score = 0;
					scoringPlane = 0;
					velocity = 0;
					gravity = .5f;
					gameLevel = 1;
					gap = 600;
					planeVelocity = 2;
					planeVelocity2 = 2;
					startGame();
				}
			}

		} else if (gameState == 3) {
			batch.draw(resume, Gdx.graphics.getWidth() / 2 - resume.getWidth()/2,Gdx.graphics.getHeight() - 1200);
			resumeRect.set(Gdx.graphics.getWidth() / 2 - resume.getWidth() / 2, Gdx.graphics.getHeight() - 1200, resume.getWidth(), resume.getHeight());
			if (Gdx.input.justTouched()) {
				touching_checking();
			}
		}


//		if (ufoState == 0) {
//			ufoState = 1;
//		} else {
//			ufoState = 0;
//		}

		batch.draw(ufos[ufoState], Gdx.graphics.getWidth() / 2 - ufos[ufoState].getWidth() / 2, ufoY);
		//batch.draw(car,Gdx.graphics.getWidth()/3,carY);
		ufoCircle.set(Gdx.graphics.getWidth() / 2, ufoY + ufos[ufoState].getHeight() / 2, ufos[ufoState].getHeight() / 2 - 20);

		if (ufoState < 2)
			ufoState++;
		else
			ufoState = 0;

		scoreFont.setColor(Color.WHITE);
		scoreFont.getData().setScale(4);
		scoreFont.draw(batch, "Score: " + score, 200, Gdx.graphics.getHeight() - 200);
		levelFont[0].draw(batch, "Game Level " + gameLevel, 100, 100);
		highScoreFont.draw(batch, "High Score: " + highScore, Gdx.graphics.getWidth() - 500, Gdx.graphics.getHeight() - 200);

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(ufoCircle.x, ufoCircle.y, ufoCircle.radius);
		for (int i=0; i < numOfPlanes; i++) {
			//shapeRenderer.rect(plane1Rectangles[i].x,plane1Rectangles[i].y,planes[0].getWidth(),planes[0].getHeight());
			//shapeRenderer.rect(plane2Rectangles[i].x,plane1Rectangles[i].y,planes[1].getWidth(),planes[1].getHeight());
			if (Intersector.overlaps(ufoCircle,plane1Rectangles[i]) || Intersector.overlaps(ufoCircle,plane2Rectangles[i]) ||
				Intersector.overlaps(ufoCircle,plane3Rectangles[i]) || Intersector.overlaps(ufoCircle,plane4Rectangles[i]) || Intersector.overlaps(ufoCircle,plane5Rectangles[i])
					|| ufoY - ufos[0].getHeight()/2 < 0){
				gravity = .5f;
				gameState = 2;

			}
		}
		//shapeRenderer.end();
		batch.end();
	}
}
