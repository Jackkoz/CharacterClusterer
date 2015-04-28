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
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.util.MathUtils;

import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DBScan<T extends Clusterable>
{

    private final double eps;

    private final int minPts;

    public final HashMap<T, HashMap<T, Double>> distances = new HashMap<>();

    private enum PointStatus
    {
        NOISE,
        PART_OF_CLUSTER
    }

    public DBScan(final double eps, final int minPts)
            throws NotPositiveException
    {
        if (eps < 0.0d) {
            throw new NotPositiveException(eps);
        }
        if (minPts < 0) {
            throw new NotPositiveException(minPts);
        }

        this.eps = eps;
        this.minPts = minPts;
    }

    public List<Cluster<T>> cluster(final Collection<T> points) throws NullArgumentException
    {
        // sanity checks
        MathUtils.checkNotNull(points);

        final List<Cluster<T>> clusters = new ArrayList<Cluster<T>>();
        final Map<Clusterable, PointStatus> visited = new HashMap<Clusterable, PointStatus>();

        for (final T point : points) {
            if (visited.get(point) != null) {
                continue;
            }
            final List<T> neighbors = getNeighbors(point, points);
            if (neighbors.size() >= minPts) {
                // DBSCAN does not care about center points
                final Cluster<T> cluster = new Cluster<T>();
                clusters.add(expandCluster(cluster, point, neighbors, points, visited));
            } else {
                visited.put(point, PointStatus.NOISE);
            }
        }

        return clusters;
    }

    /**
     * Expands the cluster to include density-reachable items.
     *
     * @param cluster Cluster to expand
     * @param point Point to add to cluster
     * @param neighbors List of neighbors
     * @param points the data set
     * @param visited the set of already visited points
     * @return the expanded cluster
     */
    private Cluster<T> expandCluster(final Cluster<T> cluster,
                                     final T point,
                                     final List<T> neighbors,
                                     final Collection<T> points,
                                     final Map<Clusterable, PointStatus> visited) {
        cluster.addPoint(point);
        visited.put(point, PointStatus.PART_OF_CLUSTER);

        List<T> seeds = new ArrayList<T>(neighbors);
        int index = 0;
        while (index < seeds.size()) {
            final T current = seeds.get(index);
            PointStatus pStatus = visited.get(current);
            // only check non-visited points
            if (pStatus == null) {
                final List<T> currentNeighbors = getNeighbors(current, points);
                if (currentNeighbors.size() >= minPts) {
                    seeds = merge(seeds, currentNeighbors);
                }
            }

            if (pStatus != PointStatus.PART_OF_CLUSTER) {
                visited.put(current, PointStatus.PART_OF_CLUSTER);
                cluster.addPoint(current);
            }

            index++;
        }
        return cluster;
    }

    /**
     * Returns a list of density-reachable neighbors of a {@code point}.
     *
     * @param point the point to look for
     * @param points possible neighbors
     * @return the List of neighbors
     */
    private List<T> getNeighbors(final T point, final Collection<T> points) {
        final List<T> neighbors = new ArrayList<T>();
        for (final T neighbor : points) {
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
                        distances.put(neighbor, new HashMap<T, Double>());

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

    /**
     * Merges two lists together.
     *
     * @param one first list
     * @param two second list
     * @return merged lists
     */
    private List<T> merge(final List<T> one, final List<T> two) {
        final Set<T> oneSet = new HashSet<T>(one);
        for (T item : two) {
            if (!oneSet.contains(item)) {
                one.add(item);
            }
        }
        return one;
    }

    protected double distance(Clusterable p1, Clusterable p2)
    {
        Character c1 = (Character) p1;
        Character c2 = (Character) p2;
        BufferedImage img1 = c1.img;
        BufferedImage img2 = c2.img;

        return eucDistance(img1, img2);
    }

    private double eucDistance(BufferedImage img1, BufferedImage img2)
    {
        double distance = 0;
        for (int x = 0; x < Math.max(img1.getWidth(), img2.getWidth()); x++)
            for (int y = 0; y < Math.max(img1.getHeight(), img2.getHeight()); y++)
            {
                distance += minDistance(x, y, img1, img2);
                distance += minDistance(x, y, img2, img1);
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
        final int RANGE = 2;

        for (int x1 = x - RANGE; x1 <= x + RANGE; x1++)
        {
            if (distance == 1)
                break;

            if ( x1 < 0 || x1 >= img2.getWidth())
                continue;

            for (int y1 = y - RANGE; y1 <= y + RANGE; y1++)
            {
                if (y1 >= 0 && y1 < img2.getHeight() && img2.getRGB(x1, y1) == colour)
                {
                    distance = Math.min(distance, Math.min(Math.abs(x - x1), Math.abs(y - y1)));
//                    distance = 1;
                }
            }
        }

        return distance * distance;
    }
}
