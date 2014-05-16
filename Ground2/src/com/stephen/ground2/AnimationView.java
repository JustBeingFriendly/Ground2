package com.stephen.ground2;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.Path.Direction;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AnimationView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

	private Thread animation = null;
	private boolean running;
	float height;
	float width;
	private Bitmap mars;
	private BitmapShader marsShade;
	Paint paint = new Paint();
	Paint marsBackground = new Paint();
	
	private int[] FeatureArray;
	
	private Random random = new Random();
	
	float bottomThirdScreen;

	public AnimationView(Context context) {
		super(context);
		mars = BitmapFactory.decodeResource(getResources(), R.drawable.mars);
		marsShade = (new BitmapShader(mars, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
		marsBackground.setShader(marsShade);
		
		getHolder().addCallback(this);
		

	}

	public AnimationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mars = BitmapFactory.decodeResource(getResources(), R.drawable.mars);
		marsShade = (new BitmapShader(mars, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
		marsBackground.setShader(marsShade);
		
		getHolder().addCallback(this);
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// start the animation thread once the surface has been created
		animation = new Thread(this);
		running = true;
		animation.start(); // start a new thread to handle this activities
							// animation
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		running = false;
		if (animation != null) {
			try {
				animation.join(); // finish the animation thread and let the
									// animation thread die a natural death
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		//Finds screen width and height
		if (width == 0){			
			width = this.getWidth();
			height = this.getHeight();
			bottomThirdScreen = (height / 3) * 2; //Finds bottom third of screen, use as baseline x position for ground drawing			
		}
		
		PsuedoRandomSort();
		
		while (running) {
			Canvas canvas = null;
			SurfaceHolder holder = getHolder();

			synchronized (holder) {
				canvas = holder.lockCanvas();
				canvas.drawColor(Color.WHITE); //Background Colour
				drawGround(canvas);
				
			}
			holder.unlockCanvasAndPost(canvas);
		}
	}
	
	
	private void drawGround(Canvas canvas){
		float nextXstartPos = 0;
		//Draw baseline for ground
		Path mainPath = new Path();		
		mainPath.moveTo(nextXstartPos, bottomThirdScreen); //Paths starting position
		
		nextXstartPos = PyramidFeature(mainPath, nextXstartPos);
		nextXstartPos = InversePyramidFeature(mainPath, nextXstartPos);
		nextXstartPos = DomeFeature(mainPath, nextXstartPos);
		nextXstartPos = TreeFeature(mainPath, nextXstartPos);		
		nextXstartPos = landingPad(mainPath, nextXstartPos);
		nextXstartPos = SpireFeature(mainPath, nextXstartPos);
		
		//Draws from far right, to bottom right, bottom left and back to start, effectively closing the shape
		mainPath.lineTo(width, bottomThirdScreen);
		mainPath.lineTo(width, height);
		mainPath.lineTo(0, height);
		mainPath.lineTo(0, bottomThirdScreen);
		canvas.drawPath(mainPath, marsBackground);
	}
	
	private void FeatureRandomiser(Path path){
		
	}
	
	private void PsuedoRandomSort(){
		FeatureArray = new int[5];
		
		boolean ChoosingNumbers = true;
		
		while(ChoosingNumbers){			
			for(int i = 0; i < FeatureArray.length; i++){
				FeatureArray[i] = getRandomNumber();
				if(FeatureArray[i] == 0){
					if(FeatureArray[4] == 0){
						ChoosingNumbers = true;
					}
					else {
						ChoosingNumbers = false;
					}
				}
			}
		}		
	}
	
	private int getRandomNumber(){
		int randomInt = random.nextInt(6);		 
		return randomInt;
	}
	

	
	private float DomeFeature(Path path, float xPos){
		float radius = width / 9;
		xPos += radius;
		path.addCircle(xPos, bottomThirdScreen, radius, Direction.CW);
		xPos += radius;
		return xPos;
	}
	
	private float InversePyramidFeature(Path path, float xPos){
		float pyramidSide = height / 3;
		float yPos = bottomThirdScreen;
		path.lineTo(xPos, yPos);
		xPos +=  (pyramidSide/2);
		yPos +=  pyramidSide;
		path.lineTo(xPos, yPos);
		xPos +=  (pyramidSide/2);
		yPos -=  pyramidSide;
		path.lineTo(xPos, yPos);
		return xPos;		
	}
		
	private float PyramidFeature(Path path, float xPos){
		float pyramidSide = height / 3;
		float yPos = bottomThirdScreen;
		path.lineTo(xPos, yPos);
		xPos +=  (pyramidSide/2);
		yPos -=  pyramidSide;
		path.lineTo(xPos, yPos);
		xPos +=  (pyramidSide/2);
		yPos +=  pyramidSide;
		path.lineTo(xPos, yPos);
		return xPos;		
	}
		
	private float TreeFeature(Path path, float xPos){
		float treeBranch = height / 9;
		float yPos = bottomThirdScreen;
		//Left under branch feature feature
			xPos += (treeBranch / 4);
			yPos += (treeBranch / 4);
			path.lineTo(xPos, yPos);
			xPos += (treeBranch / 4);
			yPos -= (treeBranch / 4);
			path.lineTo(xPos, yPos);
			xPos += (treeBranch / 4);
			yPos += (treeBranch / 4);
			path.lineTo(xPos, yPos);
			xPos += (treeBranch / 4);
			yPos -= (treeBranch / 4);
			path.lineTo(xPos, yPos);
		//Left stump
			yPos -=  treeBranch;
			path.lineTo(xPos, yPos);
		//Left branch
			xPos -= treeBranch;
			yPos -= (treeBranch/9);
			path.lineTo(xPos, yPos);
			xPos += treeBranch;
			path.lineTo(xPos, yPos);
		//top branch
			xPos += (treeBranch /9);
			yPos -= treeBranch;
			path.lineTo(xPos, yPos);
			xPos += (treeBranch /9);
			yPos += treeBranch;
			path.lineTo(xPos, yPos);
		//Right branch
			xPos += treeBranch;
			yPos += (treeBranch/9);
			path.lineTo(xPos, yPos);
			xPos -= treeBranch;
			path.lineTo(xPos, yPos);
		//Right stump
			yPos += treeBranch;
			path.lineTo(xPos, yPos);
		//Right under branch feature	
			xPos += (treeBranch / 4);
			yPos -= (treeBranch / 4);
			path.lineTo(xPos, yPos);
			xPos += (treeBranch / 4);
			yPos += (treeBranch / 4);
			path.lineTo(xPos, yPos);
			xPos += (treeBranch / 4);
			yPos -= (treeBranch / 4);
			path.lineTo(xPos, yPos);
			xPos += (treeBranch / 4);
			yPos += (treeBranch / 4);
			path.lineTo(xPos, yPos);			
		return xPos;	
	}

	private float SpireFeature(Path path, float xPos){
			float spireSide = height / 6;
			float yPos = bottomThirdScreen;
/*		//Move one tree branch away from previous feature
			xPos += spireSide;
			path.lineTo(xPos, yPos);*/
		//Left stump
			yPos -=  spireSide;
			path.lineTo(xPos, yPos);
		//top branch
			xPos += (spireSide /9);
			yPos -= spireSide;
			path.lineTo(xPos, yPos);
			xPos += (spireSide /9);
			yPos += spireSide;
			path.lineTo(xPos, yPos);
		//Right stump
			yPos += spireSide;
			path.lineTo(xPos, yPos);
		return xPos;
	}
	
	private float landingPad(Path path, float xPos) {
			float bumpHeight = (height /60);
			float yPos = bottomThirdScreen;	
		//Left Rise
			path.lineTo(xPos, yPos);
			xPos += (width /30);
			yPos -= bumpHeight;		
			path.lineTo(xPos, yPos);
			yPos += bumpHeight;
			path.lineTo(xPos, yPos);
		//Land Site
			xPos += (width /10);
			path.lineTo(xPos, yPos);
		//Right Rise
			yPos -= bumpHeight;
			path.lineTo(xPos, yPos);
			xPos += (width /30);
			yPos += bumpHeight;		
			path.lineTo(xPos, yPos);
		return xPos;
	}
	
}
