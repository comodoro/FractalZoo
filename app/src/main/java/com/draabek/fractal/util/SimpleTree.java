package com.draabek.fractal.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Low performance and low capacity tree structure
 * @param <T> The datatype this tree holds
 */
public class SimpleTree<T> {

    public T data;
    public SimpleTree<T> parent;
    public List<SimpleTree<T>> children;

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }

    public SimpleTree(T data) {
        this.data = data;
        this.children = new LinkedList<>();
    }

    public SimpleTree<T> addChild(T child) {
        SimpleTree<T> childNode = new SimpleTree<>(child);
        childNode.parent = this;
        this.children.add(childNode);
        return childNode;
    }

    public int getLevel() {
        if (this.isRoot())
            return 0;
        else
            return parent.getLevel() + 1;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "[null]";
    }

    public void putPath(Deque<T> pathParam, T value) {
        if (pathParam.isEmpty() || !pathParam.peekLast().equals(data)) {
            return;
        }
        Deque<T> path = new ArrayDeque<>(pathParam);
        path.removeLast();
        if (path.isEmpty()) {
            addChild(value);
            return;
        }
        for (int repeat = 0;repeat < 2;repeat++) {
            for (int i = 0; i < children.size(); i++) {
                SimpleTree<T> child = children.get(i);
                if ((path.peekLast() != null) && (path.peekLast().equals(child.data))) {
                    child.putPath(path, value);
                    return;
                }
            }
            addChild(path.peekLast());
        }
    }

    public void putPath(T[] pathParam, T value) {
        List<T> l = Arrays.asList(pathParam);
        Collections.reverse(l);
        ArrayDeque<T> path = new ArrayDeque<>(l);
        putPath(path, value);
    }

    private List<T> enumerateChildren() {
        List<T> childrenData = new ArrayList<>();
        for (SimpleTree<T> child : children) {
            childrenData.add(child.data);
        }
        return childrenData;
    }

    public List<T> getChildren(T[] pathParam) {
        List<T> l = Arrays.asList(pathParam);
        Collections.reverse(l);
        ArrayDeque<T> path = new ArrayDeque<>(l);
        return getChildren(path);
    }

    public List<T> getChildren(Deque<T> pathParam) {
        Deque<T> path = new ArrayDeque<>(pathParam);
        if (path.size() > 0) {
            for (SimpleTree<T> child : children) {
                if (path.peekLast().equals(child.data)) {
                    path.removeLast();
                    return child.getChildren(path);
                }
            }
            return null;
        }
        return enumerateChildren();
    }
}