package deep.copy.utils;

public class Pair<T> {
    public T constructor;
    public boolean hasDefaultConstructor;

    public T getConstructor() {
        return constructor;
    }

    public void setConstructor(T constructor) {
        this.constructor = constructor;
    }

    public boolean isHasDefaultConstructor() {
        return hasDefaultConstructor;
    }

    public void setHasDefaultConstructor(boolean hasDefaultConstructor) {
        this.hasDefaultConstructor = hasDefaultConstructor;
    }
}

