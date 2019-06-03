/*
 * FILE: GeometrySerdeTest
 * Copyright (c) 2015 - 2018 GeoSpark Development Team
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package org.datasyslab.geospark.formatMapper.shapefileParser.shapes;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.datasyslab.geospark.geometryObjects.Circle;
import org.datasyslab.geospark.geometryObjects.GeometrySerde;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

public class GeometrySerdeTest
{
    private final Kryo kryo = new Kryo();
    private final WKTReader wktReader = new WKTReader();

    @Test
    public void test()
            throws Exception
    {
        test("POINT (1.3 4.5)");
        test("POINT (1.3 4.5 55.3)");
        test("MULTIPOINT ((1 1), (1.3 4.5), (5.2 999))");
        test("MULTIPOINT ((1 1 32.5), (1.3 4.5 234.4), (5.2 999 34156.7))");
        test("LINESTRING (1 1, 1.3 4.5, 5.2 999)");
        test("LINESTRING (1 1 432.3, 1.3 4.5 654.2, 5.2 999 43.2)");
        test("MULTILINESTRING ((1 1, 1.3 4.5, 5.2 999), (0 0, 0 1))");
        test("MULTILINESTRING ((1 1 5432.6, 1.3 4.5 5432.3, 5.2 999 543.2), (0 0 43.23, 0 1 43.32))");
        test("POLYGON ((0 0, 0 1, 1 1, 1 0.4, 0 0))");
        test("POLYGON ((0 0 5423.2, 0 1 432.3, 1 1 432.2, 1 0.4 2345.6, 0 0 5423.2))");
        test("POLYGON ((0 0, 0 1, 1 1, 1 0.4, 0 0), (0.2 0.2, 0.5 0.2, 0.5 0.5, 0.2 0.5, 0.2 0.2))");
        test("POLYGON ((0 0 43.3, 0 1 432.3, 1 1 43.3, 1 0.4 43.3, 0 0 43.3), (0.2 0.2 43.3, 0.5 0.2 43.3, 0.5 0.5 43.3, 0.2 0.5 43.3, 0.2 0.2 43.3))");
        test("MULTIPOLYGON (((0 0, 0 1, 1 1, 1 0.4, 0 0)), " +
                "((0 0, 0 1, 1 1, 1 0.4, 0 0), (0.2 0.2, 0.5 0.2, 0.5 0.5, 0.2 0.5, 0.2 0.2)))");
        test("MULTIPOLYGON (((0 0 43.3, 0 1 43.3, 1 1 43.3, 1 0.4 43.3, 0 0 43.3)), " +
                "((0 0 43.3, 0 1 43.3, 1 1 43.3, 1 0.4 43.3, 0 0 43.3), (0.2 0.2 43.3, 0.5 0.2 43.3, 0.5 0.5 43.3, 0.2 0.5 43.3, 0.2 0.2 43.3)))");
        test("GEOMETRYCOLLECTION (POINT(4 6), LINESTRING(4 6,7 10))");
        test("GEOMETRYCOLLECTION (POINT(4 6 43.3), LINESTRING(4 6 543.3,7 10 433.2))");
    }

    private void test(String wkt)
            throws Exception
    {
        Geometry geometry = parseWkt(wkt);
        assertGeometryEquals(geometry, serde(geometry));


        geometry.setUserData("This is a test");
        assertGeometryEquals(geometry, serde(geometry));

        if (geometry instanceof GeometryCollection) {
            return;
        }

        Circle circle = new Circle(geometry, 1.2);
        assertGeometryEquals(circle, serde(circle));
    }

    private Geometry parseWkt(String wkt)
            throws ParseException
    {
        return wktReader.read(wkt);
    }

    private Geometry serde(Geometry input)
    {
        byte[] ser = serialize(input);
        return kryo.readObject(new Input(ser), input.getClass());
    }

    private byte[] serialize(Geometry input)
    {
        kryo.register(input.getClass(), new GeometrySerde());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Output output = new Output(bos);
        kryo.writeObject(output, input);
        output.close();
        return bos.toByteArray();
    }

    private void assertGeometryEquals(Geometry expected, Geometry actual)
    {
        Assert.assertEquals(expected, actual);
        for(int i = 0 ; i < expected.getCoordinates().length; i++) {
            Assert.assertTrue(expected.getCoordinates()[i].equals3D(actual.getCoordinates()[i]));
        }
    }
}
