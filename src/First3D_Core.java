import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;


public class First3D_Core implements ApplicationListener, InputProcessor
{
	GL11 gl11;
	//Camera cam;
	private boolean ligthBulbState = true;
	private boolean wiggleLights = false;
	private float wiggleValue = 0f;
	private float count = 0;

	LightCycle cycle1;
	LightCycle cycle2;
	static final int WORLDSIZE = 40;               // 20x20 grid
	static final byte SPEED = 5;
	private byte state = 1;

//	static final int FORWARD = Input.Keys.W;
//	static final int BACK = Input.Keys.S;
	static final int RIGHT1 = Input.Keys.D;
	static final int LEFT1 = Input.Keys.A;
	static final int RIGHT2 = Input.Keys.RIGHT;
	static final int LEFT2 = Input.Keys.LEFT;

	static final byte NORTH = 0;
	static final byte EAST = 1;
	static final byte SOUTH = 2;
	static final byte WEST = 3;

	static final byte PAUSE = 0;
	static final byte RUNNING = 1;

	ArrayList<Trail> trails = new ArrayList<Trail>();
	int trailCount = -1;

	Music music;

	@Override
	public void create()
	{
		Gdx.input.setInputProcessor(this);
		
		Gdx.gl11.glEnable(GL11.GL_LIGHTING);
		
		Gdx.gl11.glEnable(GL11.GL_LIGHT1);
		Gdx.gl11.glEnable(GL11.GL_DEPTH_TEST);
		
		Gdx.gl11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
		Gdx.gl11.glLoadIdentity();
		Gdx.glu.gluPerspective(Gdx.gl11, 90, 1.333333f, 1.0f, 30.0f);

		Gdx.gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

		FloatBuffer vertexBuffer = BufferUtils.newFloatBuffer(72);
		vertexBuffer.put(new float[] {-0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f,
									  0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f,
									  0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f,
									  0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
									  0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
									  -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f,
									  -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f,
									  -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f,
									  -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f,
									  0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f,
									  -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f,
									  0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f});
		vertexBuffer.rewind();

		Gdx.gl11.glVertexPointer(3, GL11.GL_FLOAT, 0, vertexBuffer);
		//cam = new Camera(new Point3D(0, 3.5f, 0), new Point3D(19.0f, 0.0f, 1.0f), new Vector3D(0.0f, 1.0f, 0.0f));

		gl11 = Gdx.gl11;
		cycle1 = new LightCycle(1.0f, 2.0f, 1.0f, NORTH);
		cycle1.startNorth = true;
		cycle2 = new LightCycle(18.0f, 2.0f, 18.0f, SOUTH);

		music = Gdx.audio.newMusic(Gdx.files.internal("assets/music/Arena.mp3"));
		music.play();
	}

	@Override
	public void dispose()
	{
		music.dispose();
		System.out.println("DISPOSE");
		// TODO Auto-generated method stub
	}

	@Override
	public void pause()
	{
		// TODO Auto-generated method stub
	}

	private void updatePause()
	{
		//TODO: draw pause screen
	}

