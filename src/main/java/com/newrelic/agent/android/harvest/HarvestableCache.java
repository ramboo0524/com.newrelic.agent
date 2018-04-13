//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.harvest.type.Harvestable;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class HarvestableCache {
    private static final int DEFAULT_CACHE_LIMIT = 1024;
    private int limit = DEFAULT_CACHE_LIMIT;
    private final Collection<Harvestable> cache = this.getNewCache();

    public HarvestableCache() {
    }

    protected Collection<Harvestable> getNewCache() {
        return new CopyOnWriteArrayList();
    }

    public void add(Harvestable harvestable) {
        if(harvestable != null && this.cache.size() < this.limit) {
            this.cache.add(harvestable);
        }
    }

    public boolean get(Object h) {
        return this.cache.contains(h);
    }

    public Collection<Harvestable> flush() {
        if(this.cache.size() == 0) {
            return Collections.emptyList();
        } else {
            synchronized(this) {
                Collection<Harvestable> oldCache = this.getNewCache();
                oldCache.addAll(this.cache);
                this.cache.clear();
                return oldCache;
            }
        }
    }

    public int getSize() {
        return this.cache.size();
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
