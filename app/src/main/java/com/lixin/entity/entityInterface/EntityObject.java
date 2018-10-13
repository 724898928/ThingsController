package com.lixin.entity.entityInterface;

import com.lixin.gameInterface.Object;
import com.lixin.gameInterfaceImp.TouchEvent;

/**
 * Created by li on 2018/10/13.
 */

public interface EntityObject {
    boolean OnClick(Object object, TouchEvent touchEvent);
}
