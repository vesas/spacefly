package com.vesas.spacefly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MainMenuLabel extends Label {
    
    private boolean active = false;

    private ShaderProgram glowShader;

    private Texture tex;

    public MainMenuLabel(CharSequence text, Skin skin) {
        super(text, skin);

        // background
        int width = 1;
		int height = 1;
		
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888 );
		pixmap.setFilter(Filter.BiLinear);
		pixmap.setColor( new Color(0,0,0,0) );
		pixmap.fill();
		
		tex = new Texture(pixmap);
		pixmap.dispose();
        // background


        String glowShaderString = Gdx.files.local("data/glowShader.glsl").readString();
        String vertexShaderString = Gdx.files.local("data/vertexShader.glsl").readString();
		glowShader = new ShaderProgram(vertexShaderString, glowShaderString);

		
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        
        ShaderProgram.pedantic = false;
        batch.setShader(glowShader);
        
        glowShader.bind();
        if(active)
            glowShader.setUniformf("glow_amount", 2.0f);
        else
            glowShader.setUniformf("glow_amount", 1.0f);

        batch.draw( tex, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        batch.setShader(null);
		
        super.draw(batch, parentAlpha);
        
    }

    public void setActive(boolean value) {
        active = value;
    }
}
