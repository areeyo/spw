package f2.spw;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Timer;



public class GameEngine implements KeyListener, GameReporter{
	GamePanel gp;
		
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private ArrayList<Enemy2> enemies2 = new ArrayList<Enemy2>();
	private ArrayList<Bullet> bullet = new ArrayList<Bullet>();
	private ArrayList<Item> item = new ArrayList<Item>();
	private SpaceShip v;	
	
	private Timer timer;
	
	private long score = 0;
	//private long hp = 5;
	private int time = 0;
	private double difficulty = 0.1;
	private int b = 380;
	private int e = 0;
	private int hearth = 0;
	
	
	public GameEngine(GamePanel gp, SpaceShip v) {
		this.gp = gp;
		this.v = v;		
		
		gp.sprites.add(v);
		
		timer = new Timer(50, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				process();
				process2();
				process3();
				process4();				
			}
		});
		timer.setRepeats(true);
		
	}
	
	public void start(){
		timer.start();
	}
	
	private void generateEnemy(){
		Enemy e = new Enemy((int)(Math.random()*390), 30);
		gp.sprites.add(e);
		enemies.add(e);
	}
	
	private void process(){
		if(Math.random() < difficulty){
			generateEnemy();
		}
		
		if(time>0){
			
			time--;
		}
		
		
		Iterator<Enemy> e_iter = enemies.iterator();
		while(e_iter.hasNext()){
			Enemy e = e_iter.next();
			e.proceed();
			
			if(!e.isAlive()){
				e_iter.remove();
				gp.sprites.remove(e);
				score += 1;
			}
		}
		
		gp.updateGameUI(this);
		
		Rectangle2D.Double vr = v.getRectangle();
		Rectangle2D.Double er;
		for(Enemy e : enemies){
			er = e.getRectangle();
			if(er.intersects(vr)){
				if(time == 0){
					//hp -= 1;
					gp.big.setBackground(Color.RED);

					Timer timer2 = new Timer(200, new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent arg0) {
							gp.big.setBackground(Color.BLACK);
						}
					});
					timer2.setRepeats(false);
			        timer2.start();
					b -= 380/5;
					time = 5;
					if(b <= 75){
						die();
					}
					return;
				}
				
			}
		}
		gp.bloodSpaceShip(b);
	}
	
	private void generateEnemy2(){
		Enemy2 e = new Enemy2((int)(Math.random()*390), 2);
		gp.sprites.add(e);
		enemies2.add(e);
	}
	
	private void process2(){
		if(Math.random() < difficulty/20){
			generateEnemy2();
		}
		
		Iterator<Enemy2> e_iter = enemies2.iterator();
		while(e_iter.hasNext()){
			Enemy2 e = e_iter.next();
			e.proceed();
			
			if(!e.isAlive()){
				e_iter.remove();
				gp.sprites.remove(e);
				score += 5;
			}
			
		}
		
		gp.updateGameUI(this);
		
		Rectangle2D.Double vr = v.getRectangle();
		Rectangle2D.Double er;
		for(Enemy2 e : enemies2){
			er = e.getRectangle();
			if(er.intersects(vr)){
				die();
				return;
			}
			
			Rectangle2D.Double br;
			for(Bullet bu : bullet){   
				br = bu.getRectangle();
				if(br.intersects(er)){
				    e.getHit();
					bu.getHit();
					return;
					}
				}
		}
		gp.bloodSpaceShip(b);
	}
	
	
	
	private void generateBullet(){
		Bullet b = new Bullet((v.x) + (v.width/2), v.y);
		gp.sprites.add(b);
		bullet.add(b);
	}
	
	private void process3(){
		Iterator<Bullet> e_iter = bullet.iterator();
		while(e_iter.hasNext()){
			Bullet b = e_iter.next();
			b.proceed();
			
			if(!b.isAlive()){
				e_iter.remove();
				gp.sprites.remove(b);
				
			}
		}
		
		gp.updateGameUI(this);
		
		Rectangle2D.Double vr = v.getRectangle();
		Rectangle2D.Double er;
		for(Bullet b : bullet){
			er = b.getRectangle();
			if(er.intersects(vr)){
				die();
				return;
			}
		}
		gp.bloodSpaceShip(b);
	}
	
	private void generateItem(){
		Item e = new Item((int)(Math.random()*390), 2);
		gp.sprites.add(e);
		item.add(e);
	}
	
	private void process4(){
		if(Math.random() < difficulty/10){
			generateItem();
		}
		
		if(hearth>0)
			hearth--;
		
		Iterator<Item> e_iter = item.iterator();
		while(e_iter.hasNext()){
			Item e = e_iter.next();
			e.proceed();
			
			if(!e.isAlive()){
				e_iter.remove();
				gp.sprites.remove(e);
				
			}
			
		}
		
		gp.updateGameUI(this);
		
		Rectangle2D.Double vr = v.getRectangle();
		Rectangle2D.Double er;
		for(Item e : item){
			er = e.getRectangle();
			if(er.intersects(vr)){
				if(hearth == 0){
					b = 380;
					hearth = 10;
					return;
				}
				
			}
		}
		gp.bloodSpaceShip(b);
	}
	
	public void die(){
		e=1;
		gp.bloodSpaceShip(b);
		timer.stop();
	}
	
	void controlVehicle(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			v.move(-1,0);
			break;
		case KeyEvent.VK_RIGHT:
			v.move(1,0);
			break;
		case KeyEvent.VK_D:
			difficulty += 0.1;
			break;
		case KeyEvent.VK_UP:
			v.move(0,-1);
			break;
		case KeyEvent.VK_DOWN:
			v.move(0,1);
			break;
		case KeyEvent.VK_SPACE:
			generateBullet();
			break;
		case KeyEvent.VK_ESCAPE:
			timer.stop();
			break;
		case KeyEvent.VK_ENTER:
			timer.start();
			break;
		}
	}
	
	
	public long getEND(){
		return e;
	}
	
//	public long getHP(){
//		return hp;
//	}

	public long getScore(){
		return score;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		controlVehicle(e);
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//do nothing
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//do nothing		
	}
	
	
}
