package com.demo.store.util;

import java.util.function.Supplier;

public class RetryUtils {

    @SafeVarargs
    public static <T> T retry(
            int maxAttempts,
            long backoffMillis,
            Class<? extends Throwable> retryOn,
            Supplier<T> operation,
            Class<? extends Throwable>... allowedExceptions
    ) {
        int attempt = 0;

        while (attempt < maxAttempts) {
            try {
                return operation.get();
            } catch (Throwable e) {
                attempt++;

                // Skip "allowed" exceptions
                for (Class<? extends Throwable> allowed : allowedExceptions) {
                    if (allowed.isInstance(e)) {
                        throw sneakyThrow(e);
                    }
                }

                // No retry-exception → discard immediately
                if (!retryOn.isInstance(e)) {
                    throw sneakyThrow(e);
                }

                // Last attempt failed → throw original exception
                if (attempt >= maxAttempts) {
                    throw sneakyThrow(e);
                }

                try {
                    Thread.sleep(backoffMillis);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }

        throw new IllegalStateException("Unreachable code");
    }

    // allows to throw checked exceptions without wrapper
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> RuntimeException sneakyThrow(Throwable t) throws T {
        throw (T) t;
    }
}
