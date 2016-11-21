package kohl.hadrien.vtl.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;

import java.util.Objects;

/**
 * Abstract component implementation.
 */
public abstract class AbstractComponent<T> implements Component<T> {

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(role())
                .add("type", type().getSimpleName())
                .add("name", name())
                .addValue(Optional.fromNullable(get()).transform(Object::toString).or("NULL"))
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                name(),
                type(),
                get()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false; // not class equality || getClass() != o.getClass()) return false;
        Component that = (Component) o;
        return Objects.equals(that.name(), name())
                && Objects.equals(that.type(), type())
                && Objects.equals(that.get(), get());
    }
}
