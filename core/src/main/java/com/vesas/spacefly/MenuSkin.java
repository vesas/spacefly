package com.vesas.spacefly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MenuSkin {
    
    static Skin skin = null;

    public static Skin getSkin() {

        if(skin != null) {
            return skin;
        }

        TextureAtlas atlas = new TextureAtlas(Gdx.files.local("data/skin.atlas"));
        
        Skin skin = new Skin();
        skin.addRegions(atlas);

        int width = 1;
		int height = 1;

        Color col1 = new Color(0.6f, 0.5f, 0.4f, 1.0f);
		
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888 );
		pixmap.setFilter(Filter.BiLinear);
		pixmap.setColor( col1 );
		pixmap.fill();
		
		Texture tex = new Texture(pixmap);
		pixmap.dispose();

        skin.add("default", tex);

        return skin;
    }
}
