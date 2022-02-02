package com.epam.esm.gcs.comparator;

import java.util.Comparator;

public interface ComparatorFactory<T> {

    Comparator<T> getComparator(ComparatorType comparatorType);

}
