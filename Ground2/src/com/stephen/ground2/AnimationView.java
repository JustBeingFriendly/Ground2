package com.stephen.ground2;







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
import android.graphics.Path.FillType;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AnimationView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

	private Thread animation = null;
	private boolean running;
	float height;
	float width;
	//Point centre;
	private Bitmap mars;
	private BitmapShader marsShade;
	Paint paint = new Paint();
	Paint marsBackground = new Paint();
	
	float bottomThirdScreen;

	float x[] = { 0, 200, 190, 218, 260, 275, 298, 309, 327, 336, 368, 382,
			448, 462, 476, 498, 527, 1200, 1200, 0, 0 };
	float y[] = { 616, 540, 550, 605, 605, 594, 530, 520, 520, 527, 626, 636,
			636, 623, 535, 504, 481, 481, 750, 750, 616 };

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
		nextXstartPos = HillFeature(mainPath, nextXstartPos);
		
		//Draws from far right, to bottom right, bottom left and back to start, effectively closing the shape
		mainPath.lineTo(width, bottomThirdScreen);
		mainPath.lineTo(width, height);
		mainPath.lineTo(0, height);
		mainPath.lineTo(0, bottomThirdScreen);
		//paint.setShader(marsShade);
		canvas.drawPath(mainPath, marsBackground);
	}
	
	private float HillFeature(Path path, float xPos){
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
	
	
	private float PitFeature(Path path, float xPos){
		float radius = width / 9;
		xPos += radius;
		path.addCircle(xPos, bottomThirdScreen, radius, Direction.CW);
		xPos *= 2;
		return xPos;
	}

}
