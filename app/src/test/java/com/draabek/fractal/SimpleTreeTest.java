package com.draabek.fractal;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Stack;

public class SimpleTreeTest {

    @Test
    public void isRoot() {
        SimpleTree<Integer> simpleTree = new SimpleTree<>(0);
        Assert.assertTrue(simpleTree.isRoot());
    }

    @Test
    public void isLeaf() {
        SimpleTree<Integer> simpleTree = new SimpleTree<>(0);
        Assert.assertTrue(simpleTree.isLeaf());
        simpleTree.addChild(1);
        Assert.assertTrue(!simpleTree.isLeaf());
    }

    @Test
    public void addChild() {
        SimpleTree<Integer> simpleTree = new SimpleTree<>(0);
        simpleTree.addChild(1);
        simpleTree.addChild(2);
        Assert.assertEquals(2, simpleTree.children.size());
    }

    @Test
    public void getLevel() {
        SimpleTree<Integer> simpleTree = new SimpleTree<>(0);
        simpleTree.addChild(1);
        Assert.assertEquals(0, simpleTree.getLevel());
        Assert.assertEquals(1, simpleTree.children.get(0).getLevel());
    }

    @Test
    public void putPathStringArray() {
        SimpleTree<String> simpleTree = new SimpleTree<>("ROOT");
        String[] path1 = {"ROOT", "blue", "moon"};
        simpleTree.putPath(path1, "rises");
        String[] path2 = {"ROOT", "red", "moon"};
        simpleTree.putPath(path2, "sets");
        Assert.assertEquals(2, simpleTree.children.size());
        Assert.assertEquals(1, simpleTree.children.get(0).children.size());
        Assert.assertEquals("rises", simpleTree
                .children.get(0)
                .children.get(0)
                .children.get(0)
                .data);
        Assert.assertEquals("sets", simpleTree
                .children.get(1)
                .children.get(0)
                .children.get(0)
                .data);    }

    @Test
    public void putPathDeque() {
        SimpleTree<Integer> simpleTree = new SimpleTree<>(0);
        Deque<Integer> path = new ArrayDeque<>(Arrays.asList(2,1,0));
        simpleTree.putPath(path, 10);
        path.clear();
        path.addAll(Arrays.asList(3,1,0));
        simpleTree.putPath(path, 20);
        Assert.assertEquals(1, simpleTree.children.size());
        Assert.assertEquals(2, simpleTree.children.get(0).children.size());
        Assert.assertEquals(10, (int)simpleTree
                .children.get(0)
                .children.get(0)
                .children.get(0)
                .data);
        Assert.assertEquals(20, (int)simpleTree
                .children.get(0)
                .children.get(1)
                .children.get(0)
                .data);
    }

    @Test
    public void getChildrenStringArray() {
        SimpleTree<String> simpleTree = new SimpleTree<>("ROOT");
        String[] path1 = {"ROOT", "blue", "moon"};
        simpleTree.putPath(path1, "rises");
        String[] path2 = {"ROOT", "red", "moon"};
        simpleTree.putPath(path2, "sets");
        String[] path3 = {"ROOT", "red", "sun"};
        simpleTree.putPath(path3, "rises");
        List<String> children1 = simpleTree.getChildren(new String[]{"red"});
        Assert.assertEquals(2, children1.size());
    }

    @Test
    public void getChildrenDeque() {
        SimpleTree<Integer> simpleTree = new SimpleTree<>(0);
        Deque<Integer> path = new ArrayDeque<>(Arrays.asList(2, 1, 0));
        simpleTree.putPath(path, 10);
        path.clear();
        path.addAll(Arrays.asList(3, 1, 0));
        simpleTree.putPath(path, 20);
        path.clear();
        path.addAll(Arrays.asList(3, 2, 0));
        simpleTree.putPath(path, 20);
        path.clear();
        path.addAll(Arrays.asList(4, 3, 0));
        simpleTree.putPath(path, 20);
        List<Integer> children1 = simpleTree.getChildren(new ArrayDeque<>(Collections.singletonList(1)));
        Assert.assertNotNull(children1);
        Assert.assertEquals(2, children1.size());
        List<Integer> children3 = simpleTree.getChildren(new ArrayDeque<>(Arrays.asList(3, 1)));
        Assert.assertNotNull(children3);
        Assert.assertEquals(1, children3.size());
        List<Integer> children2 = simpleTree.getChildren(new ArrayDeque<>());
        Assert.assertNotNull(children2);
        Assert.assertEquals(3, children2.size());
    }
}