	private void updateRunning()
	{
		if(this.wiggleLights)
		{
			count += 0.03;
			this.wiggleValue = (float) Math.sin(count) * 10;
		}

		if(this.ligthBulbState) Gdx.gl11.glEnable(GL11.GL_LIGHT0);
		else Gdx.gl11.glDisable(GL11.GL_LIGHT0);

		float deltaTime = Gdx.graphics.getDeltaTime();

		switch(cycle1.direction)                            // check what direction cycle 1 is facing
		{
			case NORTH:
				if(cycle1.startNorth)
				{
					trails.add(new Trail(cycle1.pos.x, cycle1.pos.y, cycle1.pos.z, NORTH));
					trailCount++;
					trails.get(trailCount).start = trails.get(trailCount).x;
					cycle1.startNorth = false;
                    if(trailCount > 0)
                    {
                        if(trails.get(trailCount-1).direction == EAST)
                        {
                            System.out.println(trails.get(trailCount-1).end);
                            trails.get(trailCount-1).end = trails.get(trailCount-1).end + 0.381f;
                        }

                    }
				}
				else
				{
					updateTrail(trails.get(trailCount), cycle1);
				}
				cycle1.pos.x += deltaTime*SPEED;            // move forwards on the x-axis
				break;
			case EAST:
				if(cycle1.startEast)
				{
					trails.add(new Trail(cycle1.pos.x, cycle1.pos.y, cycle1.pos.z - 0.8f, EAST));
					trailCount++;
					trails.get(trailCount).start = cycle1.pos.z;
					cycle1.startEast = false;
                    System.out.println(trailCount);
                    if(trailCount > 0)
                    {
                        if(trails.get(trailCount-1).direction == NORTH)
                        {
                            System.out.println(trails.get(trailCount-1).end);
                            trails.get(trailCount-1).end = trails.get(trailCount-1).end + 0.381f;
                        }

                    }
				}
				else
				{
					updateTrail(trails.get(trailCount), cycle1);
				}
				cycle1.pos.z += deltaTime*SPEED;            // move forwards on the z-axis
				break;
			case SOUTH:
				if(cycle1.startSouth)
				{
					trails.add(new Trail(cycle1.pos.x, cycle1.pos.y, cycle1.pos.z, SOUTH));
					trailCount++;
					trails.get(trailCount).start = cycle1.pos.x;
					cycle1.startSouth = false;
				}
				else
				{
					updateTrail(trails.get(trailCount), cycle1);
				}
				cycle1.pos.x -= deltaTime*SPEED;            // move backwards on the x-axis
				break;
			case WEST:
				if(cycle1.startWest)
				{
					trails.add(new Trail(cycle1.pos.x, cycle1.pos.y, cycle1.pos.z, WEST));
					trailCount++;
					trails.get(trailCount).start = cycle1.pos.z;
					cycle1.startWest = false;
				}
				else
				{
					updateTrail(trails.get(trailCount), cycle1);
				}
				cycle1.pos.z -= deltaTime*SPEED;            // move backwards on the z-axis
				break;
		}
		switch(cycle2.direction)                            // check what direction cycle 2 is facing
		{
			case NORTH:
				cycle2.pos.x += deltaTime*SPEED;            // move forwards on the x-axis
				break;
			case EAST:
				cycle2.pos.z += deltaTime*SPEED;            // move forwards on the z-axis
				break;
			case SOUTH:
				cycle2.pos.x -= deltaTime*SPEED;            // move backwards on the x-axis
				break;
			case WEST:
				cycle2.pos.z -= deltaTime*SPEED;            // move backwards on the z-axis
				break;
		}
	}

	private void update()
	{
		switch(state)
		{
			case RUNNING:
				updateRunning();
				break;
			case PAUSE:
				updatePause();
				break;
			default:
				break;
		}
	}
	
	private void drawBox()
	{
		Gdx.gl11.glNormal3f(0.0f, 0.0f, -1.0f);
		Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		Gdx.gl11.glNormal3f(1.0f, 0.0f, 0.0f);
		Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 4, 4);
		Gdx.gl11.glNormal3f(0.0f, 0.0f, 1.0f);
		Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 8, 4);
		Gdx.gl11.glNormal3f(-1.0f, 0.0f, 0.0f);
		Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 12, 4);
		Gdx.gl11.glNormal3f(0.0f, 1.0f, 0.0f);
		Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 16, 4);
		Gdx.gl11.glNormal3f(0.0f, -1.0f, 0.0f);
		Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 20, 4);
	}
	
	private void drawFloor()
	{
		for(float fx = 0.0f; fx < WORLDSIZE; fx += 1.0)
		{
			for(float fz = 0.0f; fz < WORLDSIZE; fz += 1.0)
			{
				Gdx.gl11.glPushMatrix();
				Gdx.gl11.glTranslatef(fx, 1.0f, fz);
				Gdx.gl11.glScalef(0.97f, 1.0f, 0.97f);
				drawBox();
				Gdx.gl11.glPopMatrix();
			}
		}
	}
	
	private void display()
	{
		Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);

	//Lights
		// Configure light 0
		float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
		Gdx.gl11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, lightDiffuse, 0);

		float[] lightPosition = {this.wiggleValue, 10.0f, 15.0f, 1.0f};
		Gdx.gl11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPosition, 0);

		// Configure light 1
		float[] lightDiffuse1 = {0.5f, 0.5f, 0.5f, 1.0f};
		Gdx.gl11.glLightfv(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, lightDiffuse1, 0);

		float[] lightPosition1 = {-5.0f, -10.0f, -15.0f, 1.0f};
		Gdx.gl11.glLightfv(GL11.GL_LIGHT1, GL11.GL_POSITION, lightPosition1, 0);

		// Set material on the cube.
		float[] materialDiffuse = {0.2f, .3f, 0.6f, 1.0f};
		Gdx.gl11.glMaterialfv(GL11.GL_FRONT, GL11.GL_DIFFUSE, materialDiffuse, 0);

	//Draw scene 1
