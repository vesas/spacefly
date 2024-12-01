package com.vesas.spacefly.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.vesas.spacefly.box2d.BodyBuilder;

// Tests for the BaseBullets class
public class BaseBulletsTest {

    private AbstractBullet createTestBullet() {

        // create random values
        float posx = (float) (Math.random() * 100);
        float posy = (float) (Math.random() * 100);
        float dirx = (float) (Math.random() * 100);
        float diry = (float) (Math.random() * 100);
        short category = (short) (Math.random() * 100);
        short mask = (short) (Math.random() * 100);

        
        return new AbstractBullet() {
            {
                this.body = mock(Body.class);
            }
            
            @Override
            public void draw(SpriteBatch batch) {}
            
        };
    }

    @Test
    public void testAddBullet() {
        BaseBullets bullets = new BaseBullets();
        AbstractBullet bullet = createTestBullet();
        
        bullets.add(bullet);
        assertEquals(1, bullets.getBullets().size);
    }

    @Test
    public void testRemoveBullet() {
        BaseBullets bullets = new BaseBullets();
        AbstractBullet bullet = createTestBullet();
        
        bullets.add(bullet);
        bullets.preRemove(bullet);
        bullets.remove();
        
        assertEquals(0, bullets.getBullets().size);
    }

    @Test
    public void testClear() {
        BaseBullets bullets = new BaseBullets();
        bullets.add(createTestBullet());
        bullets.add(createTestBullet());
        
        bullets.clear();
        
        assertEquals(0, bullets.getBullets().size);
        assertEquals(0, bullets.bulletsToBeRemoved.size);
    }

    @Test
    public void testMultiplePreRemoveBeforeSingleRemove() {
        BaseBullets bullets = new BaseBullets();
        AbstractBullet bullet1 = createTestBullet();
        AbstractBullet bullet2 = createTestBullet();
        
        bullets.add(bullet1);
        bullets.add(bullet2);
        bullets.preRemove(bullet1);
        bullets.preRemove(bullet2);
        bullets.remove();
        
        assertEquals(0, bullets.getBullets().size);
    }

    @Test
    public void testPreRemoveNonexistentBullet() {
        BaseBullets bullets = new BaseBullets();
        AbstractBullet bullet = createTestBullet();
        
        bullets.preRemove(bullet); // Should not throw
        bullets.remove();
        
        assertEquals(0, bullets.getBullets().size);
    }


}
