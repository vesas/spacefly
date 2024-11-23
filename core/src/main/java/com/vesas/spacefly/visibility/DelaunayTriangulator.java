package com.vesas.spacefly.visibility;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vesas.spacefly.game.G;

public class DelaunayTriangulator {

    // private short[] triangleList = new short[0];

    private List<Triangle> triangleList = new ArrayList<>();

    public static class Vertex {
        public final float x;
        public final float y;

        public Vertex(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Edge {
        public final Vertex start;
        public final Vertex end;

        public Edge(Vertex start, Vertex end) {
            this.start = start;
            this.end = end;
        }
    }

    public static class Triangle {
        public final Vertex a;
        public final Vertex b;
        public final Vertex c;

        public Triangle(Vertex a, Vertex b, Vertex c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        private static final float sqrEpsilon = 0.0000001f;

        public void circumCenter(ShapeRenderer shapeRenderer) {

            G.shapeRenderer.setColor(0.3f, 0.5f, 0.99f, 1);
            G.shapeRenderer.line(a.x, a.y, b.x, b.y);
            G.shapeRenderer.line(b.x, b.y, c.x, c.y);
            G.shapeRenderer.line(c.x, c.y, a.x, a.y);

            float midABX = (a.x + b.x) / 2.0f;
            float midABY = (a.y + b.y) / 2.0f;

            float midBCX = (b.x + c.x) / 2.0f;
            float midBCY = (b.y + c.y) / 2.0f;

            float bToMidABX = b.x - midABX;
            float bToMidABY = b.y - midABY;

            float cToMidBCX = c.x - midBCX;
            float cToMidBCY = c.y - midBCY;

            G.shapeRenderer.setColor(0.3f, 0.9f, 0.99f, 1);
            G.shapeRenderer.line(midABX, midABY, midABX + bToMidABY, midABY - bToMidABX);


            G.shapeRenderer.setColor(0.8f, 0.4f, 0.99f, 1);
            G.shapeRenderer.line(midBCX, midBCY, midBCX + cToMidBCY, midBCY - cToMidBCX);

            float p0X = midABX;
            float p0Y = midABY;
            float d0X = bToMidABY;
            float d0Y = -bToMidABX;

            float p1X = midBCX;
            float p1Y = midBCY;
            float d1X = cToMidBCY;
            float d1Y = -cToMidBCX;

            float eX = p1X - p0X;
            float eY = p1Y - p0Y;

            float kross = d0X * d1Y - d0Y * d1X;
            float sqrKross = kross * kross;

            float sqrLen0 = d0X * d0X + d0Y * d0Y;
            float sqrLen1 = d1X * d1X + d1Y * d1Y;

            if(sqrKross > sqrEpsilon * sqrLen0 * sqrLen1) {
                //  not parallel
                float s = (eX * d1Y - eY * d1X) / kross;

                float intersectionx = p0X + s * d0X;
                float intersectiony = p0Y + s * d0Y;
            
                double radius = Math.sqrt((double)((intersectionx - a.x) * (intersectionx - a.x) + (intersectiony - a.y) * (intersectiony - a.y)));

                G.shapeRenderer.setColor(0.8f, 0.4f, 0.99f, 1);
                G.shapeRenderer.circle(intersectionx, intersectiony, (float)radius, 70);

                
            }
        }
    }


    public DelaunayTriangulator() {
        
    }

    // the original supertriangle
    private Triangle superTriangle;

    public short[] triangulate(float [] vertexList) {

        if(vertexList == null || vertexList.length == 0) {
            return new short[0];
        }

        // if vertexList has exactly 3 points, just return that triangle
        if(vertexList.length == 6) {
            return new short[] {0, 1, 2};
        }

        // if vertexList has exactly 4 points, return 2 triangles' indices
        if(vertexList.length == 8) {
            return new short[] {
                0, 1, 2,  // first triangle
                0, 2, 3   // second triangle
            };
        }

        float xmax = -Float.MAX_VALUE;
        float ymax = -Float.MAX_VALUE;

        float ymin = Float.MAX_VALUE;
        float xmin = Float.MAX_VALUE;

        for(int i = 0; i < vertexList.length; i += 2) {
            float x = (float)vertexList[i];
            float y = (float)vertexList[i + 1];

            if(x > xmax) {
                xmax = x;
            }

            if(x < xmin) {
                xmin = x;
            }

            if(y > ymax) {
                ymax = y;
            }

            if(y < ymin) {
                ymin = y;
            }
        }

        float xdim = xmax - xmin;
        float ydim = ymax - ymin;

        float xmiddle = xmin + xdim / 2.0f;
        float ymiddle = ymin + ydim / 2.0f;

        superTriangle = new Triangle(
            new Vertex(xmiddle - 3.0f * xdim, ymin - 3.0f * ydim),
            new Vertex(xmiddle, ymax + 2 * ydim),
            new Vertex(xmiddle + 3.0f * xdim, ymin - 3.0f * ydim)
        );

        triangleList.add(superTriangle);

        return new short[0];
    }

}
