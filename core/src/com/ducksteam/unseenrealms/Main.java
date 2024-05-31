package com.ducksteam.unseenrealms;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.ducksteam.unseenrealms.entity.enemies.EnemyEntity;
import com.ducksteam.unseenrealms.map.MapManager;
import com.ducksteam.unseenrealms.entity.RoomInstance;
import com.ducksteam.unseenrealms.player.Player;
import com.ducksteam.unseenrealms.player.PlayerInput;

import java.util.ArrayList;

/**
 * The main class of the game
 * @author thechiefpotatopeeler
 * @author SkySourced
 * */
public class Main extends ApplicationAdapter {
	ModelBatch batch;
	Stage mainMenu;
	Stage threadMenu;
	SpriteBatch batch2d;
	AssetManager assMan;
	public static PerspectiveCamera camera;
	MapManager mapMan;
	Environment environment;
	ArrayList<ModelInstance> modelInstances = new ArrayList<>();
	ArrayList<EnemyEntity> enemies = new ArrayList<>();
	PlayerInput input = new PlayerInput();
	public static Player player;

	Animation<TextureRegion> activeUIAnim;
	float animTime;
	Runnable animPreDraw;
	Runnable animFinished;

	int[] threadAnimState = {0, 0, 0};

	Thread loaderThread = new Thread(this::loadAssets);

	//public boolean loading;

	GameState gameState;

	/**
	 * The enum for managing the game state
	 * */
	public enum GameState{
		MAIN_MENU(0),
		LOADING(1),
		THREAD_SELECT(2),
		IN_GAME(3),
		PAUSED_MENU(4),
		DEAD_MENU(5);

		final int id;
		/**
		 * @param id assigns numeric id to state
		 * */
		GameState(int id){
			this.id=id;
		}

		/**
		 * @return the id of the current state
		 * */
		int getId(){
			return this.id;
		}
	}

	/**
	 * Establishes game at start of runtime
	 * */
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		player = new Player(new Vector3(0,0,0));

		batch2d = new SpriteBatch();
//		neonSkin = new Skin(Gdx.files.internal("skin/neon-ui.json"));


//		TextButton startButton = new TextButton("Start", neonSkin,"default");
//		startButton.setPosition((float) Gdx.graphics.getWidth() /2, (float) Gdx.graphics.getHeight() /2);
//		startButton.setSize((float) Gdx.graphics.getWidth() /5, (float) Gdx.graphics.getHeight() /10);
//		startButton.setColor(0.9F,0.342F,0.52F,1);
//		Image background = new Image(new Texture("loading_background.png"));
//		background.setBounds(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
//		startButton.addListener(new InputListener(){
//			@Override
//			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//			}
//			@Override
//			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//				Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
//
//				gameState = GameState.LOADING;
//				loaderThread.run();
//				Gdx.input.setInputProcessor(input);
//
//				return true;
//			}
//		});
//		mainMenu.addActor(background);
//		mainMenu.addActor(startButton);
		buildMainMenu();
		buildThreadMenu();

		environment = new Environment();
		batch = new ModelBatch();
		camera = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		assMan = new AssetManager();
		mapMan = new MapManager();

		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		camera.near = 0.1f;

		assMan.finishLoading();

