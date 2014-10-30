package com.metamx.collections.spatial;

import com.metamx.collections.bitmap.BitmapFactory;
import com.metamx.collections.bitmap.ConciseBitmapFactory;
import com.metamx.collections.bitmap.RoaringBitmapFactory;
import com.metamx.collections.spatial.split.LinearGutmanSplitStrategy;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

/**
 */
public class RTreeTest
{
  private RTree tree;
  private RTree roaringtree;

  @Before
  public void setUp() throws Exception
  {
    BitmapFactory bf = new ConciseBitmapFactory();
    tree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, bf), bf );
    BitmapFactory rbf = new RoaringBitmapFactory();
    roaringtree = new RTree(2, new LinearGutmanSplitStrategy(0, 50, rbf), rbf );

  }

  @Test
  public void testInsertNoSplit()
  {
    float[] elem = new float[]{5, 5};
    tree.insert(elem, 1);
    Assert.assertTrue(Arrays.equals(elem, tree.getRoot().getMinCoordinates()));
    Assert.assertTrue(Arrays.equals(elem, tree.getRoot().getMaxCoordinates()));

    tree.insert(new float[]{6, 7}, 2);
    tree.insert(new float[]{1, 3}, 3);
    tree.insert(new float[]{10, 4}, 4);
    tree.insert(new float[]{8, 2}, 5);

    Assert.assertEquals(tree.getRoot().getChildren().size(), 5);

    float[] expectedMin = new float[]{1, 2};
    float[] expectedMax = new float[]{10, 7};

    Assert.assertTrue(Arrays.equals(expectedMin, tree.getRoot().getMinCoordinates()));
    Assert.assertTrue(Arrays.equals(expectedMax, tree.getRoot().getMaxCoordinates()));
    Assert.assertEquals(tree.getRoot().getArea(), 45.0d);
  }

  @Test
  public void testInsertDuplicatesNoSplit()
  {
    tree.insert(new float[]{1, 1}, 1);
    tree.insert(new float[]{1, 1}, 1);
    tree.insert(new float[]{1, 1}, 1);

    Assert.assertEquals(tree.getRoot().getChildren().size(), 3);
  }

  @Test
  public void testInsertDuplicatesNoSplitRoaring()
  {
    roaringtree.insert(new float[]{1, 1}, 1);
    roaringtree.insert(new float[]{1, 1}, 1);
    roaringtree.insert(new float[]{1, 1}, 1);

    Assert.assertEquals(roaringtree.getRoot().getChildren().size(), 3);
  }

  
  @Test
  public void testSplitOccurs()
  {
    Random rand = new Random();
    for (int i = 0; i < 100; i++) {
      tree.insert(new float[]{rand.nextFloat(), rand.nextFloat()}, i);
    }

    Assert.assertTrue(tree.getRoot().getChildren().size() > 1);
  }

  @Test
  public void testSplitOccursRoaring()
  {
    Random rand = new Random();
    for (int i = 0; i < 100; i++) {
      roaringtree.insert(new float[]{rand.nextFloat(), rand.nextFloat()}, i);
    }

    Assert.assertTrue(roaringtree.getRoot().getChildren().size() > 1);
  }

}