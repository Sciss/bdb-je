/*-
 * Copyright (C) 2002, 2017, Oracle and/or its affiliates. All rights reserved.
 *
 * This file was distributed by Oracle as part of a version of Oracle Berkeley
 * DB Java Edition made available at:
 *
 * http://www.oracle.com/technetwork/database/database-technologies/berkeleydb/downloads/index.html
 *
 * Please see the LICENSE file included in the top-level directory of the
 * appropriate version of Oracle Berkeley DB Java Edition for a copy of the
 * license and additional information.
 */

package jca.simple;

import java.rmi.RemoteException;
import javax.ejb.EJBObject;

public interface Simple extends EJBObject {

    public void put(String key, String data)
        throws RemoteException;

    public String get(String key)
        throws RemoteException;

    public void removeDatabase()
        throws RemoteException;
}
