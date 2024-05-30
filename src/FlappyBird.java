import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;
    Image bg;
    Image tPipe;
    Image bPipe;
    Image dBird;
    Image uBird;

    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 50;
    int birdHeight = 35;

    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;
        Bird(Image img){
            this.img = img;
        }
        void change(Image img){
            this.img = img;
        }
    }
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;
    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;
        Pipe(Image img){
            this.img = img;
        }
    }

    Bird bird;
    int pipeVX = -4;
    int velocityY = 0;
    int gravity = 1;
    ArrayList<Pipe> pipes = new ArrayList<Pipe>();
    Random random = new Random ();

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    boolean start = false;
    double score = 0;

    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setFocusable(true);
        addKeyListener(this);
        bg = new ImageIcon(getClass().getResource("./bg.png")).getImage();
        uBird = new ImageIcon(getClass().getResource("./uBird.png")).getImage();
        dBird = new ImageIcon(getClass().getResource("./dBird.png")).getImage();
        tPipe = new ImageIcon(getClass().getResource("./tPipe.png")).getImage();
        bPipe = new ImageIcon(getClass().getResource("./bPipe.png")).getImage();
        bird = new Bird (uBird);
        placePipesTimer = new Timer(1800, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes();
            }
        });
        gameLoop = new Timer(1000/45,this);
        if (start){
            gameLoop.start();
            placePipesTimer.start();
        }
    }

    public void placePipes(){
        int randomPipeY = (int)(pipeY-pipeHeight/4-Math.random()*(pipeHeight/2));
        int gap = boardHeight/6;
        Pipe topPipe = new Pipe(tPipe);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);
        Pipe bottomPipe = new Pipe(bPipe);
        bottomPipe.y = topPipe.y+pipeHeight+gap;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        g.drawImage(bg,0,0,boardWidth,boardHeight,null);

        g.drawImage(bird.img,bird.x,bird.y,bird.width,bird.height,null);

        for (int i = 0;i<pipes.size();i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height,null);
        }
        if (!start){
            g.drawString("try to enter 'yoshi'",230,575);
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial",Font.PLAIN, 32));
        if (!start){
            g.drawString("Press Space to Start",30,300);
        }
        if (gameOver){
            g.drawString("Tap Space to Restart",30,300);
            g.drawString("Game Over: "+String.valueOf((int)score),80,360);
        }
        else{
            g.drawString("Score: "+String.valueOf((int)score),10,35);
        }

    }
    public void move(){
        if (velocityY<0){
            bird.change(uBird);
        }
        if(velocityY>=0){
            bird.change(dBird);
        }
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y,0);
        bird.y = Math.min(bird.y,550);
        for (int i = 0; i<pipes.size();i++){
            Pipe pipe = pipes.get(i);
            pipe.x += pipeVX;
            if (!pipe.passed&&bird.x>pipe.x+pipe.width){
                pipe.passed=true;
                score +=0.5;
            }
            if (collision(bird,pipe)){
                gameOver=true;
            }
        }

    }

    public boolean collision(Bird b, Pipe p){
        return ((b.x<p.x+p.width)&&(b.x+b.width>p.x)&&(b.y<p.y+p.height)&&(b.y+b.height>p.y));
    }
    public void actionPerformed(ActionEvent e) {
        if(start){
            move();
            repaint();
        }
        if (gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }


    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_SPACE){
            if(!start){
                start=true;
                gameLoop.start();
                placePipesTimer.start();
            }
            velocityY=-9;
        }
        if(gameOver){
            bird.y=birdY;
            velocityY=0;
            pipes.clear();
            score=0;
            gameOver=false;
            gameLoop.start();
            placePipesTimer.start();
        }

    }
    public void keyTyped(KeyEvent e) {
    }
    public void keyReleased(KeyEvent e) {
    }

}
