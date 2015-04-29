/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import Utils.Image;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathUtils;

import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBScan
{
    private final double eps;
    private final double epsSqr;
    private final int minPts;

    public final HashMap<Character, HashMap<Character, Double>> distances = new HashMap<>();

    public static enum PointStatus
    {
        NOISE,
        PART_OF_CLUSTER
    }

    public DBScan(final double eps, final int minPts) throws NotPositiveException
    {
        if (eps < 0.0d) {
            throw new NotPositiveException(eps);
        }
        if (minPts < 0) {
            throw new NotPositiveException(minPts);
        }

        this.eps = eps;
        this.epsSqr = eps * eps;
        this.minPts = minPts;
    }

    public List<List<Character>> cluster(final Collection<Character> points) throws NullArgumentException
    {
        // sanity checks
        MathUtils.checkNotNull(points);

        final List<List<Character>> clusters = new ArrayList<>();
//        final Map<Character, PointStatus> visited = new HashMap<>();

        for (final Character point : points)
        {
            if (point.visited)
                continue;

            final List<Character> neighbors = getNeighbors(point, points);
            if (neighbors.size() >= minPts)
            {
                final List<Character> cluster = new ArrayList<>();
                clusters.add(expandCluster(cluster, point, neighbors, points));
            }
            else
            {
                point.status = PointStatus.NOISE;
                point.visited = true;
            }
        }

        return clusters;
    }

    private List<Character> expandCluster(final List<Character> cluster,
                                     final Character point,
                                     final List<Character> neighbors,
                                     final Collection<Character> points) {
        cluster.add(point);
        point.status = PointStatus.PART_OF_CLUSTER;
        point.visited = true;

        List<Character> seeds = new ArrayList<>(neighbors);
        int index = 0;
        while (index < seeds.size()) {
            final Character current = seeds.get(index);
            // only check non-visited points
            if (current.status == null) {
                final List<Character> currentNeighbors = getNeighbors(current, points);
                if (currentNeighbors.size() >= minPts) {
                    seeds = merge(seeds, currentNeighbors);
                }
            }

            if (current.status != PointStatus.PART_OF_CLUSTER) {
                current.status = PointStatus.PART_OF_CLUSTER;
                current.visited = true;
                cluster.add(current);
            }

            index++;
        }
        return cluster;
    }

    private List<Character> getNeighbors(final Character point, final Collection<Character> points) {
        final List<Character> neighbors = new ArrayList<>();
        for (final Character neighbor : points) {
            if (point != neighbor)
            {
                double distance;
                if (distances.containsKey(neighbor) && distances.get(neighbor).containsKey(point))
                    distance = distances.get(neighbor).get(point);
                else if (distances.containsKey(point) && distances.get(point).containsKey(neighbor))
                    distance = distances.get(point).get(neighbor);
                else
                {
                    distance = distance(neighbor, point);
                    if (!distances.containsKey(neighbor))
                        distances.put(neighbor, new HashMap<>());

                    distances.get(neighbor).put(point, distance);
                }
                if (distance <= eps)
                {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    private List<Character> merge(final List<Character> one, final List<Character> two) {
        final Set<Character> oneSet = new HashSet<>(one);
        for (Character item : two) {
            if (!oneSet.contains(item)) {
                one.add(item);
            }
        }
        return one;
    }

    protected double distance(Character p1, Character p2)
    {
        return eucDistance(p1.img, p2.img);
    }

    private double eucDistance(BufferedImage img1, BufferedImage img2)
    {
        double distance = 0;
        for (int x = 0; x < Math.max(img1.getWidth(), img2.getWidth()); x++)
            for (int y = 0; y < Math.max(img1.getHeight(), img2.getHeight()); y++)
            {
                distance += minDistance(x, y, img1, img2);
                distance += minDistance(x, y, img2, img1);

                if (distance > epsSqr + 1)
                    break;
            }
        distance = Math.sqrt(distance);

        return distance;
    }

    private double minDistance(int x, int y, BufferedImage img1, BufferedImage img2)
    {
        if (! (x < img1.getWidth() && y < img1.getHeight()))
            return 0;

        int colour = img1.getRGB(x ,y);

        if (colour != Image.BLACK)
            return 0;

        if (x < img2.getWidth() && y < img2.getHeight() && img2.getRGB(x, y) == colour)
            return 0;

        int distance = 6;
        final int RANGE = 1;

        for (int x1 = x - RANGE; x1 <= x + RANGE; x1++)
        {
            if (distance == 1)
                break;

            if ( x1 < 0 || x1 >= img2.getWidth())
                continue;

            for (int y1 = y - (x1 - x); y1 <= y + RANGE; y1++)
            {
                if (y1 >= 0 && y1 < img2.getHeight() && img2.getRGB(x1, y1) == colour)
                {
                    distance = Math.min(distance, Math.min(Math.abs(x - x1), Math.abs(y - y1)));
                }
            }
        }

        return (distance * distance) / 2;
    }
}
