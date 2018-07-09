package io.agibalov.tracing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SqlTracer {
    private final List<String> queries = new ArrayList<>();

    public void addQuery(String query) {
        queries.add(query);
    }

    public List<String> getQueries() {
        return Collections.unmodifiableList(queries);
    }

    public void clear() {
        queries.clear();
    }
}