//		Gdx.gl11.glViewport(0, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight));                                // Vertical split
		Gdx.gl11.glViewport(0, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);    // Horizontal split
		Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);
		Gdx.gl11.glLoadIdentity();
		switch (cycle1.direction)
		{
			case NORTH:
				Gdx.glu.gluLookAt(Gdx.gl11, cycle1.pos.x-2.5f, 3.0f, cycle1.pos.z, cycle1.pos.x+2, 0.0f, cycle1.pos.z, 0.0f, 1.0f, 0.0f);
				break;
			case EAST:
				Gdx.glu.gluLookAt(Gdx.gl11, cycle1.pos.x, 3.0f, cycle1.pos.z-2.5f, cycle1.pos.x, 0.0f, cycle1.pos.z+2, 0.0f, 1.0f, 0.0f);
				break;
			case SOUTH:
				Gdx.glu.gluLookAt(Gdx.gl11, cycle1.pos.x+2.5f, 3.0f, cycle1.pos.z, cycle1.pos.x-2, 0.0f, cycle1.pos.z, 0.0f, 1.0f, 0.0f);
				break;
			case WEST:
				Gdx.glu.gluLookAt(Gdx.gl11, cycle1.pos.x, 3.0f, cycle1.pos.z+2.5f, cycle1.pos.x, 0.0f, cycle1.pos.z-2, 0.0f, 1.0f, 0.0f);
				break;
			default:
				Gdx.glu.gluLookAt(Gdx.gl11, cycle1.pos.x, 3.0f, cycle1.pos.z-2.5f, cycle1.pos.x, 0.0f, cycle1.pos.z, 0.0f, 1.0f, 0.0f);
				break;
		}
	// Draw floor!
		drawFloor();
		cycle1.draw();
		cycle2.draw();
		drawTrail();
//		scaleTrail(trail1.x, trail1.y, trail1.z, cycle1.pos.x, cycle1.pos.y, cycle1.pos.z);


	//Draw scene 2
//		Gdx.gl11.glViewport(Gdx.graphics.getWidth() / 2, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight()); // Vertical
		Gdx.gl11.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);                           // Horizontal
		Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);
		Gdx.gl11.glLoadIdentity();
		switch (cycle2.direction)
		{
			case NORTH:
				Gdx.glu.gluLookAt(Gdx.gl11, cycle2.pos.x-2.5f, 3.0f, cycle2.pos.z, cycle2.pos.x+2, 0.0f, cycle2.pos.z, 0.0f, 1.0f, 0.0f);
				break;
			case EAST:
				Gdx.glu.gluLookAt(Gdx.gl11, cycle2.pos.x, 3.0f, cycle2.pos.z-2.5f, cycle2.pos.x, 0.0f, cycle2.pos.z+2, 0.0f, 1.0f, 0.0f);
				break;
			case SOUTH:
				Gdx.glu.gluLookAt(Gdx.gl11, cycle2.pos.x+2.5f, 3.0f, cycle2.pos.z, cycle2.pos.x-2, 0.0f, cycle2.pos.z, 0.0f, 1.0f, 0.0f);
				break;
			case WEST:
				Gdx.glu.gluLookAt(Gdx.gl11, cycle2.pos.x, 3.0f, cycle2.pos.z+2.5f, cycle2.pos.x, 0.0f, cycle2.pos.z-2, 0.0f, 1.0f, 0.0f);
				break;
			default:
				Gdx.glu.gluLookAt(Gdx.gl11, cycle2.pos.x, 3.0f, cycle2.pos.z-2.5f, cycle2.pos.x, 0.0f, cycle2.pos.z, 0.0f, 1.0f, 0.0f);
				break;
		}

		drawFloor();
		cycle1.draw();
		cycle2.draw();
		drawTrail();