		gameState = GameState.MAIN_MENU;
		Gdx.input.setInputProcessor(mainMenu);
    }

	private void buildMainMenu() {
		mainMenu = new Stage();

		Texture transitionMap = new Texture("ui/menu/thread-transition.png");
		TextureRegion[] transitionFrames = TextureRegion.split(transitionMap, 640, 360)[0];
		Animation<TextureRegion> transitionAnimation = new Animation<>(Config.loadingAnimSpeed, transitionFrames);

		Image background = new Image(new Texture("ui/menu/background.png"));
		background.setBounds(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		mainMenu.addActor(background);

		ImageButton.ImageButtonStyle playButtonStyle = new ImageButton.ImageButtonStyle();
		playButtonStyle.up = new Image(new Texture("ui/menu/play1.png")).getDrawable();
		playButtonStyle.down = new Image(new Texture("ui/menu/play2.png")).getDrawable();
		playButtonStyle.over = new Image(new Texture("ui/menu/play2.png")).getDrawable();

		ImageButton playButton = new ImageButton(playButtonStyle);
		playButton.setPosition((float) Gdx.graphics.getWidth() * 36/640, (float) Gdx.graphics.getHeight() * 228/360);
		playButton.setSize((float) Gdx.graphics.getWidth() * 129/640, (float) Gdx.graphics.getHeight() * 30/360);
		playButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				activeUIAnim = transitionAnimation;
				animTime = 0;
				animPreDraw = () -> renderMainMenuFrame();
				animFinished = () -> gameState = GameState.THREAD_SELECT;
				return true;
			}
		});
		mainMenu.addActor(playButton);

		ImageButton.ImageButtonStyle instructionsButtonStyle = new ImageButton.ImageButtonStyle();
		instructionsButtonStyle.up = new Image(new Texture("ui/menu/instructions1.png")).getDrawable();
		instructionsButtonStyle.down = new Image(new Texture("ui/menu/instructions2.png")).getDrawable();
		instructionsButtonStyle.over = new Image(new Texture("ui/menu/instructions2.png")).getDrawable();

		ImageButton instructionsButton = new ImageButton(instructionsButtonStyle);
		instructionsButton.setPosition((float) Gdx.graphics.getWidth() * 36/640, (float) Gdx.graphics.getHeight() * 193/360);
		instructionsButton.setSize((float) Gdx.graphics.getWidth() * 129/640, (float) Gdx.graphics.getHeight() * 30/360);
		instructionsButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
		mainMenu.addActor(instructionsButton);

		ImageButton.ImageButtonStyle optionsButtonStyle = new ImageButton.ImageButtonStyle();
		optionsButtonStyle.up = new Image(new Texture("ui/menu/options1.png")).getDrawable();
		optionsButtonStyle.down = new Image(new Texture("ui/menu/options2.png")).getDrawable();
		optionsButtonStyle.over = new Image(new Texture("ui/menu/options2.png")).getDrawable();

		ImageButton optionsButton = new ImageButton(optionsButtonStyle);
		optionsButton.setPosition((float) Gdx.graphics.getWidth() * 36/640, (float) Gdx.graphics.getHeight() * 158/360);
		optionsButton.setSize((float) Gdx.graphics.getWidth() * 129/640, (float) Gdx.graphics.getHeight() * 30/360);
		optionsButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
		mainMenu.addActor(optionsButton);

		ImageButton.ImageButtonStyle quitButtonStyle = new ImageButton.ImageButtonStyle();
		quitButtonStyle.up = new Image(new Texture("ui/menu/quit1.png")).getDrawable();
		quitButtonStyle.down = new Image(new Texture("ui/menu/quit2.png")).getDrawable();
		quitButtonStyle.over = new Image(new Texture("ui/menu/quit2.png")).getDrawable();

		ImageButton quitButton = new ImageButton(quitButtonStyle);
		quitButton.setPosition((float) Gdx.graphics.getWidth() * 36/640, (float) Gdx.graphics.getHeight() * 80/360);
		quitButton.setSize((float) Gdx.graphics.getWidth() * 129/640, (float) Gdx.graphics.getHeight() * 30/360);
		quitButton.addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.exit(0);
				return true;
			}
		});
		mainMenu.addActor(quitButton);
	}

	private void buildThreadMenu(){
		threadMenu = new Stage();

		Image background = new Image(new Texture("ui/thread/background.png"));
		background.setBounds(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		threadMenu.addActor(background);

		ImageButton soulButton = new ImageButton(new Image(new Texture("ui/thread/soul"+(threadAnimState[0]+1)+".png")).getDrawable());
		ImageButton coalButton = new ImageButton(new Image(new Texture("ui/thread/coal"+(threadAnimState[1]+1)+".png")).getDrawable());
		ImageButton joltButton = new ImageButton(new Image(new Texture("ui/thread/jolt"+(threadAnimState[2]+1)+".png")).getDrawable());

		ImageButton tRodButton = new ImageButton(new Image(new Texture("ui/thread/threadedrod.png")).getDrawable());
		tRodButton.setPosition((float) Gdx.graphics.getWidth() * 220/640, (float) Gdx.graphics.getHeight() * 57/360);
		tRodButton.setSize((float) Gdx.graphics.getWidth() * 198/640, (float) Gdx.graphics.getHeight() * 30/360);

		Gdx.app.debug("threadAnimState", threadAnimState[0] + " " + threadAnimState[1] + " " + threadAnimState[2]);
		Gdx.app.debug("Gdx.graphics dimensions", Gdx.graphics.getWidth() + " " + Gdx.graphics.getHeight());

		if (threadAnimState[0] > 0) { // soul anim
			threadAnimState[1] = 0;
			threadAnimState[2] = 0;

			soulButton.setPosition((float) Gdx.graphics.getWidth() * 193/640, (float) Gdx.graphics.getHeight() * 100/360);
			soulButton.setSize((float) Gdx.graphics.getWidth() * (65 + threadAnimState[0] * 10)/640, (float) Gdx.graphics.getHeight() * 173/360);

			coalButton.setPosition((float) Gdx.graphics.getWidth() * (288 + threadAnimState[0] * 10)/640, (float) Gdx.graphics.getHeight() * 100/360);
			coalButton.setSize((float) Gdx.graphics.getWidth() * 65/640, (float) Gdx.graphics.getHeight() * 173/360);

			joltButton.setPosition((float) Gdx.graphics.getWidth() * (383 + threadAnimState[0] * 10)/640, (float) Gdx.graphics.getHeight() * 100/360);
			joltButton.setSize((float) Gdx.graphics.getWidth() * 65/640, (float) Gdx.graphics.getHeight() * 173/360);
		} else if (threadAnimState[1] > 0) { // coal anim
			threadAnimState[0] = 0;
			threadAnimState[2] = 0;

			soulButton.setPosition((float) Gdx.graphics.getWidth() * (193 - threadAnimState[1] * 5)/640, (float) Gdx.graphics.getHeight() * 100/360);
			soulButton.setSize((float) Gdx.graphics.getWidth() * 65/640, (float) Gdx.graphics.getHeight() * 173/360);

			coalButton.setPosition((float) Gdx.graphics.getWidth() * (288 - threadAnimState[1] * 5)/640, (float) Gdx.graphics.getHeight() * 100/360);
			coalButton.setSize((float) Gdx.graphics.getWidth() * (65 + threadAnimState[1] * 10)/640, (float) Gdx.graphics.getHeight() * 173/360);

			joltButton.setPosition((float) Gdx.graphics.getWidth() * (383 + threadAnimState[1] * 5)/640, (float) Gdx.graphics.getHeight() * 100/360);
			joltButton.setSize((float) Gdx.graphics.getWidth() * 65/640, (float) Gdx.graphics.getHeight() * 173/360);
		} else if (threadAnimState[2] > 0) { // jolt anim
			threadAnimState[0] = 0;
			threadAnimState[1] = 0;

			soulButton.setPosition((float) Gdx.graphics.getWidth() * (193 - threadAnimState[2] * 10)/640, (float) Gdx.graphics.getHeight() * 100/360);
			soulButton.setSize((float) Gdx.graphics.getWidth() * 65/640, (float) Gdx.graphics.getHeight() * 173/360);

			coalButton.setPosition((float) Gdx.graphics.getWidth() * (288 - threadAnimState[2] * 10)/640, (float) Gdx.graphics.getHeight() * 100/360);
			coalButton.setSize((float) Gdx.graphics.getWidth() * 65/640, (float) Gdx.graphics.getHeight() * 173/360);

			joltButton.setPosition((float) Gdx.graphics.getWidth() * (383 - threadAnimState[2] * 10)/640, (float) Gdx.graphics.getHeight() * 100/360);
			joltButton.setSize((float) Gdx.graphics.getWidth() * (65 + threadAnimState[2] * 10)/640, (float) Gdx.graphics.getHeight() * 173/360);
		} else { // no anim
			soulButton.setPosition((float) Gdx.graphics.getWidth() * 193/640, (float) Gdx.graphics.getHeight() * 100/360);
			soulButton.setSize((float) Gdx.graphics.getWidth() * 65/640, (float) Gdx.graphics.getHeight() * 173/360);

			Gdx.app.debug("soulButton", soulButton.getWidth() + " " + soulButton.getHeight());

			coalButton.setPosition((float) Gdx.graphics.getWidth() * 288/640, (float) Gdx.graphics.getHeight() * 100/360);
			coalButton.setSize((float) Gdx.graphics.getWidth() * 65/640, (float) Gdx.graphics.getHeight() * 173/360);

			joltButton.setPosition((float) Gdx.graphics.getWidth() * 383/640, (float) Gdx.graphics.getHeight() * 100/360);
			joltButton.setSize((float) Gdx.graphics.getWidth() * 65/640, (float) Gdx.graphics.getHeight() * 173/360);
		}

		threadMenu.addActor(soulButton);
		threadMenu.addActor(coalButton);
		threadMenu.addActor(joltButton);
		threadMenu.addActor(tRodButton);
	}

	/**
	 * Method for loader thread to load assets
	 * */
	private void loadAssets(){
			enemies.forEach((EnemyEntity enemy) -> {
				assMan.load(enemy.getModelAddress(), Model.class);
				assMan.finishLoadingAsset(enemy.getModelAddress());
				//((Model) assMan.get(enemy.getModelAddress())).materials.clear();
				//((Model) assMan.get(enemy.getModelAddress())).materials.addAll(new Material(TextureAttribute.createDiffuse(new Texture(new String("/debug.jpg")))));
				modelInstances.add(new ModelInstance((Model) assMan.get(enemy.getModelAddress())));
			});
			mapMan.getCurrentLevel().getRooms().forEach((RoomInstance room) -> {
				if (room.getModelAddress() == null) return;
				assMan.load(room.getModelAddress(), Model.class);
				assMan.finishLoadingAsset(room.getModelAddress());
				modelInstances.add(new ModelInstance((Model) assMan.get(room.getModelAddress())));
			});
			Gdx.app.debug("Loader thread", "Loading finished");

	}
	/**
	 * Renders the loading screen while the assets are loading
	 * */
	private void renderLoadingFrame(){
		batch2d.begin();
		batch2d.draw(new Texture("loading_background.png"),0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		batch2d.draw(new Texture("logo_temp.png"), (float) Gdx.graphics.getWidth()/4, (float) Gdx.graphics.getHeight()/4, (float) Gdx.graphics.getWidth() /2, (float) Gdx.graphics.getHeight() /2);
		batch2d.end();
		batch2d.dispose();
	}

	/**
	 * Renders the main menu
	 * */
	private void renderMainMenuFrame(){
		mainMenu.act();
		mainMenu.draw();
	}

	/**
	 *
	 * */
	@Override
	public void render () {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if(activeUIAnim != null){
			if (animPreDraw != null) animPreDraw.run();
			animTime += Gdx.graphics.getDeltaTime();
			Gdx.app.debug("animTime", animTime + "");
			TextureRegion currentFrame = activeUIAnim.getKeyFrame(animTime);
			batch2d.begin();
			batch2d.draw(currentFrame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			batch2d.end();
			if(activeUIAnim.isAnimationFinished(animTime)){
				activeUIAnim = null;
				animTime = 0;
				if (animFinished != null) animFinished.run();
			}
			return;
		}

		if(gameState == GameState.LOADING){
			gameState = (loaderThread.isAlive())? GameState.LOADING:GameState.IN_GAME;
			renderLoadingFrame();
			return;
		}

		if(gameState == GameState.MAIN_MENU) {
			renderMainMenuFrame();
			return;
		}

		if (gameState == GameState.THREAD_SELECT){
			threadMenu.act();
			threadMenu.draw();
			return;
		}

		//if (!player.getVel().equals(Vector3.Zero)) Gdx.app.debug("vel", player.getVel() + " vel | pos " + player.getPos());
		input.update();

		player.setPos(player.getPos().add(player.getVel().scl(Gdx.graphics.getDeltaTime())));

		camera.position.set(player.getPos()).add(0,0,5);

		camera.direction.set(player.getRot());
		//camera.lookAt(0f, 0f, 0f);
		batch.begin(camera);
		batch.render(modelInstances,environment);

		if (Config.doRenderColliders) {
			for (RoomInstance o : mapMan.getCurrentLevel().getRooms()) {
				if (o.collider != null) {
					batch.render(o.collider.getRenderable(), environment);
				}
			}
		}

		batch.end();

		camera.update();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
