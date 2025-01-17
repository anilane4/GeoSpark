/*
 * FILE: PointParser
 * Copyright (c) 2015 - 2019 GeoSpark Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datasyslab.geospark.formatMapper.shapefileParser.parseUtils.shp;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.io.IOException;

public class PointParser
        extends ShapeParser
{

    /**
     * create a parser that can abstract a Point from input source with given GeometryFactory.
     *
     * @param geometryFactory the geometry factory
     */
    public PointParser(GeometryFactory geometryFactory)
    {
        super(geometryFactory);
    }

    /**
     * abstract a Point shape.
     *
     * @param reader the reader
     * @return the geometry
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public Geometry parseShape(ShapeReader reader)
    {
        double x = reader.readDouble();
        double y = reader.readDouble();
        double z = reader.readDouble();
        Point point = geometryFactory.createPoint(new Coordinate(x, y, z));
        return point;
    }
}
