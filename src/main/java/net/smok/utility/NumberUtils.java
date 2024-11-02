package net.smok.utility;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class NumberUtils extends Number {

    @Override
    public String toString() {
        return Float.toString(floatValue());
    }

    public static final NumberUtils ZERO = new NumberUtils() {
        @Override
        public int intValue() {
            return 0;
        }

        @Override
        public long longValue() {
            return 0;
        }

        @Override
        public float floatValue() {
            return 0;
        }

        @Override
        public double doubleValue() {
            return 0;
        }
    };

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull NumberUtils add(Number first, Number second) {
        return new NumberUtils() {
            @Override
            public int intValue() {
                return first.intValue() + second.intValue();
            }

            @Override
            public long longValue() {
                return first.longValue() + second.longValue();
            }

            @Override
            public float floatValue() {
                return first.floatValue() + second.floatValue();
            }

            @Override
            public double doubleValue() {
                return first.doubleValue() + second.doubleValue();
            }
        };
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull NumberUtils subtract(Number first, Number second) {
        return new NumberUtils() {
            @Override
            public int intValue() {
                return first.intValue() - second.intValue();
            }

            @Override
            public long longValue() {
                return first.longValue() - second.longValue();
            }

            @Override
            public float floatValue() {
                return first.floatValue() - second.floatValue();
            }

            @Override
            public double doubleValue() {
                return first.doubleValue() - second.doubleValue();
            }
        };
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull NumberUtils multiply(Number first, Number second) {
        return new NumberUtils() {
            @Override
            public int intValue() {
                return first.intValue() * second.intValue();
            }

            @Override
            public long longValue() {
                return first.longValue() * second.longValue();
            }

            @Override
            public float floatValue() {
                return first.floatValue() * second.floatValue();
            }

            @Override
            public double doubleValue() {
                return first.doubleValue() * second.doubleValue();
            }
        };
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull NumberUtils divide(Number first, Number second) {
        return new NumberUtils() {
            @Override
            public int intValue() {
                return first.intValue() / second.intValue();
            }

            @Override
            public long longValue() {
                return first.longValue() / second.longValue();
            }

            @Override
            public float floatValue() {
                return first.floatValue() / second.floatValue();
            }

            @Override
            public double doubleValue() {
                return first.doubleValue() / second.doubleValue();
            }
        };
    }

}
