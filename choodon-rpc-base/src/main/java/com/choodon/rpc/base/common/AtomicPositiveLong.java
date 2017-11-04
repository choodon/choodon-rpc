package com.choodon.rpc.base.common;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicPositiveLong extends Number {

    private static final long serialVersionUID = -3038533876489105940L;

    private final AtomicLong i;

    public AtomicPositiveLong() {
        i = new AtomicLong();
    }

    public AtomicPositiveLong(long initialValue) {
        i = new AtomicLong(initialValue);
    }

    public final long getAndIncrement() {
        for (; ; ) {
            long current = i.get();
            long next = (current >= Long.MAX_VALUE ? 0 : current + 1);
            if (i.compareAndSet(current, next)) {
                return current;
            }
        }
    }

    public final long getAndDecrement() {
        for (; ; ) {
            long current = i.get();
            long next = (current <= 0 ? Long.MAX_VALUE : current - 1);
            if (i.compareAndSet(current, next)) {
                return current;
            }
        }
    }

    public final long incrementAndGet() {
        for (; ; ) {
            long current = i.get();
            long next = (current >= Long.MAX_VALUE ? 0 : current + 1);
            if (i.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    public final long decrementAndGet() {
        for (; ; ) {
            long current = i.get();
            long next = (current <= 0 ? Long.MAX_VALUE : current - 1);
            if (i.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    public final long get() {
        return i.get();
    }

    public final void set(long newValue) {
        if (newValue < 0) {
            throw new IllegalArgumentException("new value " + newValue + " < 0");
        }
        i.set(newValue);
    }

    public final long getAndSet(long newValue) {
        if (newValue < 0) {
            throw new IllegalArgumentException("new value " + newValue + " < 0");
        }
        return i.getAndSet(newValue);
    }

    public final long getAndAdd(long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("delta " + delta + " < 0");
        }
        for (; ; ) {
            long current = i.get();
            long next = (current >= Long.MAX_VALUE - delta + 1 ? delta - 1 : current + delta);
            if (i.compareAndSet(current, next)) {
                return current;
            }
        }
    }

    public final long addAndGet(long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("delta " + delta + " < 0");
        }
        for (; ; ) {
            long current = i.get();
            long next = (current >= Long.MAX_VALUE - delta + 1 ? delta - 1 : current + delta);
            if (i.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    public final boolean compareAndSet(long expect, long update) {
        if (update < 0) {
            throw new IllegalArgumentException("update value " + update + " < 0");
        }
        return i.compareAndSet(expect, update);
    }

    public final boolean weakCompareAndSet(long expect, long update) {
        if (update < 0) {
            throw new IllegalArgumentException("update value " + update + " < 0");
        }
        return i.weakCompareAndSet(expect, update);
    }

    public byte byteValue() {
        return i.byteValue();
    }

    public short shortValue() {
        return i.shortValue();
    }

    @Override
    public int intValue() {
        return i.intValue();
    }

    public long longValue() {
        return i.longValue();
    }

    public float floatValue() {
        return i.floatValue();
    }

    public double doubleValue() {
        return i.doubleValue();
    }

    public String toString() {
        return i.toString();
    }

    @Override
    public int hashCode() {
        final long prime = 31;
        long result = 1;
        result = prime * result + ((i == null) ? 0 : i.hashCode());
        return (int) result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AtomicPositiveLong other = (AtomicPositiveLong) obj;
        if (i == null) {
            if (other.i != null)
                return false;
        } else if (i == other.i)
            return false;
        return true;
    }

}