package com.lixin.entity.entityInterfaceImp;

import com.lixin.entity.entityInterface.EntityObject;
import com.lixin.gameInterface.Object;
import com.lixin.gameInterfaceImp.TouchEvent;

/**
 * Created by li on 2018/10/13.
 */

public class EntityObjectImp implements EntityObject {

    @Override
    public boolean OnClick(Object object, TouchEvent touchEvent) {
        if (Math.sqrt(Math.pow((object.x - touchEvent.x), 2)
                + Math.pow((object.y - touchEvent.y), 2)) <= object.R) {
            return true;
        } else {
            return false;
        }
    }
}
