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

        // Add vertices one by one
        for (int i = 0; i < vertexList.length; i += 2) {
            
            Vertex point = new Vertex(vertexList[i], vertexList[i + 1]);
            
            // Find all triangles whose circumcircle contains this point
            List<Triangle> badTriangles = new ArrayList<>();
            for (Triangle triangle : triangleList) {
                if (isPointInCircumcircle(point, triangle)) {
                    badTriangles.add(triangle);
                }
            }
            
            // Find the boundary of the polygonal hole
            List<Edge> polygon = new ArrayList<>();
            for (Triangle triangle : badTriangles) {
                Edge[] edges = new Edge[] {
                    new Edge(triangle.a, triangle.b),
                    new Edge(triangle.b, triangle.c),
                    new Edge(triangle.c, triangle.a)
                };
                
                for (Edge edge : edges) {
                    if (isEdgeShared(edge, badTriangles) == 1) {
                        polygon.add(edge);
                    }
                }
            }
            
            // Remove bad triangles
            triangleList.removeAll(badTriangles);
            
            // Re-triangulate the polygonal hole
            for (Edge edge : polygon) {
                Triangle newTriangle = new Triangle(edge.start, edge.end, point);
                triangleList.add(newTriangle);
            }
        }

        // Convert triangles to indices and return
        return trianglesToIndices(vertexList);
    }

    public boolean isPointInCircumcircle(Vertex point, Triangle triangle) {
        Vertex a = triangle.a;
        Vertex b = triangle.b;
        Vertex c = triangle.c;

        // First determine triangle orientation
        float orientation = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        
        // Swap b and c if triangle is clockwise
        if (orientation < 0) {
            Vertex temp = b;
            b = c;
            c = temp;
        }

        float ax = a.x, ay = a.y;
        float bx = b.x, by = b.y;
        float cx = c.x, cy = c.y;
        float px = point.x, py = point.y;

        float determinant = (ax - px) * ((by - py) * (cx * cx + cy * cy - px * px - py * py) -
                        (cy - py) * (bx * bx + by * by - px * px - py * py)) -
                        (ay - py) * ((bx - px) * (cx * cx + cy * cy - px * px - py * py) -
                        (cx - px) * (bx * bx + by * by - px * px - py * py)) +
                        ((bx - px) * (cy - py) - (cx - px) * (by - py)) * (ax * ax + ay * ay - px * px - py * py);

        return determinant > 0;
    }

    private int isEdgeShared(Edge edge, List<Triangle> triangles) {
        int count = 0;
        for (Triangle triangle : triangles) {
            if ((edge.start == triangle.a && edge.end == triangle.b) ||
                (edge.start == triangle.b && edge.end == triangle.c) ||
                (edge.start == triangle.c && edge.end == triangle.a) ||
                (edge.end == triangle.a && edge.start == triangle.b) ||
                (edge.end == triangle.b && edge.start == triangle.c) ||
                (edge.end == triangle.c && edge.start == triangle.a)) {
                count++;
            }
        }
        return count;
    }

    private short[] trianglesToIndices(float[] vertexList) {
        // Remove super triangle
        triangleList.removeIf(t -> 
            t.a == superTriangle.a || t.a == superTriangle.b || t.a == superTriangle.c ||
            t.b == superTriangle.a || t.b == superTriangle.b || t.b == superTriangle.c ||
            t.c == superTriangle.a || t.c == superTriangle.b || t.c == superTriangle.c);

        // Convert to indices
        short[] indices = new short[triangleList.size() * 3];
        int idx = 0;
        for (Triangle t : triangleList) {
            indices[idx++] = findVertexIndex(t.a, vertexList);
            indices[idx++] = findVertexIndex(t.b, vertexList);
            indices[idx++] = findVertexIndex(t.c, vertexList);
        }
        return indices;
    }

    private short findVertexIndex(Vertex v, float[] vertexList) {
        for (short i = 0; i < vertexList.length; i += 2) {
            if (Math.abs(vertexList[i] - v.x) < 0.0001f && 
                Math.abs(vertexList[i + 1] - v.y) < 0.0001f) {
                return (short)(i / 2);
            }
        }
        return -1;
    }

}