//		scaleTrail(trail1.x, trail1.y, trail1.z, cycle1.pos.x, cycle1.pos.y, cycle1.pos.z);
	}

	private void updateTrail(Trail trail, LightCycle cycle)
	{
		switch (trail.direction)
		{
			case NORTH:
				trail.end = cycle.pos.x;
				break;

			case EAST:
				trail.end = cycle.pos.z;
				break;

			case SOUTH:
				trail.end = cycle.pos.x;
				break;

			case WEST:
				trail.end = cycle.pos.z;
		}
	}

	private void drawTrail()
	{
		for(Trail trail : trails)
		{
			switch (trail.direction)
			{
				case NORTH:
					Gdx.gl11.glPushMatrix();
					Gdx.gl11.glTranslatef(((trail.start+trail.end)/2), 2.0f, trail.z);
					Gdx.gl11.glScalef(trail.end-trail.start, 1.0f, 0.1f);
					//Gdx.gl11.glRotatef(angle, 0.0f, 1.0f, 0.0f);
					drawBox();
					Gdx.gl11.glPopMatrix();
					break;

				case EAST:
					Gdx.gl11.glPushMatrix();
					Gdx.gl11.glTranslatef(trail.x, 2.0f, ((trail.start+trail.end)/2));
					Gdx.gl11.glScalef(0.1f, 1.0f, trail.end-trail.start);
					//Gdx.gl11.glRotatef(angle, 0.0f, 1.0f, 0.0f);
					drawBox();
					Gdx.gl11.glPopMatrix();
					break;

				case SOUTH:
					Gdx.gl11.glPushMatrix();
					Gdx.gl11.glTranslatef(((trail.start+trail.end)/2), 2.0f, trail.z);
					Gdx.gl11.glScalef(trail.end-trail.start, 1.0f, 0.1f);
					//Gdx.gl11.glRotatef(angle, 0.0f, 1.0f, 0.0f);
					drawBox();
					Gdx.gl11.glPopMatrix();
					break;

				case WEST:
					Gdx.gl11.glPushMatrix();
					Gdx.gl11.glTranslatef(trail.x, 2.0f, ((trail.start+trail.end)/2));
					Gdx.gl11.glScalef(0.1f, 1.0f, trail.end-trail.start);
					//Gdx.gl11.glRotatef(angle, 0.0f, 1.0f, 0.0f);
					drawBox();
					Gdx.gl11.glPopMatrix();
					break;
			}
		}
	}

	@Override
	public void render()
	{
		update();
		display();
	}

	@Override
	public void resize(int arg0, int arg1)
	{}

	@Override
	public void resume()
	{}

	@Override
	public boolean keyDown(int arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int arg0)
	{
		switch (arg0)                       // Check what key was released
		{
			case RIGHT1:                    // Player 1 right turn
				cycle1.movePlayer(RIGHT1);
				break;
			case LEFT1:                     // Player 1 left turn
				cycle1.movePlayer(LEFT1);
				break;
			case RIGHT2:                    // Player 2 right turn
				cycle2.movePlayer(RIGHT2);
				break;
			case LEFT2:                     // Player 2 left turn
				cycle2.movePlayer(LEFT2);
				break;
			case Input.Keys.P:              // Pause game
				if(state == PAUSE) state = RUNNING;
				else if(state == RUNNING) state = PAUSE;
				break;
			case Input.Keys.L:
				this.ligthBulbState = this.ligthBulbState ? false:true;
				break;
			case Input.Keys.O:
				this.wiggleLights = this.wiggleLights ? false:true;
				break;
			default:
				break;
		}
		/*if(arg0 == Input.Keys.L){
			this.ligthBulbState = this.ligthBulbState ? false:true;
		}
		if(arg0 == Input.Keys.O){
			this.wiggleLights = this.wiggleLights ? false:true;
		}*/
		return false;
	}

	@Override
	public boolean mouseMoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}
}
