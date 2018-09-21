package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture gameover;
    private Random numeroRandomico;
    private BitmapFont font;
    private BitmapFont mensagem;
    private Circle passarocirculo;
    private Rectangle reccanoTopo;
    private Rectangle reccanoBaixo;
    //private ShapeRenderer shapeRenderer;

    //Atributos de configuracao
    private int larguraDispositivo;
    private int alturaDispositivo;
    private int estadodojogo=0; //0 esta parado 1 esta a correr 2 estado gameover
    private int pontuacao=0;

    private float variacao = 0;
    private float velocidadeQueda=0;
    private float posicaoInicialVertical;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos;
    private float deltaTime;
    private float alturaEntreCanosRandomica;
    private boolean marcouPonto=false;

	@Override
	public void create () {

        batch = new SpriteBatch();
        numeroRandomico = new Random();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(6);

        passarocirculo = new Circle();
        reccanoTopo=new Rectangle();
        reccanoBaixo = new Rectangle();
        //shapeRenderer= new ShapeRenderer();

            mensagem= new BitmapFont();
        mensagem.setColor(Color.WHITE);
        mensagem.getData().setScale(3);

        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo.png");
        canoTopo = new Texture("cano_topo.png");
        gameover = new Texture("game_over.png");

        larguraDispositivo = Gdx.graphics.getWidth();
        alturaDispositivo  = Gdx.graphics.getHeight();
        posicaoInicialVertical = alturaDispositivo / 2;
        posicaoMovimentoCanoHorizontal = larguraDispositivo;
        espacoEntreCanos = 300;

    }

	@Override
	public void render () {
	    //passaro bater asas emquanto nao comeca
        deltaTime = Gdx.graphics.getDeltaTime();
        if (variacao > 2) variacao = 0;

        variacao += deltaTime * 10;
	    if(estadodojogo == 0){

            if(Gdx.input.justTouched()){
                estadodojogo=1;
            }
        }else {//jogo iniciado
            velocidadeQueda++;
            if (posicaoInicialVertical > 0 || velocidadeQueda < 0)//verificar se saiu da tela
                posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;

            if(estadodojogo==1) {

                posicaoMovimentoCanoHorizontal -= deltaTime * 200;

                if (Gdx.input.justTouched()) {
                    velocidadeQueda = -15;
                }
                //Verifica se o cano saiu inteiramente da tela
                if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
                    marcouPonto = false;
                }
                //verifica se passa -> pontuação
                if (posicaoMovimentoCanoHorizontal < 120) {
                    if (!marcouPonto) {

                        pontuacao++;
                        marcouPonto = true;
                    }
                }
            }
            else{
                //tela gameover
                if (Gdx.input.justTouched()) {
                    estadodojogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posicaoInicialVertical = alturaDispositivo / 2;
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                }

            }
	    }
            //colocar imagens
            batch.begin();

            batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
            batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica);
            batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);
            batch.draw(passaros[(int) variacao], 120, posicaoInicialVertical);
            font.draw(batch,String.valueOf(pontuacao),larguraDispositivo/2,alturaDispositivo-50);

            if(estadodojogo==2) {
                batch.draw(gameover, larguraDispositivo / 2 - gameover.getWidth() / 2, alturaDispositivo / 2);
                mensagem.draw(batch,"Toque para Reniciar",larguraDispositivo/2 - 200,alturaDispositivo/2 - gameover.getHeight()/2);

            }
            batch.end();

            //criar formular a volta dos objectos
            passarocirculo.set(120 + passaros[0].getWidth()/2,posicaoInicialVertical + passaros[0].getHeight()/2,passaros[0].getWidth()/2);

            reccanoBaixo= new Rectangle(
                    posicaoMovimentoCanoHorizontal,
                    alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica,
                    canoBaixo.getWidth(),
                    canoBaixo.getHeight()
            );
            reccanoTopo= new Rectangle(
                    posicaoMovimentoCanoHorizontal,
                    alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica,
                    canoTopo.getWidth(),
                    canoBaixo.getHeight()
            );


            //desenhar estruturas a volta por causa das colisoes
            /*shapeRenderer.begin( ShapeRenderer.ShapeType.Filled);
            shapeRenderer.circle(passarocirculo.x,passarocirculo.y,passarocirculo.radius);
            shapeRenderer.rect      (reccanoBaixo.x,reccanoBaixo.y,reccanoBaixo.width,reccanoBaixo.height);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.end();
                */
             //TESTE DE COLISAO
        if(Intersector.overlaps(passarocirculo,reccanoBaixo) ||Intersector.overlaps(passarocirculo,reccanoTopo) ||
                posicaoInicialVertical<=0 ||posicaoInicialVertical>= alturaDispositivo ){
            //Gdx.app.log("Colisao","houve colisao");
            estadodojogo=2;
        }
	}
}
