package com.tibco.flogo.ss.util;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * Created by mregiste on 2/10/2016.
 */
public enum StorageType
{
    REDIS(1), INMEM(2);

    private final Integer id;

    private static final Map<Integer, StorageType> intToStorageType   =
            new HashMap<Integer, StorageType>();
    private static final Map<String, StorageType>  stringToServerType =
            new HashMap<String, StorageType>();

    static
    {
        for (StorageType type : StorageType.values())
        {
            intToStorageType.put(type.id, type);
        }

        for (StorageType type : StorageType.values())
        {
            stringToServerType.put(type.id.toString(), type);
        }
    }

    private StorageType(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    public static StorageType fromInteger(Integer id)
    {
        if (intToStorageType.containsKey(id))
        {
            return intToStorageType.get(id);
        }
        throw new NoSuchElementException(id + "not found");
    }
}
