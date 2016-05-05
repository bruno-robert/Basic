package com.maze.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

/*
 * April 2016
 * M4ZE is a game created by Bruno ROBERT, student at Concordia University
 * It is based on tutorials:
 * https://www.youtube.com/watch?v=1gir2R7G9ws&list=PLWms45O3n--6TvZmtFHaCWRZwEqnz2MHa
 * 
 * It's just a basic maze game.
 */

public class Maze extends Canvas implements Runnable {

	// variables
	private static final long serialVersionUID = -7208683769814496626L;
	public static final int WIDTH = 640, HEIGHT = WIDTH / 12 * 9;
	public static final int WIDTH_B = 20, HEIGHT_B = 14;
	private boolean running = false;
	private Thread thread; // I don't really understand this one yet
	Graphics g;

	// XMLReader stuff
	int width = 20, height = 14;
	int[][] tileMap = new int[width][height];
	XMLReader mapReader = new XMLReader("/M4ZE.XML", width, height);
	BufferedImage img = null;

	// objects
	private Handler handler; // creation of a handler object


	// main method that just runs Maze()
	public static void main(String args[]) {
		new Maze();
	}

	// default constructor for maze
	public Maze() {

		// initialise objects
		handler = new Handler(); // initialises the Handler object

		// Mouse and Keyboard input
		this.addKeyListener(new KeyInput(this, handler));

		// Call the window class and runs run()
		new Window(WIDTH, HEIGHT, "M4ze", this);

		try {
			img = ImageIO.read(new File("res/tiles.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Start and stop the application
	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}

	public synchronized void stop() {
		try {
			thread.join();
			running = false;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// This is the game loop, the heart of the program
	// It basically loops and executes Maze.tick() and Maze.render() 60 per
	// second
	public void run() {
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				tick();
				delta--;
			}
			if (running)
				render();
			frames++;

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames);
				frames = 0;
			}
			try {
				TimeUnit.MILLISECONDS.sleep(14);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		stop();
	}

	// tick, asks Handler to update all objects
	private void tick() {
		handler.tick();

	}

	// Basically, asks Handler to print all objects in their current state to
	// the window
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		handler.render(g);
		g.dispose();
		bs.show();
	}

	// Clamp() takes a value and two extremes and clamps it between the two
	// extremes
	public static int clamp(int var, int min, int max) {
		if (var >= max)
			return var = max;
		else if (var <= min)
			return var = min;
		else
			return var;
	}

	public static void paint(Graphics g, BufferedImage img, int x, int y, int tile) {
		g.drawImage(img, x, y, (x + 32), (y + 32), (tile % 20), (tile - (tile % 20) * 20), (tile % 20) + 32,
				(tile - (tile % 20) * 20) + 32, null);
	}

}
