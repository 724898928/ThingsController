package com.lixin.gameInterfaceImp;

/**
 * Created by li on 2018/8/25.
 */

import com.lixin.gameInterface.PoolObjectFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作垃圾回收器
 */
public class Pool<T> {
    private final List<T> freeObjects;
    private final PoolObjectFactory<T> Factory;
    private final int maxSixe;

    public Pool(List<T> freeObjects, PoolObjectFactory<T> factory, int maxSixe) {
        this.freeObjects = freeObjects;
        this.Factory = factory;
        this.maxSixe = maxSixe;
    }

    public Pool(PoolObjectFactory<T> factory, int maxSixe) {
        this.freeObjects = new ArrayList<>();
        this.Factory = factory;
        this.maxSixe = maxSixe;
    }

    /**
     * 获取已有的对象
     *
     * @return
     */
    public T newObject() {
        T object = null;
        if (freeObjects.size() == 0) {
            object = Factory.createObject();
        } else {
            object = freeObjects.remove(freeObjects.size() - 1);
        }
        return object;
    }

    /**
     * 添加对象，允许重新插入不再实用的对象。
     * @param object
     */
    public void free(T object) {
        if (freeObjects.size() < maxSixe) {
            freeObjects.add(object);
        }
    }
}
