package com.scottlogic.deg.profile.reader.names;

import java.util.Set;

public interface NamePopulator<T> {

    Set<NameFrequencyHolder> retrieveNames(T config);
}